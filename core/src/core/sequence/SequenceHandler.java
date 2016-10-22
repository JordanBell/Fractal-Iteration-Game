package core.sequence;

import java.io.IOException;
import java.util.Calendar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import core.FIConfig;
import core.GameBoard;
import core.InputButton;
import core.Receipt;
import core.tools.assertion.Assertion;
import core.tools.audio.AudioEffectSet;
import core.tools.audio.AudioManager;

class EmotionModeData
{
	boolean m_bActive = false;
	boolean m_bFadedColor = true;
	int m_iStartCharacterIndex = 0;
	int m_iDeltaBufferTimer = 0;
	float m_fFlashAlpha = 1.f;
}

enum ESpeakerID
{
	ESpeakerID_System,
	ESpeakerID_Rose,
	ESpeakerID_Adam;
	
	LabelStyle getLabelStyle()
	{
		switch(this)
		{
			default:
			case ESpeakerID_System:
				return FIConfig.s_pLabelStyleSystem;
			case ESpeakerID_Rose:
				return FIConfig.s_pLabelStyleRose;
			case ESpeakerID_Adam:
				return FIConfig.s_pLabelStyleAdam;
		}
	}
	
	AudioEffectSet getSFXPrint()
	{
		switch(this)
		{
			default:
			case ESpeakerID_System:
				return AudioManager.get().system;
			case ESpeakerID_Rose:
				return AudioManager.get().rose;
			case ESpeakerID_Adam:
				return AudioManager.get().adam;
		}
	}
};

class SpeakerConfig
{
	private ESpeakerID m_eID;
	
	public AudioEffectSet m_pSFXPrint;
	
	void setID(ESpeakerID i_eID)
	{
		if(m_eID != i_eID)
		{
			m_eID = i_eID;
			m_pSFXPrint = m_eID.getSFXPrint();
		}
	}
	
	ESpeakerID getID()
	{
		return m_eID;
	}
}

public class SequenceHandler
{
	SequenceParser pSequenceParser = new SequenceParser();
	private SequenceNode m_pCurrentNode = null;
	
	public StringBuilder m_sText = new StringBuilder();
	private int m_iWaitCount = 0;

	public SequenceNodeQuestion m_pActiveQuestion = null;
	
	private String m_sCurrentTypedWord = "";
	private int m_iCurrentTypedWordTimer = 0;
	
	public Label m_pTopLabel;
	public Receipt m_pReceipt;
	public GameBoard m_pBoard;
	
	private Sprite m_pBottomScreenFlash;
	
	public float m_fAnimationTimer = 0.f;
	public Animation m_pAnimationTapPrompt;

	Preferences m_pPreferences;
	
	private float m_fFlickerOverrideChance = 1.f;
	private boolean m_bFlickerOverrideActive = false;
	
	SpeakerConfig m_pSpeakerConfig = new SpeakerConfig();
	
	EmotionModeData m_pEmotionModeData = new EmotionModeData();
	
	// Persistent Data
	public boolean m_bIsEventPending = false;
	public String m_sCurrentSequenceID = "";
	public long m_lTimeOfEventComplete = 0;
	
	public SequenceHandler()
	{
		m_pBoard = new GameBoard();
		m_pReceipt = new Receipt();
		pSequenceParser = new SequenceParser();

		// Set up the top m_pTopLabel
		m_pTopLabel = new Label("", FIConfig.s_pLabelStyleRose);
		m_pTopLabel.setAlignment(Align.topLeft);
		m_pTopLabel.setEllipsis(false);
		m_pTopLabel.setX(32);
		m_pTopLabel.setY(408);
		m_pTopLabel.setWrap(true);
		m_pTopLabel.setWidth(206);
		
		Texture tapPrompt = new Texture("data/Textures/DownPointer.png");
		m_pAnimationTapPrompt = new Animation(0.5f, new TextureRegion(tapPrompt, 0, 0, 16, 16), new TextureRegion(tapPrompt, 16, 0, 16, 16));
		
		m_pBottomScreenFlash = new Sprite(new Texture(Gdx.files.internal("data/Textures/BottomScreenFlash.png")));
		m_pBottomScreenFlash.setX(54);
		m_pBottomScreenFlash.setY(53);
		
		// Init save data
		m_pPreferences = Gdx.app.getPreferences("FI_Preferences");
		m_pPreferences.clear(); // TODO: This is temporary, we should remove it
		
		// Get the pending event data
		loadData();
	}
	
