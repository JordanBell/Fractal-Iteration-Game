package core.sequence;

import core.tools.assertion.Assertion;

public class SequenceNodeMessageDecoder extends SequenceNode
{
	String m_sInput;
	String m_sCode;
	String m_sMessageUnscrambled;
	String m_sMessageScrambleLevel1;
	String m_sMessageScrambleLevel2;
	String m_sMessageScrambleLevel3;
	String m_sMessageScrambleLevel4;
	boolean m_bIsDecoded;
	
	SequenceNodeMessageDecoder(	String i_sCode,
								String i_sMessageUnscrambled,
								String i_sMessageScrambleLevel1,
								String i_sMessageScrambleLevel2,
								String i_sMessageScrambleLevel3,
								String i_sMessageScrambleLevel4)
	{
		Assertion.warning.assertTrue(i_sCode.length() == 4);
		
		m_sCode = i_sCode;
		m_sMessageUnscrambled = i_sMessageUnscrambled;
		m_sMessageScrambleLevel1 = i_sMessageScrambleLevel1;
		m_sMessageScrambleLevel2 = i_sMessageScrambleLevel2;
		m_sMessageScrambleLevel3 = i_sMessageScrambleLevel3;
		m_sMessageScrambleLevel4 = i_sMessageScrambleLevel4;
		
		m_sInput = "";
		m_bIsDecoded = false;
	}
	
	public String enterCode()
	{
		int iMatchCount = 0;
		
		// Count the number of character matches
		for(int i = 0 ; i < Math.min(m_sInput.length(), m_sCode.length()); ++i)
		{
			if(m_sInput.charAt(i) == m_sCode.charAt(i))
			{
				++iMatchCount;
			}
		}
		
		// Clear the input value.
		m_sInput = "";
		
		// Determine whether the code was successful
		m_bIsDecoded = (iMatchCount == m_sCode.length());
		
		return getMessageByMatch(iMatchCount);
	}
	
	/** Get the message corresponding to a number of matches. */
	private String getMessageByMatch(int i_iMatchCount)
	{
		StringBuilder message = new StringBuilder();
		message.append(SequenceNodeReceipt.toTitleFormat("Code match: " + i_iMatchCount + "/" + m_sCode.length()));
		message.append(SequenceNodeReceipt.getLine());
		
		switch (i_iMatchCount)
		{
			case 0: message.append(m_sMessageScrambleLevel4); break;
			case 1: message.append(m_sMessageScrambleLevel3); break;
			case 2: message.append(m_sMessageScrambleLevel2); break;
			case 3: message.append(m_sMessageScrambleLevel1); break;
			case 4: message.append(m_sMessageUnscrambled); break;
			
			default: Assertion.warning.triggerWith("Unrecognised decoder match number."); break;
		}
		
		return message.toString();
	}

	public static String generateRandomCode()
	{
		StringBuilder sCode = new StringBuilder();
		
		for(int i = 0; i < 4; ++i)
		{
			if(Math.random() < 0.5f)
			{
				sCode.append('0');
			}
			else
			{
				sCode.append('1');
			}
		}
		
		return sCode.toString();
	}
}
