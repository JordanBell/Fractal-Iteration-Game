package core.ui.menus.debug;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.FractalIterationGame;

import core.FIConfig;
import core.GameMode_Portrait;
import core.tools.ui.ButtonGDX;

public class UIMenuElement_ChooseSequence
{
	public Label m_pLabelCurrentSequence;
	
	public GameMode_Portrait m_pGame;
	
	Sprite m_pSpriteButtonPointer;

	ButtonGDX m_pButtonCycleLeft;
	ButtonGDX m_pButtonCycleRight;
	
	static final int s_iLabelX = 270/4;
	static final int s_iLabelY = 350;

	static final int s_iLeftX = 38;
	static final int s_iRightX = 270 - 56;
	static final int s_iButtonY = 345;
	
	UIMenuElement_ChooseSequence(GameMode_Portrait i_pGame)
	{
		m_pGame = i_pGame;
		
		m_pLabelCurrentSequence = new Label("[Failed to read sequence]", FIConfig.s_pLabelStyleCourierBlack8);
		m_pLabelCurrentSequence.setAlignment(Align.center);
		m_pLabelCurrentSequence.setEllipsis(false);
		m_pLabelCurrentSequence.setX(s_iLabelX);
		m_pLabelCurrentSequence.setY(s_iLabelY);
		m_pLabelCurrentSequence.setWrap(false);
		m_pLabelCurrentSequence.setWidth(128);
		
		m_pSpriteButtonPointer = new Sprite(new Texture("data/Textures/MenuDebug/PointerLeft.png"));
		m_pSpriteButtonPointer.setY(s_iButtonY);
		m_pButtonCycleLeft = new ButtonGDX(s_iLeftX, s_iButtonY, 32, 32)
		{
			@Override
			public void onButtonPressed()
			{
				m_pGame.debugCycleSequenceLeft();
			}
		};
		
		m_pButtonCycleRight = new ButtonGDX(s_iRightX, s_iButtonY, 32, 32)
		{
			@Override
			public void onButtonPressed()
			{
				m_pGame.debugCycleSequenceRight();
			}
		};
	}
	
	void draw(SpriteBatch i_pBatch)
	{
		m_pLabelCurrentSequence.draw(i_pBatch, 1.f);
		
		// Draw pointer left
		m_pSpriteButtonPointer.setRotation(0);
		m_pSpriteButtonPointer.setX(s_iLeftX);
		m_pSpriteButtonPointer.draw(i_pBatch);
		
		// Draw pointer right
		m_pSpriteButtonPointer.setRotation(180);
		m_pSpriteButtonPointer.setX(s_iRightX);
		m_pSpriteButtonPointer.draw(i_pBatch);
	}
	
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return m_pButtonCycleLeft.touchDown(screenX, screenY, pointer, button)
				|| m_pButtonCycleRight.touchDown(screenX, screenY, pointer, button);
	}
}