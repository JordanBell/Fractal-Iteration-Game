package core.tools.assertion;

/**
 * An interface for singleton assertion implementations. This has been abstracted so that we can redefine unique assertion behaviours.
 * 
 * @author Jordan Bell
 * Created on: 22 Aug 2015
 */
abstract class AssertionBase
{
	private boolean m_bIsActive = true;
	
	/** Handle an assertion trigger with a given message to print. This message is automatically prefixed with the assertion's prefix indicator. */
	public abstract void triggerImpl(String i_sAssertionMessage);
	
	/** @return A string prefixed before all assertion messages (delegated to the triggerImpl() function). Indicates the assertion type. */
	protected abstract String getPrefix();
	
	
	//--------------------------------------------------------------//
	// 						Member Functions						//
	//--------------------------------------------------------------//
	
	public void triggerWith(String i_sAssertionMessage)
	{
		// Only trigger if set as active.
		if (m_bIsActive)
		{
			if ((i_sAssertionMessage == null) || (i_sAssertionMessage.equals("")))
			{
				i_sAssertionMessage = "[No details provided]";
			}
			
			// Add this implementation's prefix before the message.
			triggerImpl(getPrefix() + i_sAssertionMessage);
		}
	}
	
	public void setActive(boolean i_b)
	{
		m_bIsActive = i_b;
	}

	
	//--------------------------------------------------------------//
	// 							Asserts								//
	//--------------------------------------------------------------//
	
	/** Trigger this assertion if the given condition is true. */
	public void assertTrue(boolean i_bCondition, String i_sAssertionMessage)
	{
		if (i_bCondition == false)
		{
			triggerWith(i_sAssertionMessage);
		}
	}
	
	/** Trigger this assertion if the given condition is true. */
	public void assertTrue(boolean i_bCondition)
	{
		assertTrue(i_bCondition, "");
	}
	
	/** Trigger this assertion if the given condition is false. */
	public void assertFalse(boolean i_bCondition, String i_sAssertionMessage)
	{
		if (i_bCondition == true)
		{
			triggerWith(i_sAssertionMessage);
		}
	}
	
	/** Trigger this assertion if the given condition is false. */
	public void assertFalse(boolean i_bCondition)
	{
		assertFalse(i_bCondition, "");
	}
	
	/** Trigger this assertion if the given condition is false. */
	public void assertNonNull(Object pO, String i_sAssertionMessage)
	{
		assertTrue(pO != null, i_sAssertionMessage);
	}
	
	/** Trigger this assertion if the given condition is false. */
	public void assertFalse(Object pO)
	{
		assertNonNull(pO, "");
	}
}