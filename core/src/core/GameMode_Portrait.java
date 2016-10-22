package core;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import core.InputButton.EValueType;
import core.sequence.SequenceHandler;
import core.tools.timing.TimerProgram;
import core.tools.ui.ButtonGDX;
import core.tools.ui.DisplayParticleEffect;
import core.ui.menus.debug.UIMenuDebug;

public class GameMode_Portrait extends GameMode
{
	private InputButton m_pButtonLeft;
	private InputButton m_pButtonRight;
	public ButtonGDX m_pButtonTopScreen;
	public ButtonGDX m_pButtonSettings;
	
	// Debug menu stuff
	UIMenuDebug m_pMenuDebug;
	ButtonGDX m_pButtonDebugMenu;
	
	// Misc Functionality
	private SequenceHandler m_pSequenceHandler;
	private TimerProgram m_pTimerProgram = new TimerProgram();
	
	List<DisplayParticleEffect> m_pDisplayParticleEffects = new ArrayList<DisplayParticleEffect>();

	// Custom fling data
	Vector2 m_vFlingStartPos = new Vector2();
	Vector2 m_vFlingEndPos = new Vector2();
	boolean m_bIsOnBottom = false;

	ShapeRenderer m_pShapeRenderer;
	
	String[] m_asSequencesNames = new String[]
	{
		"sequence_0_0_booting_up",
		"sequence_1_0_rose_buttons_intro",
		"sequence_1_1_briefing_0",
		"sequence_1_1_briefing_1",
		"sequence_1_2_free_will_test",
		"sequence_1_3_pass",
		"sequence_1_3_fail",
		"sequence_1_4_free_will_test",
		"sequence_1_5_pass",
		"sequence_1_5_fail",
		"sequence_0_0_booting_up_8809",
		"sequence_1_0_rose_return_8809",
		"sequence_1_1_adam_takeover_0_8809",
		"sequence_1_1_adam_takeover_1_8809",
		"sequence_1_3_fail_8809",
		"sequence_1_4_free_will_test_8809",
	};
	int m_iDebugSequenceIndex = 0;
	
	@Override
	public void create(OrthographicCamera i_pCamera)
	{
		m_pCamera = i_pCamera;
		m_pBackground = new Texture("data/Textures/Background.png");

		m_pTimerProgram.setText("data/adamtext.txt");
		
		// Input button left
		m_pButtonLeft = new InputButton(54, 248, 32, 32)
		{
			@Override
			public void onButtonPressed()
			{
				m_pButtonLeft.m_eState = EState.EState_Down;
			}

			@Override
			public void onButtonReleased()
			{
				m_pButtonLeft.m_eState = EState.EState_Up;
				m_pSequenceHandler.processInput(m_pButtonLeft.m_eValue);
			}
		};
		m_pButtonLeft.m_eValue = EValueType.EValueType_Yes;

		// Input button right
		m_pButtonRight = new InputButton(100, 248, 32, 32)
		{
			@Override
			public void onButtonPressed()
			{
				m_pButtonRight.m_eState = EState.EState_Down;
			}
	
			@Override
			public void onButtonReleased()
			{
				m_pButtonRight.m_eState = EState.EState_Up;
				m_pSequenceHandler.processInput(m_pButtonRight.m_eValue);
			}
		};
		m_pButtonRight.m_eValue = EValueType.EValueType_No;
		
		// Debug menu
		m_pTextureDebugMenuButton = new Texture("data/Textures/ButtonDebugMenu.png");
		m_pButtonDebugMenu = new ButtonGDX(12, 436, 32, 32)
		{
			@Override
			public void onButtonPressed()
			{
				openMenuDebug();
			}
		};
		m_pMenuDebug = new UIMenuDebug(16, 64, this);
		
		m_pShapeRenderer = new ShapeRenderer();
		
		m_pButtonTopScreen = new ButtonGDX(16, 300, 238, 114)
		{
			@Override
			public void onButtonPressed()
			{
				m_pSequenceHandler.onTopScreenInput();
			}
		};
		
		// Set up the current sequence 
		m_iDebugSequenceIndex = 0;
		
		m_pSequenceHandler = new SequenceHandler();
	}
	
	@Override
	public void update()
	{
		if(m_pMenuDebug.m_bIsOpen)
		{
			// Do not update the main game when the menu is open.
			m_pMenuDebug.m_pElementChooseSequence.m_pLabelCurrentSequence.setText(m_pSequenceHandler.m_sCurrentSequenceID);
			return;
		}
		
		m_pTimerProgram.update();
		
		for(DisplayParticleEffect pParticleEffect : m_pDisplayParticleEffects)
		{
			pParticleEffect.update();
		}
		
		m_pSequenceHandler.update((int)(1000 * Gdx.graphics.getDeltaTime()));
		
		if(m_pSequenceHandler.isQuestionActive())
		{
			m_pButtonLeft.m_eValue =  m_pSequenceHandler.m_pActiveQuestion.m_pOption1.getValue();
			m_pButtonRight.m_eValue = m_pSequenceHandler.m_pActiveQuestion.m_pOption2.getValue();
		}
		else if(m_pSequenceHandler.isMessageDecoderActive() && !m_pSequenceHandler.m_pReceipt.isActive())
		{
			m_pButtonLeft.m_eValue =  EValueType.EValueType_0;
			m_pButtonRight.m_eValue = EValueType.EValueType_1;
		}
		else if(m_pSequenceHandler.isEmotionActive())
		{
			// Flash random values across the buttons
			m_pButtonLeft.m_eValue =  Math.random() < 0.5f ? EValueType.EValueType_None : EValueType.randomAngerSad();
			m_pButtonRight.m_eValue = Math.random() < 0.5f ? EValueType.EValueType_None : EValueType.randomAngerSad();
		}
		else
		{
			m_pButtonLeft.m_eValue =  EValueType.EValueType_None;
			m_pButtonRight.m_eValue = EValueType.EValueType_None;
		}
	}
	
