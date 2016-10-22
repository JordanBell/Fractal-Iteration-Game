package core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class FIConfig
{
	public static final boolean k_bShowDebugMenu = true;
	public static final boolean k_bUseRandomDecoderCodes = true;
	
	public static final float k_fMinFlingLength = 50;
	
	// Font config
	public static FreeTypeFontGenerator s_CourierGenerator;
	public static Color s_pColorGreen;
	public static Color s_pColorPink;
	public static Color s_pColorRed;
	
	public static FreeTypeFontParameter s_pFontParametersBlack8;
	public static BitmapFont s_pFontCourierBlack8;
	public static LabelStyle s_pLabelStyleCourierBlack8;
	
	public static FreeTypeFontParameter s_pFontParametersSystem;
	public static BitmapFont s_pFontCourierGreen12;
	public static LabelStyle s_pLabelStyleSystem;
	
	public static FreeTypeFontParameter s_pFontParametersRose;
	public static BitmapFont s_pFontCourierPink12;
	public static LabelStyle s_pLabelStyleRose;
	
	public static FreeTypeFontParameter s_pFontParametersAdam;
	public static BitmapFont s_pFontCourierRed12;
	public static LabelStyle s_pLabelStyleAdam;
	
	static public void create()
	{
		// Courier config setup
		s_CourierGenerator = new FreeTypeFontGenerator(Gdx.files.internal("data/courier.ttf"));
		s_pColorGreen = new Color(0.f, 1.f, 0.f, 1.f);
		s_pColorPink = new Color(1.0f, 0.75f, 0.75f, 1.f);
		s_pColorRed = new Color(0.9f, 0.7f, 0.f, 1.f);
		
		// Font parameters - Green 12
		s_pFontParametersSystem = new FreeTypeFontParameter();
		s_pFontParametersSystem.color = s_pColorGreen;
		s_pFontParametersSystem.size = 12;
		s_pFontParametersSystem.borderWidth = 0.5f;
		s_pFontParametersSystem.borderStraight = true;
		s_pFontParametersSystem.borderColor = new Color(s_pColorGreen.r, s_pColorGreen.g, s_pColorGreen.b, 0.25f);
		s_pFontCourierGreen12 = s_CourierGenerator.generateFont(s_pFontParametersSystem);
		s_pLabelStyleSystem = new LabelStyle(s_pFontCourierGreen12, s_pColorGreen);
		
		// Font parameters - Pink 12
		s_pFontParametersRose = new FreeTypeFontParameter();
		s_pFontParametersRose.color = s_pColorPink;
		s_pFontParametersRose.size = 12;
		s_pFontParametersRose.borderWidth = 0.5f;
		s_pFontParametersRose.borderStraight = true;
		s_pFontParametersRose.borderColor = new Color(s_pColorPink.r, s_pColorPink.g, s_pColorPink.b, 0.5f);
		s_pFontCourierPink12 = s_CourierGenerator.generateFont(s_pFontParametersRose);
		s_pLabelStyleRose = new LabelStyle(s_pFontCourierPink12, s_pColorPink);
		
		// Font parameters - Red 12
		s_pFontParametersAdam = new FreeTypeFontParameter();
		s_pFontParametersAdam.color = s_pColorRed;
		s_pFontParametersAdam.size = 12;
		s_pFontCourierRed12 = s_CourierGenerator.generateFont(s_pFontParametersAdam);
		s_pLabelStyleAdam = new LabelStyle(s_pFontCourierRed12, s_pColorRed);
		
		// Font parameters - Black 8
		s_pFontParametersBlack8 = new FreeTypeFontParameter();
		s_pFontParametersBlack8.color = Color.BLACK;
		s_pFontParametersBlack8.size = 8;
		s_pFontCourierBlack8 = s_CourierGenerator.generateFont(s_pFontParametersBlack8);
		s_pLabelStyleCourierBlack8 = new LabelStyle(s_pFontCourierBlack8, Color.BLACK);
	}
}
