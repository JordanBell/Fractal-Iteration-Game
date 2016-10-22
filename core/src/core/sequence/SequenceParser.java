package core.sequence;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;

import core.FIConfig;
import core.InputButton.EValueType;
import core.tools.assertion.Assertion;

public class SequenceParser
{
	XmlReader reader = new XmlReader();
	
	public SequenceNode parseFile(String i_sFilePath) throws IOException
	{
		XmlReader.Element pRoot = reader.parse(Gdx.files.internal(i_sFilePath));
		
		return parseSequence(pRoot);
	}
	
	public SequenceNode parseSequence(XmlReader.Element i_pNode)
	{
		String sNodeName = i_pNode.getName();
		Assertion.imperative.assertTrue(sNodeName.equals("sequence"));
		
		return parseSequenceChildren(i_pNode);
	}
	
	public SequenceNode parseSequenceChildren(XmlReader.Element i_pNode)
	{
		if(i_pNode.getChildCount() == 0)
		{
			return null;
		}
		
		// Serialise the children while parsing them as sequence nodes
		SequenceNode pRoot = parseSequenceNode(i_pNode.getChild(0));
		if(i_pNode.getChildCount() > 1)
		{
			SequenceNode pNode = pRoot;
			for (int index_next = 1; index_next < i_pNode.getChildCount(); ++index_next)
			{
				pNode.setNext(parseSequenceNode(i_pNode.getChild(index_next)));
				pNode = pNode.getNext();
			}
		}
		
		return pRoot;
	}
	
	public SequenceNode parseSequenceNode(XmlReader.Element i_pNode)
	{
		SequenceNode r_pParsedNode = parseSequenceNodeByName(i_pNode);
		
		// Parse an ID, if one exists.
		r_pParsedNode.m_sID = i_pNode.getAttribute("id", r_pParsedNode.m_sID);
		
		return r_pParsedNode;
	}

	public SequenceNode parseSequenceNodeByName(XmlReader.Element i_pNode)
	{
		String sNodeName = i_pNode.getName();
		if(sNodeName.equals("t"))
		{
			// Text
			String sText = i_pNode.getText();
			if(sText == null)
			{
				sText = "";
			}
			sText = sText.replace("\\n", "\n");
			SequenceNodeText pSequenceNodeText = new SequenceNodeText(sText);
			pSequenceNodeText.m_fSpeedModifier = i_pNode.getFloatAttribute("speed", 1.f);
			pSequenceNodeText.m_fFlickerChance = i_pNode.getFloatAttribute("flicker", 0.f);
			return pSequenceNodeText;
		}
		else if(sNodeName.equals("w"))
		{
			// Wait
			return new SequenceNodeWait(Integer.parseInt(i_pNode.getText()));
		}
		else if (sNodeName.equals("c"))
		{
			return new SequenceNodeClearScreenImmediate();
		}
		else if (sNodeName.equals("cw"))
		{
			return new SequenceNodeClearScreenWait();
		}
		else if (sNodeName.equals("play"))
		{
			return new SequenceNodeLayout(i_pNode.getText());
		}
		else if (sNodeName.equals("next"))
		{
			return new SequenceNodeLink(i_pNode.getText());
		}
		else if (sNodeName.equals("q"))
		{
			// Parse the option children
			// Get the two option attribute values
			String sOption1Value = i_pNode.getAttribute("o1");
			String sOption2Value = i_pNode.getAttribute("o2");
			
			SequenceNodeOption pOption1 = null;
			SequenceNodeOption pOption2 = null;
			
			SequenceNode pNestedSilenceNode = null;
			float fSilenceResponseActivationTime = 0.f;
			
			for(int i = 0; i < i_pNode.getChildCount(); ++i)
			{
				XmlReader.Element pChildElement = i_pNode.getChild(i);
				
				if(pChildElement.getName().equals("o1"))
				{
					pOption1 = new SequenceNodeOption(EValueType.fromString(sOption1Value), parseSequenceChildren(pChildElement));
				}
				else if(pChildElement.getName().equals("o2"))
				{
					pOption2 = new SequenceNodeOption(EValueType.fromString(sOption2Value), parseSequenceChildren(pChildElement));
				}
				else if(pChildElement.getName().equals("silence_response"))
				{
					fSilenceResponseActivationTime = pChildElement.getFloatAttribute("activation_time");
					pNestedSilenceNode = parseSequenceChildren(pChildElement);
				}
			}
			
			// If no options have been set, initialise them as empty options
			if(pOption1 == null)
			{
				pOption1 = new SequenceNodeOption(EValueType.fromString(sOption1Value), null);
			}
			
			if(pOption2 == null)
			{
				pOption2 = new SequenceNodeOption(EValueType.fromString(sOption2Value), null);
			}

			return new SequenceNodeQuestion(pOption1, pOption2, pNestedSilenceNode, fSilenceResponseActivationTime);
		}
		else if (sNodeName.equals("receipt"))
		{
			// Handle receipt data
			return parseReceipt(i_pNode);
		}
		else if (sNodeName.equals("message_decoder"))
		{
			return parseMessageDecoder(i_pNode);
		}
		else if (sNodeName.equals("timer"))
		{
			SequenceNodeTimer pTimerNode = new SequenceNodeTimer();

			pTimerNode.addSeconds(i_pNode.getIntAttribute("seconds", 0));
			pTimerNode.addMinutes(i_pNode.getIntAttribute("minutes", 0));
			pTimerNode.addHours(i_pNode.getIntAttribute("hours", 0));
			pTimerNode.addDays(i_pNode.getIntAttribute("days", 0));
			
			// Set a default timer id
			pTimerNode.m_sID = "timer";
			
			return pTimerNode;
		}
		else if (sNodeName.equals("audio_syllable"))
		{
			SequenceNodeScreenFlash pFlashNode = new SequenceNodeScreenFlash(i_pNode.getIntAttribute("time_until_next"), i_pNode.getText());
			return pFlashNode;
		}
		else if (sNodeName.length() > 0)
		{
			// Treat this as a named generic sequence marker
			SequenceNodeGenericMarker pNode = new SequenceNodeGenericMarker(sNodeName, i_pNode.getText());
			pNode.m_sID = pNode.m_sTypeID;
			return pNode;
		}
		
		return null;
	}
	
