package core.ui.menus.debug;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.FractalIterationGame;

import core.FIConfig;
import core.GameMode_Portrait;
import core.tools.ui.ButtonGDX;

public class UIMenuDebug
{
	Texture m_pTextureBackground;
	int m_iX;
	int m_iY;
	public boolean m_bIsOpen = false;
	
	ButtonGDX m_pButtonClose;
	
	public UIMenuElement_ChooseSequence m_pElementChooseSequence;
	
	GameMode_Portrait m_pGame;
	
	public UIMenuDebug(int i_iX, int i_iY, GameMode_Portrait i_pGame)
	{
		m_iX = i_iX;
		m_iY = i_iY;
		m_pGame = i_pGame;
		m_pTextureBackground = new Texture("data/Textures/MenuDebug/Background.png");
		
		m_pElementChooseSequence = new UIMenuElement_ChooseSequence(m_pGame);
		
		m_pButtonClose = new ButtonGDX(225, 355, 32, 32)
		{
			@Override
			public void onButtonPressed()
			{
				close();
			}
		};
	}
	
	public void open()
	{
		m_bIsOpen = true;
	}
	
	public void close()
	{
		m_bIsOpen = false;
	}
	
	public void render(SpriteBatch i_pBatch)
	{
		if(m_bIsOpen)
		{
			i_pBatch.draw(m_pTextureBackground, m_iX, m_iY);
			m_pElementChooseSequence.draw(i_pBatch);
		}
	}
	
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if(!m_bIsOpen)
		{
			return false;
		}
		
		return m_pButtonClose.touchDown(screenX, screenY, pointer, button)
				|| m_pElementChooseSequence.touchDown(screenX, screenY, pointer, button);
	}
}
