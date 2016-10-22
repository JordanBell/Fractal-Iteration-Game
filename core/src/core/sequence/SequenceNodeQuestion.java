package core.sequence;

public class SequenceNodeQuestion extends SequenceNode
{
	public SequenceNodeOption m_pOption1 = null;
	public SequenceNodeOption m_pOption2 = null;
	
	float m_fSilenceActivationTime;
	SequenceNode m_pNestedSilenceNode = null;
	
	SequenceNodeQuestion(SequenceNodeOption i_pOption1, SequenceNodeOption i_pOption2, SequenceNode i_pNestedSilenceNode, float i_fNumSeconds)
	{
		m_pOption1 = i_pOption1;
		m_pOption2 = i_pOption2;
		
		// Optional silence response sequence node
		if(i_pNestedSilenceNode != null)
		{
			m_fSilenceActivationTime = i_fNumSeconds;
			
			m_pNestedSilenceNode = i_pNestedSilenceNode;
			m_pNestedSilenceNode.setParent(this);
		}
	}
	
	@Override
	void setNext(SequenceNode i_pNext)
	{
		super.setNext(i_pNext);
		m_pOption1.setNext(i_pNext);
		m_pOption2.setNext(i_pNext);
	}
}
