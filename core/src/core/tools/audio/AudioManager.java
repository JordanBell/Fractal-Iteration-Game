package core.tools.audio;

public class AudioManager
{
	public AudioEffectSet system = new AudioEffectSet("data/Audio/Effects/TextPrint/PrintText_System", 10);
	public AudioEffectSet rose = new AudioEffectSet("data/Audio/Effects/TextPrint/TextPrint_Rose", 3);
	public AudioEffectSet adam = new AudioEffectSet("data/Audio/Effects/TextPrint/PrintText_AdamAlt", 10);
	
	private static AudioManager m_pInstance = null;
	
	public static AudioManager get()
	{
		if(m_pInstance == null)
		{
			m_pInstance = new AudioManager();
			m_pInstance.init();
		}
		
		return m_pInstance;
	}
	
	private void init()
	{
		// Initialise
	}
}
