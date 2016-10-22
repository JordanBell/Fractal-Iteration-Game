package core.sequence;

public class SequenceNodeScreenFlash extends SequenceNode
{
	int m_iTimeUntilNext = 0;
	String m_sText = "";
	
	SequenceNodeScreenFlash(int i_iTimeUntilNext, String i_sText)
	{
		m_iTimeUntilNext = i_iTimeUntilNext;
		m_sText = i_sText;
	}
	
	public float getFlashAlpha(int i_iWaitCount)
	{
		float r_fInterpValue = (float)Math.pow(1.f - ((float)i_iWaitCount / Math.max(m_iTimeUntilNext, 1000)), 5);
		
		// Flicker variance
		final float fFlickerMagnitude = 0.2f;
		r_fInterpValue += (Math.random() * fFlickerMagnitude) - (fFlickerMagnitude / 2);
		
		if(r_fInterpValue > 1.f)
			r_fInterpValue = 1.f;
		if(r_fInterpValue < 0.f)
			r_fInterpValue = 0.f;
		
		return r_fInterpValue;
	}
}