	public SequenceNodeReceipt parseReceipt(XmlReader.Element i_pNode)
	{
		SequenceNodeReceipt r_pReceiptNode = new SequenceNodeReceipt();
		
		// Check children for data		
		for (int i = 0; i < i_pNode.getChildCount(); ++i)
		{
			XmlReader.Element pChildNode = i_pNode.getChild(i);
			String childName = pChildNode.getName();
			
			if(childName.equals("line"))
			{
				r_pReceiptNode.addLine();
			}
			else
			{
				String sContent = pChildNode.getText();
				sContent = sContent.replace("\\n", "\n");
				
				if(childName.equals("title"))
				{
					r_pReceiptNode.addTitle(sContent);
				}
				else if(childName.equals("t"))
				{
					r_pReceiptNode.addText(sContent);
				}
			}
		}
		
		return r_pReceiptNode;
	}
	
	public SequenceNodeMessageDecoder parseMessageDecoder(XmlReader.Element i_pNode)
	{		
		Assertion.imperative.assertTrue(i_pNode.getChildCount() == 5, "There must be exactly 5 children in a message_decoder node.");
		
		String sMessageUnscrambled = i_pNode.getChild(4).getText();
		String sMessageScrambleLevel1 = i_pNode.getChild(3).getText();
		String sMessageScrambleLevel2 = i_pNode.getChild(2).getText();
		String sMessageScrambleLevel3 = i_pNode.getChild(1).getText();
		String sMessageScrambleLevel4 = i_pNode.getChild(0).getText();
		
		String sCode;
		if(FIConfig.k_bUseRandomDecoderCodes)
		{
			sCode = SequenceNodeMessageDecoder.generateRandomCode();
		}
		else
		{
			sCode = "0000";
		}
		
		return new SequenceNodeMessageDecoder(sCode, sMessageUnscrambled, sMessageScrambleLevel1, sMessageScrambleLevel2, sMessageScrambleLevel3, sMessageScrambleLevel4);
	}
}
