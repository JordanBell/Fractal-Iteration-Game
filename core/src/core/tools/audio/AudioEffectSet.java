package core.tools.audio;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;


public class AudioEffectSet
{
	private List<Sound> m_pSounds = new ArrayList<Sound>();
	public float m_fVolume = 1.f;
	static final String fileTypeSuffix = ".wav";
	
	public AudioEffectSet(final String i_sFileNameBase, final int i_iVariantCount)
	{
		// Add the variant number of sound effects
		for (int i = 0; i < i_iVariantCount; ++i)
		{
			StringBuilder fileName = new StringBuilder(i_sFileNameBase);
			fileName.append((i+1) + fileTypeSuffix);
			m_pSounds.add(Gdx.audio.newSound(Gdx.files.internal(fileName.toString())));
		}
	}

	public void play()
	{
		// Play a random sound
		int iRandIndex = (int)Math.floor(Math.random() * m_pSounds.size());
		m_pSounds.get(iRandIndex).play(m_fVolume);
	}
}
