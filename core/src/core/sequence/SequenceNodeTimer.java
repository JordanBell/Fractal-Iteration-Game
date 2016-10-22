package core.sequence;

public class SequenceNodeTimer extends SequenceNode
{
	int m_iStartTextIndex = 0;
	
	long m_lRemainingMilliseconds = 0;
	
	SequenceNodeTimer()
	{
		m_bIsCheckpoint = true;
	}
	
	void addMilliseconds(int i_i)
	{
		m_lRemainingMilliseconds += i_i;
	}
	
	void addSeconds(int i_i)
	{
		addMilliseconds(i_i * 1000);
	}
	
	void addMinutes(int i_i)
	{
		addSeconds(i_i * 60);
	}
	
	void addHours(int i_i)
	{
		addMinutes(i_i * 60);
	}
	
	void addDays(int i_i)
	{
		addHours(i_i * 24);
	}
	
	@Override
	public String toString()
	{
		StringBuilder r_sTime = new StringBuilder();
		r_sTime.append("        ");
		
		long lMilliseconds = m_lRemainingMilliseconds;
		final long lSecond = 1000;
		final long lMinute = lSecond * 60;
		final long lHour = lMinute * 60;
		final long lDay = lHour * 24;
		
		// Days
		if(lMilliseconds >= lDay)
		{
			final long lNumDays = lMilliseconds / lDay;
			r_sTime.append(lNumDays);
			lMilliseconds -= lNumDays * lDay;
		}
		else
		{
			r_sTime.append('0');
		}
		
		r_sTime.append(':');
		
		// Hours
		if(lMilliseconds > lHour)
		{
			final long lNumHours = lMilliseconds / lHour;
			StringBuilder sNumHours = new StringBuilder();
			sNumHours.append(lNumHours);
			lMilliseconds -= lNumHours * lHour;
			
			if(sNumHours.length() == 1)
			{
				sNumHours.insert(0, '0');
			}
			
			r_sTime.append(sNumHours);
		}
		else
		{
			r_sTime.append("00");
		}
		
		r_sTime.append(':');
		
		// Minutes
		if(lMilliseconds > lMinute)
		{
			final long lNumMinutes = lMilliseconds / lMinute;
			StringBuilder sNumMinutes = new StringBuilder();
			sNumMinutes.append(lNumMinutes);
			lMilliseconds -= lNumMinutes * lMinute;
			
			if(sNumMinutes.length() == 1)
			{
				sNumMinutes.insert(0, '0');
			}

			r_sTime.append(sNumMinutes);
		}
		else
		{
			r_sTime.append("00");
		}
		
		r_sTime.append(':');
		
		// Seconds
		if(lMilliseconds > lSecond)
		{
			final long lNumSeconds = lMilliseconds / lSecond;
			StringBuilder sNumSeconds = new StringBuilder();
			sNumSeconds.append(lNumSeconds);
			lMilliseconds -= lNumSeconds * lSecond;
			
			if(sNumSeconds.length() == 1)
			{
				sNumSeconds.insert(0, '0');
			}

			r_sTime.append(sNumSeconds);
		}
		else
		{
			r_sTime.append("00");
		}
		
		r_sTime.append('\n');
		
		return r_sTime.toString();
	}
}
