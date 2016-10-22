package core;

import java.awt.Point;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import core.tools.assertion.Assertion;
import core.tools.ui.ButtonGDX;
import core.tools.ui.SpriteSheetGDX;

abstract public class InputButton extends ButtonGDX
{
	static private SpriteSheetGDX s_pSpriteSheetStates;
	static private SpriteSheetGDX s_pSpriteSheetValues;
	
	public enum EState
	{
		EState_Up,
		EState_Down,
	}
	
	public enum EValueType
	{
		EValueType_AngerMild,
		EValueType_AngerMedium,
		EValueType_AngerStrong,
		EValueType_Deny,
		
		EValueType_SadMild,
		EValueType_SadMedium,
		EValueType_SadStrong,
		EValueType_Confirm,
		
		EValueType_HappyMild,
		EValueType_HappyMedium,
		EValueType_HappyStrong,
		EValueType_Defiant,

		EValueType_Left,
		EValueType_Right,
		EValueType_Yes,
		EValueType_No,
		
		EValueType_0,
		EValueType_1,
		
		EValueType_None;
		
		public static EValueType fromString(String string)
		{
			for(EValueType eValue : EValueType.values())
			{
				if(eValue.toString().toLowerCase().equals(string.toLowerCase()))
				{
					return eValue;
				}
			}
			
			return EValueType_None;
		}
		
		static public EValueType random()
		{
			return EValueType.values()[(int)Math.floor(Math.random() * EValueType.values().length)];
		}
		
		static public EValueType randomAngerSad()
		{
			final int index = (int)(Math.random() * 6);
			
			switch(index)
			{
				case 0: return EValueType_AngerMild;
				case 1: return EValueType_AngerMedium;
				case 2: return EValueType_AngerStrong;
				case 3: return EValueType_SadMild;
				case 4: return EValueType_SadMedium;
				case 5: return EValueType_SadStrong;
				default: return EValueType_None;
			}
		}
		
		@Override
		public String toString()
		{
			switch(this)
			{
				case EValueType_AngerMild:
					return ">:|";
				case EValueType_AngerMedium:
					return ">:(";
				case EValueType_AngerStrong:
					return "D:<";
				case EValueType_Deny:
					return "Denied";
					
				case EValueType_SadMild:
					return "(:|";
				case EValueType_SadMedium:
					return "(:(";
				case EValueType_SadStrong:
					return "D:)";
				case EValueType_Confirm:
					return "Confirmed";
					
				case EValueType_HappyMild:
					return ":|";
				case EValueType_HappyMedium:
					return ":)";
				case EValueType_HappyStrong:
					return ":D";
				case EValueType_Defiant:
					return ">:D";
					
				case EValueType_Left:
					return "<";
				case EValueType_Right:
					return ">";
				case EValueType_Yes:
					return "Y";
				case EValueType_No:
					return "N";

				case EValueType_0:
					return "0";
				case EValueType_1:
					return "1";
					
				case EValueType_None:
					return "null";
					
				default:
					Assertion.warning.triggerWith("No toString value for this button value type.");
			}
			
			return "Error";
		}
	}
	
	public EState m_eState = EState.EState_Up;
	public EValueType m_eValue = EValueType.EValueType_None;
	
	public InputButton(int i_iX, int i_iY, int i_iW, int i_iH) 
	{
		super(i_iX, i_iY, i_iW, i_iH);
		
		if(s_pSpriteSheetValues == null)
		{
			s_pSpriteSheetValues = new SpriteSheetGDX("data/Textures/ButtonValues.png", 8, 8, 16, 16);
		}
		
		if(s_pSpriteSheetStates == null)
		{
			s_pSpriteSheetStates = new SpriteSheetGDX("data/Textures/ButtonSheet.png", 1, 2, 32, 32);
		}
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.draw(s_pSpriteSheetStates.getRegion(m_eState.ordinal()), m_iX, m_iY);
		
		if(m_eValue != EValueType.EValueType_None)
		{ 
			batch.draw(s_pSpriteSheetValues.getRegion(m_eValue.ordinal()), m_iX + 8, m_iY + 10 - (m_eState == EState.EState_Down ? 4 : 0));
		}
	}
}
