package core.tools.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteSheetGDX
{
	private Texture m_pTexture = null;
	private TextureRegion[][] m_aRegions;

	private int m_iRows;
	private int m_iColumns;
	
	public SpriteSheetGDX(String i_sTextureName, final int rows, final int columns, final int width, final int height)
	{
		this(new Texture(Gdx.files.internal(i_sTextureName)), rows, columns, width, height);
	}
	
	public SpriteSheetGDX(Texture i_pTexture, final int i_iRows, final int i_iColumns, final int width, final int height)
	{
		m_pTexture = i_pTexture;
		
		m_iRows = i_iRows;
		m_iColumns = i_iColumns;
		
		m_aRegions = new TextureRegion[i_iColumns][i_iRows];
		
		for(int i = 0; i < i_iColumns; ++i)
		{
			for(int j = 0; j < i_iRows; ++j)
			{
				m_aRegions[i][j] = new TextureRegion(m_pTexture, i * width, j * height, width, height);
			}
		}
	}
	
	public TextureRegion getRegion(final int i_iSerialIndex)
	{
		final int iX = i_iSerialIndex % m_iColumns;
		final int iY = i_iSerialIndex / m_iColumns;
		
		return getRegion(iX, iY);
	}
	
	public TextureRegion getRegion(final int i_iX, final int i_iY)
	{
		return m_aRegions[i_iX][i_iY];
	}
}
