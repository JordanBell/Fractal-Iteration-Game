package core;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameMode implements InputProcessor
{
	protected Texture m_pBackground = null;
	protected Texture m_pTextureDebugMenuButton;
	protected OrthographicCamera m_pCamera;
	
	public abstract void create(OrthographicCamera i_pCamera);
	public abstract void update();
	public abstract void render(SpriteBatch i_pBatch);
}