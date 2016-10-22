package core.tools.assertion;

/**
 * An assertion implementation for lower-priority assertions.
 * 
 * @author Jordan Bell
 * Created on: 22 Aug 2015
 */
public class Warning extends AssertionBase
{
	private static boolean m_bForcedImperative = false;
	
	@Override
	public void triggerImpl(String i_sAssertionMessage)
	{
		if (!m_bForcedImperative)
		{
			System.err.println(i_sAssertionMessage);
		}
		else
		{
			// If warnings are overridden as imperative, delegate to the imperative implementation.
			Assertion.imperative.triggerImpl("[HEIGHTENED] " + i_sAssertionMessage);
		}
	}
	
	@Override
	protected String getPrefix()
	{
		return "WARNING-~: ";
	}
	
	public static void setForcedImperative(boolean i_b)
	{
		m_bForcedImperative = i_b;
	}
}
