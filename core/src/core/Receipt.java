package core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import core.tools.ui.ButtonGDX;

public class Receipt
{
	private Label m_pLabel;
	private Label m_pLabelCopyright;
	private Texture m_pTextureReceiptInDispenser = null;
	private Texture m_pTextureReceiptFull = null;

	public boolean m_bIsFullVisible = false;
	public boolean m_bIsDispenserGraphicVisible = false;
	public boolean m_bIsProcessed = true;
	
	public ButtonGDX m_pButtonTakeReceipt;
	
	public Receipt()
	{
		// Create textures
		m_pTextureReceiptInDispenser = new Texture(Gdx.files.internal("data/Textures/ReceiptInDispenser.png"));
		m_pTextureReceiptFull = new Texture(Gdx.files.internal("data/Textures/ReceiptBackground.png"));
		
		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("data/ReceiptFont.ttf"));
		Color fontColor = new Color(0.6f, 0.6f, 0.3f, 1.f);
		FreeTypeFontParameter mainFontGenerationParameters = new FreeTypeFontParameter();
		mainFontGenerationParameters.color = fontColor;
		mainFontGenerationParameters.size = 12;
		BitmapFont mainFont = fontGenerator.generateFont(mainFontGenerationParameters);
		LabelStyle mainFontStyle = new LabelStyle(mainFont, fontColor);
		
		m_pLabel = new Label("", mainFontStyle);
		m_pLabel.setAlignment(Align.topLeft);
		m_pLabel.setEllipsis(false);
		m_pLabel.setX(32);
		m_pLabel.setY(416);
		m_pLabel.setWrap(true);
		m_pLabel.setWidth(216);

		FreeTypeFontParameter copyrightFontGenerationParameters = new FreeTypeFontParameter();
		copyrightFontGenerationParameters.color = fontColor;
		copyrightFontGenerationParameters.size = 6;
		BitmapFont copyrightFont = fontGenerator.generateFont(copyrightFontGenerationParameters);
		LabelStyle copyrightFontStyle = new LabelStyle(copyrightFont, fontColor);
		m_pLabelCopyright = new Label("Copyright Virgo Ltd., 2003", copyrightFontStyle);
		m_pLabelCopyright.setEllipsis(false);
		m_pLabelCopyright.setX(-copyrightFontGenerationParameters.size);
		m_pLabelCopyright.setY(44);
		m_pLabelCopyright.setWrap(false);
		m_pLabelCopyright.setWidth(270);
		m_pLabelCopyright.setAlignment(Align.center);
		
		m_pButtonTakeReceipt = new ButtonGDX(150, 246, 64, 32)
		{
			@Override
			public void onButtonPressed()
			{
				if(m_bIsDispenserGraphicVisible)
				{
					onReceiptTaken();
				}
			}
		};
	}
	
	public void onReceiptTaken()
	{
		m_bIsDispenserGraphicVisible = false;
		m_bIsFullVisible = true;
	}
	
	public boolean isActive()
	{
		return m_bIsFullVisible || m_bIsDispenserGraphicVisible;
	}
	
	public void onReceiptClosed()
	{
		if(isActive())
		{
			m_bIsFullVisible = false;
			m_bIsProcessed = true;
		}
	}
	
	public void draw(SpriteBatch i_pBatch)
	{
		if(m_bIsDispenserGraphicVisible)
		{
			i_pBatch.draw(m_pTextureReceiptInDispenser, 158, 254);
		}
		
		if(m_bIsFullVisible)
		{
			i_pBatch.draw(m_pTextureReceiptFull, 0, 0);
			m_pLabel.draw(i_pBatch, 1.f);
			m_pLabelCopyright.draw(i_pBatch, 1.f);
		}
	}
	
	public void printReceipt(String m_sText)
	{
		m_pLabel.setText(m_sText);
		m_bIsDispenserGraphicVisible = true;
		m_bIsProcessed = false;
	}
}
