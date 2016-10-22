package core.sequence;

import core.tools.assertion.Assertion;

public class SequenceNodeReceipt extends SequenceNode
{
	static final int k_iCharacterWidth = 25;
	
	StringBuilder m_sText = new StringBuilder();
	
	public static String toTitleFormat(String i_sText)
	{
		int frontPaddingSize = (k_iCharacterWidth - i_sText.length()) / 2;
		Assertion.warning.assertFalse(frontPaddingSize < 0);
		
		StringBuilder asTitle = new StringBuilder();
		
		for(int i = 0; i < frontPaddingSize; ++i)
		{
			asTitle.append(' ');
		}
		
		asTitle.append(i_sText);
		asTitle.append('\n');
		asTitle.append('\n');
		
		return asTitle.toString();
	}
	
	public static String getLine()
	{
		StringBuilder sLineCharacters = new StringBuilder();

		for(int i = 0; i < k_iCharacterWidth; ++i)
		{
			sLineCharacters.append('*');
		}
		sLineCharacters.append('\n');
		sLineCharacters.append('\n');
		
		return sLineCharacters.toString();
	}
	
	void addTitle(String i_sTitle)
	{
		m_sText.append(toTitleFormat(i_sTitle));
	}
	
	void addLine()
	{
		m_sText.append(getLine());
	}
	
	void addText(String i_sText)
	{
		m_sText.append(i_sText);
		m_sText.append('\n');
	}
}
