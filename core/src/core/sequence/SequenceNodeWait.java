package core.sequence;

public class SequenceNodeWait extends SequenceNode
{
	protected int m_iWaitMilliseconds = 0;
	
	SequenceNodeWait(int i_iTime)
	{
		m_iWaitMilliseconds = i_iTime;
	}
}
