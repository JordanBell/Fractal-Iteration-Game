package core.sequence;

public class SequenceNodeGenericMarker extends SequenceNode
{
	String m_sTypeID;
	String m_sText;
	
	SequenceNodeGenericMarker(String i_sTypeID, String i_sText)
	{
		m_sTypeID = i_sTypeID;
		m_sText = i_sText;
	}
}
