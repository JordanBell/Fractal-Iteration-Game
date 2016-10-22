package core.sequence;

public class SequenceNodeLayout extends SequenceNode
{
	String m_sLayoutName = "";
	boolean m_bIsLoaded = false;
	
	SequenceNodeLayout(String i_sLayoutName)
	{
		m_sLayoutName = i_sLayoutName;
	}
}