	// Load all persistent values
	private void loadData()
	{
		try
		{
			// Get the time of the last save
			long lTimeNow = System.currentTimeMillis();
			long lTimeOfLastSaveMilli = m_pPreferences.getLong("saveDateMilli", lTimeNow);
			if(lTimeOfLastSaveMilli < lTimeNow)
			{
				// Handle a time difference since the last save
				long lTimeSinceLastSave = lTimeNow - lTimeOfLastSaveMilli;
				
				long lDay = 86400000l;
				if(lTimeSinceLastSave > lDay)
				{
					// TODO Handle time since last save
					System.out.println("It's been a while.");
				}
			}
			
			// Progress data
			final String sSavedSequenceID = m_pPreferences.getString("currentSequenceID", "sequence_0_0_booting_up");
			final String sSavedNodeID = m_pPreferences.getString("currentNodeID", "");
			
			// Load the current speaker profile
			int speakerProfileIndex = m_pPreferences.getInteger("profileIndex", 0);
			setProfileID(ESpeakerID.values()[speakerProfileIndex]);
			
			// Load the current event ID if it differs
			if(!m_sCurrentSequenceID.equals(sSavedSequenceID))
			{
				loadSequence(sSavedSequenceID, true);
			}
			
			// Skip to the saved node ID if it differs
			if(!m_pCurrentNode.m_sID.equals(sSavedNodeID))
			{
				goToNode(sSavedNodeID);
			}
			
			// Load the saved screen text
			String savedText = m_pPreferences.getString("screenText", "");
			if(!savedText.isEmpty())
			{
				m_sText.append(savedText);
			}
			
			// Load pending event data
			m_bIsEventPending = m_pPreferences.getBoolean("isWaitingForEvent", false);
			m_lTimeOfEventComplete = m_pPreferences.getLong("timeOfEventComplete", 0);
			
			if(m_bIsEventPending)
			{
				// If an event is pending, assert that we are currently on a timer node.
				Assertion.imperative.assertTrue(m_pCurrentNode instanceof SequenceNodeTimer, "Mismatch between these facts: An event is pending; The last saved sequence node is not a timer.");
				SequenceNodeTimer pAsTimer = (SequenceNodeTimer)m_pCurrentNode;
				
				// Update the timer data to reflect the amount of time until the event is complete
				pAsTimer.m_lRemainingMilliseconds = m_lTimeOfEventComplete - System.currentTimeMillis();
				
				// Update the timer's text start index
				pAsTimer.m_iStartTextIndex = m_pPreferences.getInteger("timerTextStartIndex", 0);
				
				// Print the timer's time
				m_sText.append(pAsTimer.toString());
			}
		}
		catch(RuntimeException e)
		{
			System.err.println(e.getMessage());
			System.out.println("Clearing save data.");
			
			m_pPreferences.clear();
			m_pPreferences.flush();
			
			// Reload
			loadData();
		}
	}
	
	// Save all persistent values
	private void saveData()
	{
		// Save the time of this save
		m_pPreferences.putLong("saveDateMilli", System.currentTimeMillis());
		
		m_pPreferences.putBoolean("isWaitingForEvent", m_bIsEventPending);
		if(m_bIsEventPending)
		{
			m_pPreferences.putLong("timeOfEventComplete", m_lTimeOfEventComplete);
			
			if(m_pCurrentNode != null && m_pCurrentNode instanceof SequenceNodeTimer)
			{
				SequenceNodeTimer pAsTimer = (SequenceNodeTimer)m_pCurrentNode;
				m_pPreferences.putInteger("timerTextStartIndex", pAsTimer.m_iStartTextIndex);
			}
		}
		
		// Progress data
		m_pPreferences.putString("currentSequenceID", m_sCurrentSequenceID);
		m_pPreferences.putString("currentNodeID", m_pCurrentNode.m_sID);
		
		// Save the text on the screen
		String savedText = "";  // By default, do not save any text
		if(m_pCurrentNode != null && m_pCurrentNode instanceof SequenceNodeTimer)
		{
			savedText = m_sText.substring(0, ((SequenceNodeTimer)m_pCurrentNode).m_iStartTextIndex);
		}
		m_pPreferences.putString("screenText", savedText);
		
		// Save the current speaker profile
		m_pPreferences.putInteger("profileIndex", m_pSpeakerConfig.getID().ordinal());
		
		// Flush the save data
		m_pPreferences.flush();
	}
	
