package core.tools.ui;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

abstract public class ButtonGDX
{
	public int m_iX;
	public int m_iY;
	public int m_iWidth;
	public int m_iHeight;
	
	public ButtonGDX(int i_iX, int i_iY, int i_iW, int i_iH)
	{
		m_iX = i_iX;
		m_iY = i_iY;
		m_iWidth = i_iW;
		m_iHeight = i_iH;
	}
	
	public boolean contains(Vector2 point)
	{
		if (point.x < m_iX)
			return false;
		if (point.x > m_iX + m_iWidth)
			return false;
		if (point.y < m_iY)
			return false;
		if (point.y > m_iY + m_iHeight)
			return false;
		
		// If none of the out of bounds checks triggered, return true
		return true;
	}

	/** The button's response when the mouse is pressed over it. Must be overriden. */
	public abstract void onButtonPressed();

	public void onButtonReleased() {}

	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if(contains(new Vector2(screenX, screenY)))
		{
			onButtonPressed();
			return true;
		}
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if(contains(new Vector2(screenX, screenY)))
		{
			onButtonReleased();
			return true;
		}
		return false;
	}
}
