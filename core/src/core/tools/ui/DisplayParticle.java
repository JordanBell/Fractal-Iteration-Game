package core.tools.ui;

public class DisplayParticle
{
	boolean m_bHasSpawnedNeighbors = false;
	float m_fLifeRemaining;
	float x = 0;
	float y = 0;
	float w = 4;
	float h = 4;
	
	boolean isDead()
	{
		return m_fLifeRemaining <= 0.f;
	}
}
