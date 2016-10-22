package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import core.FIConfig;
import core.GameMode;
import core.GameMode_Landscape;
import core.GameMode_Portrait;
import core.InputButton;
import core.InputButton.EValueType;
import core.sequence.SequenceHandler;
import core.tools.timing.TimerProgram;
import core.tools.ui.ButtonGDX;
import core.tools.ui.DisplayParticleEffect;
import core.ui.menus.debug.UIMenuDebug;

public class FractalIterationGame implements ApplicationListener, GestureListener
{
	enum EMode
	{
		EModePortrait,
		EModeLandscape,
	}
	
	EMode m_eMode;
	
	OrthographicCamera m_pCamera;
	Viewport m_pViewport;
	
	GestureDetector m_pGestureDetector;
	InputMultiplexer m_pInputMultiplexer;
	
	// Graphical components and buttons
	SpriteBatch m_pBatch;
	
	// Debug values
	boolean m_bDebugFlashFlag = false;
	
	// Modes
	GameMode_Portrait m_pModePortrait = new GameMode_Portrait();
	GameMode_Landscape m_pModeLandscape = new GameMode_Landscape();
	
	TextButton m_pTextButtonTest;
	
	boolean isPortrait()
	{
		return Gdx.input.getRotation() == 0 || Gdx.input.getRotation() == 180;
	}
	
	void updateMode()
	{
		m_eMode = isPortrait() ? EMode.EModePortrait : EMode.EModeLandscape;
	}
	
	@Override
	public void create() 
	{
		FIConfig.create();

		m_pModePortrait.create(m_pCamera);
		m_pModeLandscape.create(m_pCamera);
		
		m_pGestureDetector = new GestureDetector(this);
		m_pInputMultiplexer = new InputMultiplexer(m_pModePortrait, m_pModeLandscape, m_pGestureDetector);
		Gdx.input.setInputProcessor(m_pInputMultiplexer);
		
		m_pCamera = new OrthographicCamera();
	    m_pViewport = new FitViewport(270, 480, m_pCamera);
	    m_pViewport.apply();
	    m_pCamera.position.set(m_pCamera.viewportWidth/2,m_pCamera.viewportHeight/2,0);
		
		m_pBatch = new SpriteBatch();
	}
	
	private GameMode getCurrentGameMode()
	{
		switch(m_eMode)
		{
			case EModePortrait:
				return m_pModePortrait;
			case EModeLandscape:
				return m_pModeLandscape;
			default:
				return null;
		}
	}

	@Override
	public void render () 
	{
		updateMode();
		GameMode pCurrentMode = getCurrentGameMode();
		
		pCurrentMode.update();
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(m_bDebugFlashFlag)
		{
			m_bDebugFlashFlag = false;
			return;
		}
		
		m_pBatch.setProjectionMatrix(m_pCamera.combined);
		m_pBatch.begin();
		{
			pCurrentMode.render(m_pBatch);
		}
		m_pBatch.end();
	}

	@Override
	public void resize(int width, int height)
	{
		m_pViewport.update(width, height);
	    m_pCamera.position.set(m_pCamera.viewportWidth / 2, m_pCamera.viewportHeight / 2, 0);
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button)
	{
		return false;
	}

	@Override
	public boolean longPress(float x, float y)
	{
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button)
	{
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY)
	{
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance)
	{
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2)
	{
		return false;
	}
}
