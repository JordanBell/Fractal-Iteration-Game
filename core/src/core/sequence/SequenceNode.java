package core.sequence;

public class SequenceNode
{
	private SequenceNode m_pNext = null;
	SequenceNode m_pParent = null;
	
	public boolean m_bIsCheckpoint = false;
	public String m_sID = "";
	
	void setNext(SequenceNode i_pNext)
	{
		m_pNext = i_pNext;
	}
	
	SequenceNode getNext()
	{
		return m_pNext != null ? m_pNext : m_pParent != null ? m_pParent.getNext() : null;
	}

	// Set the parent of this node, and the nodes of any immediate neighbors
	void setParent(SequenceNode i_pParent)
	{
		m_pParent = i_pParent;
		if(m_pNext != null)
		{
			m_pNext.setParent(i_pParent);
		}
	}
}