	public boolean isEmotionActive()
	{
		return m_pEmotionModeData.m_bActive;
	}
	
	public float getTopRenderTextAlpha()
	{
		if(m_bFlickerOverrideActive)
		{
			return (float)(Math.random() * m_fFlickerOverrideChance + (1.f - m_fFlickerOverrideChance));
		}
		
		if(!m_pEmotionModeData.m_bActive)
		{
			if(m_pSpeakerConfig.getID() == ESpeakerID.ESpeakerID_Adam)
			{
				// Adam always flickers a bit
				return (float)(Math.random() * 0.5f + 0.5f);
			}
			
			return 1.f;
		}
		
		if(m_pCurrentNode instanceof SequenceNodeWait || (!m_sCurrentTypedWord.isEmpty() && m_sCurrentTypedWord.charAt(0) == ' '))
		{
			return (float)(Math.random() * 0.75f + 0.25f);
		}

		return (float)Math.pow(Math.random(), 5.0);
	}
	
	private int getTextNodeTime()
	{
		return (int) ((30 + (Math.random() * 10)) / ((SequenceNodeText)m_pCurrentNode).m_fSpeedModifier);
	}
	
	public void loadSequence(String i_sSequenceName, boolean i_bSuppressSave)
	{
		SequenceNode pTestSequence = null;
		
		try
		{
			pTestSequence = pSequenceParser.parseFile("data/XML/Gameplay/" + i_sSequenceName + ".xml");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		reset();
		m_sCurrentSequenceID = i_sSequenceName;
		handle(pTestSequence, i_bSuppressSave);
	}
	
	public void loadSequence(String i_sSequenceName)
	{
		loadSequence(i_sSequenceName, false);
	}
	
	public void handle(SequenceNode i_pRoot, boolean i_pSuppressSave)
	{
		if(i_pRoot == null)
		{
			return;
		}
		
		if(m_pCurrentNode != null)
		{
			reset();
		}
		
		onNodeStart(i_pRoot, i_pSuppressSave);
	}
	
	public void handle(SequenceNode i_pRoot)
	{
		handle(i_pRoot, false);
	}
	
	void clearText()
	{
		m_sText.delete(0, m_sText.length());
	}
	
	void reset()
	{
		clearText();
		m_sCurrentTypedWord = "";
		m_pActiveQuestion = null;
		m_pCurrentNode = null;
		m_pEmotionModeData.m_bActive = false;
	}
	
	public void processInput(InputButton.EValueType i_eValueType)
	{
		if(isQuestionActive())
		{
			if(m_pActiveQuestion.m_pOption1.m_eValueType == i_eValueType)
			{
				next(m_pActiveQuestion.m_pOption1);
				m_sText.append(i_eValueType.toString() + "\n");
			}
			else if(m_pActiveQuestion.m_pOption2.m_eValueType == i_eValueType)
			{
				next(m_pActiveQuestion.m_pOption2);
				m_sText.append(i_eValueType.toString() + "\n");
			}
			else
			{
				Assertion.warning.triggerWith("Unrecognised option: " + i_eValueType);
			}
		}
		else if(m_pCurrentNode instanceof SequenceNodeMessageDecoder)
		{
			if(m_pReceipt.isActive())
			{
				// Ignore input once a receipt has been printed
				return;
			}
			
			SequenceNodeMessageDecoder pAsMessageDecoder = (SequenceNodeMessageDecoder)m_pCurrentNode;
			
			m_sText.append(i_eValueType.toString());
			pAsMessageDecoder.m_sInput += (i_eValueType.toString());
			
			if(pAsMessageDecoder.m_sInput.length() == 4)
			{
				// Code has been fully entered.
				m_sText.append('\n');
				
				// Enter the code into the decoder, and print a receipt containing the result.
				m_pReceipt.printReceipt(pAsMessageDecoder.enterCode());

				// If not fully decoded, reset and loop. TODO: Do this once the receipt is dismissed.
				if(pAsMessageDecoder.m_bIsDecoded)
				{
					m_sText.append("Fully decoded.\n");
				}
				else
				{
					// Add a new input prompt
					m_sText.append("Partially decoded. Read and try again.\n");
				}
			}
		}
	}
	
	public void next(SequenceNodeOption i_pSelectedOption)
	{
		m_iWaitCount = 0;
		m_pActiveQuestion = null;
		onNodeStart(i_pSelectedOption.m_pNestedNode != null ? i_pSelectedOption.m_pNestedNode : i_pSelectedOption.getNext());
	}
	
	public void next(SequenceNode i_pNextNode)
	{
		m_iWaitCount = 0;
		m_pActiveQuestion = null;
		onNodeStart(i_pNextNode);
	}
	
	public void next()
	{
		m_iWaitCount = 0;
		m_pActiveQuestion = null;
		onNodeStart(m_pCurrentNode.getNext());
	}
	
	private void goToNode(final String i_sNodeID)
	{
		if(i_sNodeID.isEmpty())
		{
			Assertion.warning.triggerWith("Attempting to go to a node with an empty ID as argument.");
			return;
		}
		
		Assertion.imperative.assertNonNull(m_pCurrentNode, "Cannot skip to a node when the current node is null. Load a sequence before skipping to a new node.");
		
		SequenceNode pNodeTraverser = m_pCurrentNode.getNext();
		
		while(pNodeTraverser != null)
		{
			if(!pNodeTraverser.m_sID.isEmpty() && pNodeTraverser.m_sID.equals(i_sNodeID))
			{
				onNodeStart(pNodeTraverser, true);
				return;
			}
			
			pNodeTraverser = pNodeTraverser.getNext();
		}
	}
	
	private void onNodeStart(SequenceNode i_pNewNode)
	{
		onNodeStart(i_pNewNode, false);
	}
	
	private void setProfileID(ESpeakerID i_eSpeakerID)
	{
		m_pSpeakerConfig.setID(ESpeakerID.ESpeakerID_System);
		
		// Update the label's style to reflect the speaker
		m_pTopLabel.setStyle(m_pSpeakerConfig.getID().getLabelStyle());
	}
	
	private void onNodeStart(SequenceNode i_pNewNode, boolean i_bSuppressSave)
	{
		m_pCurrentNode = i_pNewNode;
		
		m_bIsEventPending = false; // Until proven true
		
		if(m_pCurrentNode == null)
		{
			return;
		}
		
		// Handle the new node type
		if (m_pCurrentNode instanceof SequenceNodeQuestion)
		{
			if(!isQuestionActive())
			{
				SequenceNodeQuestion pAsQuestion = (SequenceNodeQuestion)m_pCurrentNode;
				
				// Add an input prompt to the m_sText
				m_sText.append("> ");
				
				m_pActiveQuestion = pAsQuestion;
			}
		}
		else if (m_pCurrentNode instanceof SequenceNodeText)
		{
			m_iCurrentTypedWordTimer = getTextNodeTime();
			
			// If a flicker override chance is defined, activate the override.
			SequenceNodeText pAsText = (SequenceNodeText)m_pCurrentNode;
			m_fFlickerOverrideChance = pAsText.m_fFlickerChance;
			m_bFlickerOverrideActive = pAsText.m_fFlickerChance > 0.0f;
			
			if(((SequenceNodeText)m_pCurrentNode).m_sText.isEmpty())
			{
				next();
			}
		}
		else if (m_pCurrentNode instanceof SequenceNodeMessageDecoder)
		{
			// Add an input prompt to the m_sText
			m_sText.append("> ");
		}
		else if (m_pCurrentNode instanceof SequenceNodeLayout)
		{
			SequenceNodeLayout pAsLayout = (SequenceNodeLayout)m_pCurrentNode;
			
			String sLayoutFilename = "data/Layouts/" + pAsLayout.m_sLayoutName + ".txt"; 
			m_pBoard.loadLayout(sLayoutFilename);
		}
		else if (m_pCurrentNode instanceof SequenceNodeLink)
		{
			// Load the next sequence.
			SequenceNodeLink asLink = (SequenceNodeLink)(m_pCurrentNode);
			loadSequence(asLink.m_sSequenceName);
		}
		else if (m_pCurrentNode instanceof SequenceNodeClearScreenImmediate)
		{
			clearText();
			next();
		}
		else if (m_pCurrentNode instanceof SequenceNodeClearScreenWait)
		{
			m_fAnimationTimer = 0.f;
		}
		else if(m_pCurrentNode instanceof SequenceNodeReceipt)
		{
			// Print a new receipt.
			SequenceNodeReceipt pNodeAsReceipt = (SequenceNodeReceipt)(m_pCurrentNode);
			m_pReceipt.printReceipt(pNodeAsReceipt.m_sText.toString());
		}
		else if (m_pCurrentNode instanceof SequenceNodeTimer)
		{
			// An event is pending if the current node is a timer
			m_bIsEventPending = true;
			
			SequenceNodeTimer pAsTimer = (SequenceNodeTimer)m_pCurrentNode;
			
			pAsTimer.m_iStartTextIndex = m_sText.length();
			
			// Save the time of completion
			m_lTimeOfEventComplete = System.currentTimeMillis() + pAsTimer.m_lRemainingMilliseconds;
		}
		else if (m_pCurrentNode instanceof SequenceNodeScreenFlash)
		{
			SequenceNodeScreenFlash pAsFlash = (SequenceNodeScreenFlash)m_pCurrentNode;
			m_sText.append(pAsFlash.m_sText);
		}
		else if (m_pCurrentNode instanceof SequenceNodeGenericMarker)
		{
			SequenceNodeGenericMarker pAsMarker = (SequenceNodeGenericMarker)m_pCurrentNode;
			
			String sMarkerTypeID = pAsMarker.m_sTypeID;
			
			if(sMarkerTypeID.equalsIgnoreCase("emotion_toggle"))
			{
				// Toggle the emotion mode
				m_pEmotionModeData.m_bActive = !m_pEmotionModeData.m_bActive;
				
				if(m_pEmotionModeData.m_bActive)
				{
					// If activated, save the current length of the string at the point of activation
					m_pEmotionModeData.m_iStartCharacterIndex = Math.max(0, m_sText.length());
				}
				else
				{
					// If deactivated, remove all words spoken during the emotion stage
					m_sText.delete(m_pEmotionModeData.m_iStartCharacterIndex, m_sText.length());
				}
				
				next();
			}
			else if(sMarkerTypeID.contains("profile"))
			{
				// Set the profile type
				if(sMarkerTypeID.equalsIgnoreCase("profile_rose"))
				{
					setProfileID(ESpeakerID.ESpeakerID_Rose);
				}
				else if(sMarkerTypeID.equalsIgnoreCase("profile_adam"))
				{
					setProfileID(ESpeakerID.ESpeakerID_Adam);
				}
				else
				{
					setProfileID(ESpeakerID.ESpeakerID_System);
				}
				
				next();
			}
			else if(sMarkerTypeID.equals("voiceover"))
			{
				// Play the file within the voiceover node
				Music pVoiceoverAudio = Gdx.audio.newMusic(Gdx.files.internal("data/Audio/Voiceover/" + pAsMarker.m_sText));
				pVoiceoverAudio.play();
				
				next();
			}
			else if(sMarkerTypeID.contains("ti"))
			{
				// "Text Instant" - Print the text immediately without the "typing" effect over time.
				m_sText.append(pAsMarker.m_sText);
				next();
			}
			else if(sMarkerTypeID.contains("checkpoint"))
			{
				if(!i_bSuppressSave)
				{
					// Save, move on
					saveData();
					next();
				}
			}
			else if(sMarkerTypeID.contains("await_input"))
			{
				// Simply, wait
			}
			else
			{
				Assertion.warning.triggerWith("Unrecognised generic marker with ID: " + sMarkerTypeID);
			}
		}
		
		// If the current node is a checkpoint, handle that now.
		if(!i_bSuppressSave && m_pCurrentNode.m_bIsCheckpoint)
		{
			saveData();
		}
	}
	
	private void typeNextCharacter()
	{
		// If the next word will be on a newline after added, add the newline now
		int iIndexWordEnd = m_sCurrentTypedWord.indexOf(' ') + 1;
		{
			if(iIndexWordEnd == 0)
			{
				iIndexWordEnd = m_sCurrentTypedWord.indexOf('/') + 1;

				if(iIndexWordEnd == 0)
				{
					iIndexWordEnd = m_sCurrentTypedWord.indexOf('.') + 1;

					if(iIndexWordEnd == 0)
					{
						iIndexWordEnd = m_sCurrentTypedWord.indexOf('?') + 1;

						if(iIndexWordEnd == 0)
						{
							iIndexWordEnd = m_sCurrentTypedWord.indexOf('!') + 1;

							if(iIndexWordEnd == 0)
							{
								iIndexWordEnd = m_sCurrentTypedWord.length();
							}
						}
					}
				}
			}
		}
		
		String sCurrentTypedWord = m_sCurrentTypedWord.substring(0, iIndexWordEnd);
		
		// Check if adding this word will overflow the current line. If so, add the newline now
		if(!sCurrentTypedWord.isEmpty())
		{
			m_pTopLabel.layout();
			int iCurrentRuns = m_pTopLabel.getGlyphLayout().runs.size;
			String sCurrentText = m_pTopLabel.getText().toString();
			m_pTopLabel.setText(sCurrentText + sCurrentTypedWord);
			m_pTopLabel.layout();
			int iRunsAfterWord = m_pTopLabel.getGlyphLayout().runs.size;

			final boolean bSucceedingNewline = !sCurrentText.isEmpty() && sCurrentText.charAt(sCurrentText.length() - 1) == '\n';
			final boolean bIsStart = iCurrentRuns == 0;
			if(!bSucceedingNewline && !bIsStart)
			{
				final boolean bWillOverflowOnce = iRunsAfterWord == iCurrentRuns + 1;
				final boolean bWillOverflowTwice = iRunsAfterWord == iCurrentRuns + 2;
				if(bWillOverflowOnce || (bWillOverflowTwice && sCurrentTypedWord.contains("\n")))
				{
					m_sText.append('\n');
				}
			}
			m_pTopLabel.setText(sCurrentText);
			m_pTopLabel.layout();
		}
		
		// Type the next character
		if (m_sCurrentTypedWord.charAt(0) == '\\' && m_sCurrentTypedWord.length() >= 2)
		{
			// Handle escape characters (do both characters at the same time)
			m_sText.append(m_sCurrentTypedWord.substring(0, 2));
			m_sCurrentTypedWord = m_sCurrentTypedWord.substring(2, m_sCurrentTypedWord.length());
		}
		else
		{
			char cNext = m_sCurrentTypedWord.charAt(0);
			
			// Play the character type sfx if the character is not whitespace
			if(!Character.isWhitespace(cNext))
			{
				m_pSpeakerConfig.m_pSFXPrint.play();
			}
			
			m_sText.append(cNext);
			
			// Remove the front character from the word. If it is now empty, we have finished typing it.
			m_sCurrentTypedWord = m_sCurrentTypedWord.substring(1, m_sCurrentTypedWord.length());
		}

		if(m_sCurrentTypedWord.isEmpty())
		{
			next();
		}
		else
		{
			// Set the time until next character is written. Divide this time by the speed modifier attached to the m_sText node increase/decrease the time until the next letter accordingly.
			m_iCurrentTypedWordTimer = getTextNodeTime();
		}
	}
	
	public void update(int i_iDelta)
	{
		// Update the label
		m_pTopLabel.setText(m_sText.toString());
		m_pTopLabel.getColor().a = getTopRenderTextAlpha();
		
		// Update the board
		m_pBoard.update(i_iDelta);
		
		if(m_pCurrentNode == null)
		{
			// Clear the screen
			reset();
		}
		
		// Update bottom screen flash alpha
		if(m_pCurrentNode instanceof SequenceNodeScreenFlash)
		{
			SequenceNodeScreenFlash pAsFlash = (SequenceNodeScreenFlash)m_pCurrentNode;
			
			// Increase the wait counter
			m_iWaitCount += i_iDelta;
			m_pBottomScreenFlash.setAlpha(pAsFlash.getFlashAlpha(m_iWaitCount));
			if(m_iWaitCount >= pAsFlash.m_iTimeUntilNext)
			{
				next();
			}
		}
		else
		{
			m_pBottomScreenFlash.setAlpha(0.f);
		}
		
		if(m_pCurrentNode instanceof SequenceNodeWait)
		{
			SequenceNodeWait pAsWait = (SequenceNodeWait)m_pCurrentNode;
			
			// Increase the wait counter
			m_iWaitCount += i_iDelta;
			if(m_iWaitCount >= pAsWait.m_iWaitMilliseconds)
			{
				// Add a space character if the wait was mid-sentence.
				if(m_sText.length() > 0)
				{
					if(m_sText.charAt(m_sText.length() - 1) != '\n')
					{
						m_sText.append(' ');
					}
				}
				next();
			}
		}
		else if (m_pCurrentNode instanceof SequenceNodeText)
		{
			if(m_sCurrentTypedWord.isEmpty())
			{
				m_sCurrentTypedWord = ((SequenceNodeText)m_pCurrentNode).m_sText;
			}
			else
			{
				if(m_iCurrentTypedWordTimer < 0)
				{
					typeNextCharacter();
				}
				
				// Type the word
				m_iCurrentTypedWordTimer -= i_iDelta;
			}
		}
		else if (m_pCurrentNode instanceof SequenceNodeLayout)
		{
			if(!m_pBoard.isActive())
			{
				// Layout is no longer active. Therefore it must be over.
				next();
			}
		}
		else if (m_pCurrentNode instanceof SequenceNodeReceipt)
		{
			if(m_pReceipt.m_bIsProcessed)
			{
				// No longer waiting for input, therefore we are done with the receipt.
				next();
			}
		}
		else if (m_pCurrentNode instanceof SequenceNodeQuestion)
		{
			SequenceNodeQuestion pAsQuestion = (SequenceNodeQuestion)m_pCurrentNode;
			
			if(pAsQuestion.m_pNestedSilenceNode != null)
			{
				pAsQuestion.m_fSilenceActivationTime -= Gdx.graphics.getDeltaTime();
				if(pAsQuestion.m_fSilenceActivationTime <= 0.f)
				{
					next(pAsQuestion.m_pNestedSilenceNode);
				}
			}
		}
		else if (m_pCurrentNode instanceof SequenceNodeTimer)
		{
			SequenceNodeTimer pAsTimer = (SequenceNodeTimer)m_pCurrentNode;
			
			// Update the timer
			pAsTimer.m_lRemainingMilliseconds -= (long)(Gdx.graphics.getDeltaTime() * 1000);
			if(pAsTimer.m_lRemainingMilliseconds <= 0L)
			{
				next();
			}
			else
			{
				// Print the timer to the top label m_sText
				m_sText.delete(pAsTimer.m_iStartTextIndex, m_sText.length());
				m_sText.append(pAsTimer.toString());
			}
		}
	}
	
	public void onScreenInput()
	{
		// Close the receipt and do stuff according to the current node
		if(m_pReceipt.m_bIsFullVisible)
		{
			// Dismiss it and move on.
			m_pReceipt.onReceiptClosed();
			
			if(m_pCurrentNode instanceof SequenceNodeReceipt)
			{
				next();
			}
			else if (m_pCurrentNode instanceof SequenceNodeMessageDecoder)
			{
				SequenceNodeMessageDecoder pAsMessageDecoder = (SequenceNodeMessageDecoder)m_pCurrentNode;
				if(pAsMessageDecoder.m_bIsDecoded)
				{
					next();
				}
				else
				{
					// Add a new input prompt
					clearText();
					m_sText.append("Please enter binary pass code:\n");
					m_sText.append("> ");
				}
			}
		}
		else if(m_pCurrentNode instanceof SequenceNodeGenericMarker)
		{
			SequenceNodeGenericMarker pAsMarker = (SequenceNodeGenericMarker)m_pCurrentNode;
			if(pAsMarker.m_sTypeID.equals("await_input"))
			{
				next();
			}
		}
	}

	public void onTopScreenInput()
	{
		if(m_pCurrentNode instanceof SequenceNodeClearScreenWait)
		{
			clearText();
			next();
		}
	}
	
	public boolean isQuestionActive()
	{
		return (m_pActiveQuestion != null);
	}

	public boolean isMessageDecoderActive()
	{
		return m_pCurrentNode instanceof SequenceNodeMessageDecoder;
	}

	public void draw(SpriteBatch m_pBatch)
	{
		m_pTopLabel.draw(m_pBatch, 1.f);
		
		if(m_pCurrentNode instanceof SequenceNodeClearScreenWait)
		{
			m_fAnimationTimer += Gdx.graphics.getDeltaTime();
			m_pBatch.draw(m_pAnimationTapPrompt.getKeyFrame(m_fAnimationTimer, true), 222, 308);
		}
		
		m_pBottomScreenFlash.draw(m_pBatch);
		
		// Render the board
		m_pBoard.render(m_pBatch, 62, 190);
		
		// Draw the receipt
		m_pReceipt.draw(m_pBatch);
	}
}
