package core.sequence;

public class SequenceNodeText extends SequenceNode
{
	String m_sText = "";
	
	// Optional Attributes
	float m_fSpeedModifier = 1.f;
	float m_fFlickerChance = 0.f;
	
	SequenceNodeText(String i_sText)
	{
		m_sText = i_sText;
	}
}
