package core.sequence;

import core.InputButton;

public class SequenceNodeOption extends SequenceNode
{
	public InputButton.EValueType m_eValueType;
	SequenceNode m_pNestedNode = null;
	
	SequenceNodeOption(InputButton.EValueType i_eValueType, SequenceNode i_pNestedNode)
	{
		m_eValueType = i_eValueType;
		
		if(i_pNestedNode != null)
		{
			m_pNestedNode = i_pNestedNode;
			m_pNestedNode.setParent(this);
		}
	}
	
	public InputButton.EValueType getValue()
	{
		return m_eValueType;
	}
}