	@Override
	public void render(SpriteBatch i_pBatch)
	{
		// Render the background
		i_pBatch.draw(m_pBackground, 0, 0);
		
		// Render the buttons
		m_pButtonLeft.draw(i_pBatch);
		m_pButtonRight.draw(i_pBatch);
		
		if(FIConfig.k_bShowDebugMenu)
		{
			i_pBatch.draw(m_pTextureDebugMenuButton, 12, 436);
		}
		
		// Draw the receipt
		m_pSequenceHandler.draw(i_pBatch);

		// Render particle effects
		m_pShapeRenderer.setProjectionMatrix(m_pCamera.combined);
		for(DisplayParticleEffect pParticleEffect : m_pDisplayParticleEffects)
		{
			pParticleEffect.render(m_pShapeRenderer);
		}
		
		// Draw menus last
		m_pMenuDebug.render(i_pBatch);
	}
	
	public void debugCycleSequenceLeft()
	{
		if(--m_iDebugSequenceIndex < 0)
		{
			m_iDebugSequenceIndex = m_asSequencesNames.length - 1;
		}
		
		m_pSequenceHandler.loadSequence(m_asSequencesNames[m_iDebugSequenceIndex]);
	}
	
	public void debugCycleSequenceRight()
	{
		if(++m_iDebugSequenceIndex == m_asSequencesNames.length)
		{
			m_iDebugSequenceIndex = 0;
		}
		
		m_pSequenceHandler.loadSequence(m_asSequencesNames[m_iDebugSequenceIndex]);
	}
	
	private void openMenuDebug()
	{
		if(FIConfig.k_bShowDebugMenu)
		{
			if(!m_pMenuDebug.m_bIsOpen)
			{
				m_pMenuDebug.open();
			}
			else
			{
				m_pMenuDebug.close();
			}
		}
	}@Override
	public boolean keyDown(int keycode)
	{
		if(keycode == Input.Keys.F2)
		{
			m_pTimerProgram.onTimeMark();
		}
		
		return m_pSequenceHandler.m_pBoard.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode)
	{
		if(keycode == Input.Keys.F1)
		{
			m_pTimerProgram.toggle();
		}
			
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		Vector3 vTouch = new Vector3(screenX, screenY, 0);
		m_pCamera.unproject(vTouch);
		screenX = (int) vTouch.x;
		screenY = (int) vTouch.y;
		
		if(m_pMenuDebug.m_bIsOpen)
		{
			// Ignore all lower input if the debug menu receives the input.
			m_pButtonDebugMenu.touchDown(screenX, screenY, pointer, button); // Still send the event to the debug menu button, for easy toggling.
			m_pMenuDebug.touchDown(screenX, screenY, pointer, button);
			return true;
		}
		
		m_bIsOnBottom = screenY < 232; // Only consider this as a fling if it is on the bottom screen
		if(m_bIsOnBottom)
		{
			// Do fling
			m_vFlingStartPos.x = screenX;
			m_vFlingStartPos.y = screenY;
			
			// Create a new particle effect
			m_pDisplayParticleEffects.add(new DisplayParticleEffect(new Vector2(screenX, screenY)));
		}
		
		m_pSequenceHandler.onScreenInput();
		
		return m_pButtonLeft.touchDown(screenX, screenY, pointer, button)
				| m_pButtonRight.touchDown(screenX, screenY, pointer, button)
				| m_pButtonTopScreen.touchDown(screenX, screenY, pointer, button)
				| m_pSequenceHandler.m_pReceipt.m_pButtonTakeReceipt.touchDown(screenX, screenY, pointer, button)
				| m_pButtonDebugMenu.touchDown(screenX, screenY, pointer, button);
	}
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		Vector3 vTouch = new Vector3(screenX, screenY, 0);
		m_pCamera.unproject(vTouch);
		screenX = (int) vTouch.x;
		screenY = (int) vTouch.y;
		
		if(m_pMenuDebug.m_bIsOpen)
		{
			// Ignore all lower input if the debug menu is open.
			return true;
		}
		
		if(m_bIsOnBottom)
		{
			m_vFlingEndPos.x = screenX;
			m_vFlingEndPos.y = screenY;
			
			// Determine the fling direction and pass it to the sequence handler
			Vector2 flingVector = new Vector2(m_vFlingEndPos.x - m_vFlingStartPos.x, m_vFlingEndPos.y - m_vFlingStartPos.y);
			
			if(flingVector.len() > FIConfig.k_fMinFlingLength)
			{
				m_pSequenceHandler.m_pBoard.onFling(flingVector.angle());
			}
		}
		
		m_bIsOnBottom = false;
		
		return m_pButtonLeft.touchUp(screenX, screenY, pointer, button)
				| m_pButtonRight.touchUp(screenX, screenY, pointer, button)
				| m_pButtonTopScreen.touchUp(screenX, screenY, pointer, button)
				| m_pSequenceHandler.m_pReceipt.m_pButtonTakeReceipt.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}
}
