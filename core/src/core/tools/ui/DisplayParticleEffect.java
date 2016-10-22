package core.tools.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

abstract class EffectStyle
{
	public float m_fStartingLife = 0.f;
	public float m_fCreationTime = 0.f;
	public int m_iEffectEndCount = 1;
	
	abstract public boolean b();
	abstract public int d();
}

class EffectStyleKaiser extends EffectStyle
{
	public EffectStyleKaiser()
	{
		super();
		m_fStartingLife = 0.25f;
		m_fCreationTime = 0.025f;
		m_iEffectEndCount = 20;
	}
	
	public boolean b() 	{ return Math.random() < 0.85f; }
	public int d() 		{ return b() ? 4 : 8; }
}

class EffectStyleDiamondThick extends EffectStyle
{
	public EffectStyleDiamondThick()
	{
		super();
		m_fStartingLife = 0.25f;
		m_fCreationTime = 0.025f;
		m_iEffectEndCount = 80;
	}
	
	public boolean b() 	{ return true; }
	public int d() 		{ return 4; }
}

class EffectStyleDiamondThin extends EffectStyle
{
	public EffectStyleDiamondThin()
	{
		super();
		m_fStartingLife = 0.05f;
		m_fCreationTime = 0.025f;
		m_iEffectEndCount = 80;
	}
	
	public boolean b() 	{ return true; }
	public int d() 		{ return 4; }
}

class EffectStyleTesting extends EffectStyle
{
	public EffectStyleTesting()
	{
		super();
		m_fStartingLife = 0.05f;
		m_fCreationTime = 0.015f;
		m_iEffectEndCount = 80;
	}
	
	public boolean b() 	{ return Math.random() < 0.75f; }
	public int d() 		{ return 4; }
}

public class DisplayParticleEffect
{
	List<DisplayParticle> m_lParticles = new ArrayList<DisplayParticle>();
	Vector2 m_pStartPos;
	float m_fCreationTimer = 0;
	boolean m_bActive = false;
	
	EffectStyle m_pEffectStyle = new EffectStyleTesting();
	
	int m_iLayerCount = 0;
	
	public DisplayParticleEffect(Vector2 i_pStartPoint)
	{
		start(i_pStartPoint);
	}

	public void start(Vector2 i_pStartPos)
	{
		i_pStartPos.x = (int)(i_pStartPos.x / 2) * 2.f;
		i_pStartPos.y = (int)(i_pStartPos.y / 2) * 2.f;
		m_pStartPos = i_pStartPos;
		
		createParticle(i_pStartPos);
		m_bActive = true;
	}
	
	public void update()
	{
		if(m_bActive)
		{
			m_fCreationTimer += Gdx.graphics.getDeltaTime();
			updateLife();
			updateDeath();
		}
		else
		{
			m_fCreationTimer = 0.f;
			m_iLayerCount = 0;
		}
	}
	
	public void render(ShapeRenderer pRenderer)
	{
		pRenderer.begin(ShapeType.Filled);
		pRenderer.setColor(Color.GREEN);
		for(DisplayParticle pDisplayParticle : m_lParticles)
		{
			pRenderer.rect(pDisplayParticle.x, pDisplayParticle.y, pDisplayParticle.w, pDisplayParticle.h);
		}
		pRenderer.end();
	}
	
	void updateDeath()
	{
		// Update life values and create a list of dead particles to remove.
		List<DisplayParticle> lDeadParticles = new ArrayList<DisplayParticle>();
		for(DisplayParticle pDisplayParticle : m_lParticles)
		{
			pDisplayParticle.m_fLifeRemaining -= Gdx.graphics.getDeltaTime();
			
			if(pDisplayParticle.isDead())
			{
				lDeadParticles.add(pDisplayParticle);
			}
		}

		// Remove dead particles
		for(DisplayParticle pDeadParticle : lDeadParticles)
		{
			m_lParticles.remove(m_lParticles.indexOf(pDeadParticle));
		}
	}
	
	void updateLife()
	{
		if(m_fCreationTimer > m_pEffectStyle.m_fCreationTime && m_iLayerCount <= m_pEffectStyle.m_iEffectEndCount)
		{
			m_fCreationTimer -= m_pEffectStyle.m_fCreationTime;
			++m_iLayerCount;

			// Create a layer now
			spawnParticleNeighbors();
			System.out.println(m_fCreationTimer);
		}
		
		m_bActive = !m_lParticles.isEmpty();
	}
	
	boolean isValidCreationPoint(Vector2 i_pSourcePoint, Vector2 i_pNeighbor)
	{
		if(occupied(i_pNeighbor))
		{
			return false;
		}
		
		float fSourceDistance = i_pSourcePoint.dst(m_pStartPos);
		float fCreationDistance = i_pNeighbor.dst(m_pStartPos);
		
		return fCreationDistance > fSourceDistance;
	}
	
	boolean occupied(Vector2 i_p)
	{
		for(DisplayParticle pDisplayParticle : m_lParticles)
		{
			if(pDisplayParticle.x == i_p.x && pDisplayParticle.y == i_p.y)
			{
				return true;
			}
		}
		
		return false;
	}
	
	void spawnParticleNeighbors()
	{
		List<Vector2> lCreationPoints = new ArrayList<Vector2>();
		
		for(DisplayParticle pDisplayParticle : m_lParticles)
		{
			if(!pDisplayParticle.m_bHasSpawnedNeighbors)
			{
				Vector2 pOriginPoint = new Vector2(pDisplayParticle.x, pDisplayParticle.y);

				Vector2 pLeft = new Vector2(pOriginPoint.x - m_pEffectStyle.d(), pOriginPoint.y);
				Vector2 pRight = new Vector2(pOriginPoint.x + m_pEffectStyle.d(), pOriginPoint.y);
				Vector2 pUp = new Vector2(pOriginPoint.x, pOriginPoint.y + m_pEffectStyle.d());
				Vector2 pDown = new Vector2(pOriginPoint.x, pOriginPoint.y - m_pEffectStyle.d());
				
				if(m_iLayerCount <= 3 || m_pEffectStyle.b() && isValidCreationPoint(pOriginPoint, pRight))
					lCreationPoints.add(pRight);

				if(m_iLayerCount <= 3 || m_pEffectStyle.b() && isValidCreationPoint(pOriginPoint, pLeft))
					lCreationPoints.add(pLeft);

				if(m_iLayerCount <= 3 || m_pEffectStyle.b() && isValidCreationPoint(pOriginPoint, pUp))
					lCreationPoints.add(pUp);

				if(m_iLayerCount <= 3 || m_pEffectStyle.b() && isValidCreationPoint(pOriginPoint, pDown))
					lCreationPoints.add(pDown);
				
				pDisplayParticle.m_bHasSpawnedNeighbors = true;
			}
			
		}
		
		for(Vector2 pCreationPoint : lCreationPoints)
		{
			createParticle(pCreationPoint);
		}
	}
	
	void createParticle(Vector2 i_p)
	{
		if(!occupied(i_p))
		{
			DisplayParticle pParticle = new DisplayParticle();
			pParticle.x = i_p.x;
			pParticle.y = i_p.y;
			pParticle.m_fLifeRemaining = m_pEffectStyle.m_fStartingLife;
			
			m_lParticles.add(pParticle);
		}
	}
}
