package core.tools.assertion;

/**
 * An assertion implementation for imperative assertions.
 * 
 * @author Jordan Bell
 * Created on: 22 Aug 2015
 */
public class Imperative extends AssertionBase
{
	@Override
	public void triggerImpl(String i_sAssertionMessage)
	{
		throw new RuntimeException(i_sAssertionMessage);
	}

	@Override
	protected String getPrefix()
	{
		return "Assertion Triggered: ";
	}
}
