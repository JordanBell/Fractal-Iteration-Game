package core.tools.timing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlWriter;

public class TimerProgram
{
	int m_iTimeSinceLastKeyPress;
	boolean m_bIsActive = false;

	List<Integer> m_lTimes = new LinkedList<Integer>();
	List<String> m_lWords = new LinkedList<String>();

	StringWriter m_pStringWriter = new StringWriter();
	XmlWriter m_pXmlWriter = new XmlWriter(m_pStringWriter);
	
	public void setText(String textFileName)
	{
		BufferedReader reader;
		try
		{
			reader = new BufferedReader(new FileReader(textFileName));
			try 
			{
				StringBuilder text = new StringBuilder(reader.readLine());
				
				while(text.length() > 0)
				{
					int iIndexWordEnd = text.indexOf(" ") + 1;
					{
						if(iIndexWordEnd == 0)
						{
							iIndexWordEnd = text.indexOf("/") + 1;

							if(iIndexWordEnd == 0)
							{
								iIndexWordEnd = text.indexOf(".") + 1;

								if(iIndexWordEnd == 0)
								{
									iIndexWordEnd = text.indexOf("?") + 1;

									if(iIndexWordEnd == 0)
									{
										iIndexWordEnd = text.indexOf("!") + 1;

										if(iIndexWordEnd == 0)
										{
											iIndexWordEnd = text.length();
										}
									}
								}
							}
						}
					}
					
					// Get the next word, add it to the list
					String nextWord = text.substring(0, iIndexWordEnd);
					m_lWords.add(nextWord);
					text.delete(0, iIndexWordEnd);
				}
			} 
			finally
			{
				reader.close();
			}
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void update()
	{
		if(m_bIsActive)
		{
			m_iTimeSinceLastKeyPress += (int)(1000 * Gdx.graphics.getDeltaTime());
		}
	}
	
	public void onTimeMark()
	{
		m_lTimes.add(m_iTimeSinceLastKeyPress);
		m_iTimeSinceLastKeyPress = 0;
		System.out.println("num times: " + m_lTimes.size());
	}
	
	public void toggle()
	{
		m_bIsActive = !m_bIsActive;
		
		if(!m_bIsActive)
		{
			m_lTimes.add(m_iTimeSinceLastKeyPress);
			printData();
		}

		m_iTimeSinceLastKeyPress = 0;
		m_lTimes.clear();
	}
	
	void printData()
	{
		try
		{
			m_pXmlWriter.element("sequence");
			for(int i = 0; i < m_lTimes.size() - 1; ++i)
			{
				int time = m_lTimes.get(i + 1);
				m_pXmlWriter.element("audio_syllable").attribute("time_until_next", time).pop();
			}
			m_pXmlWriter.pop();
			
			System.out.println(m_pStringWriter);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

/*
public class TimerProgram
{
	int m_iTimeSinceLastKeyPress;
	boolean m_bIsActive = false;

	List<Integer> m_lTimes = new LinkedList<Integer>();
	List<String> m_lWords = new LinkedList<String>();

	StringWriter m_pStringWriter = new StringWriter();
	XmlWriter m_pXmlWriter = new XmlWriter(m_pStringWriter);
	
	public void setText(String textFileName)
	{
		BufferedReader reader;
		try
		{
			reader = new BufferedReader(new FileReader(textFileName));
			try 
			{
				StringBuilder text = new StringBuilder(reader.readLine());
				
				while(text.length() > 0)
				{
					int iIndexWordEnd = text.indexOf(" ") + 1;
					{
						if(iIndexWordEnd == 0)
						{
							iIndexWordEnd = text.indexOf("/") + 1;

							if(iIndexWordEnd == 0)
							{
								iIndexWordEnd = text.indexOf(".") + 1;

								if(iIndexWordEnd == 0)
								{
									iIndexWordEnd = text.indexOf("?") + 1;

									if(iIndexWordEnd == 0)
									{
										iIndexWordEnd = text.indexOf("!") + 1;

										if(iIndexWordEnd == 0)
										{
											iIndexWordEnd = text.length();
										}
									}
								}
							}
						}
					}
					
					// Get the next word, add it to the list
					String nextWord = text.substring(0, iIndexWordEnd);
					m_lWords.add(nextWord);
					text.delete(0, iIndexWordEnd);
				}
			} 
			finally
			{
				reader.close();
			}
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void update()
	{
		if(m_bIsActive)
		{
			m_iTimeSinceLastKeyPress += (int)(1000 * Gdx.graphics.getDeltaTime());
		}
	}
	
	public void onTimeMark()
	{
		m_lTimes.add(m_iTimeSinceLastKeyPress);
		m_iTimeSinceLastKeyPress = 0;
		System.out.println("num times: " + m_lTimes.size());
	}
	
	public void toggle()
	{
		m_bIsActive = !m_bIsActive;
		
		if(!m_bIsActive)
		{
			if(m_lTimes.size() == m_lWords.size())
			{
				// Save a file with all of the timings
				writeFile();
			}
			else
			{
				System.out.println("Mismatch: " + m_lTimes.size() + " : words: " + m_lWords.size());
			}
		}

		m_iTimeSinceLastKeyPress = 0;
		m_lTimes.clear();
	}
	
	void writeFile()
	{
		try
		{
			m_pXmlWriter.element("sequence");
			for(int i = 0; i < m_lWords.size(); ++i)
			{
				String word = m_lWords.get(i);
				int time = m_lTimes.get(i);
				
				m_pXmlWriter.element("ti").text(word).pop().element("w").text(time).pop();
				
			}
			m_pXmlWriter.pop();
			
			System.out.println(m_pStringWriter);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}*/
