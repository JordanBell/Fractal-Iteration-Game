package core;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import core.tools.assertion.Assertion;
import core.tools.ui.SpriteSheetGDX;

class KGridConfig
{
	public static final int GRID_WIDTH = 9;
	public static final int GRID_HEIGHT = 9;
	
	public static final int PLAYER_SPEED = 20;
}

enum EDirection
{
	EDirection_Up,
	EDirection_Down,
	EDirection_Left,
	EDirection_Right,
	
	EDirection_None;
	
	static EDirection fromAngle(float i_fAngle)
	{
		// Snap to one of the directions
		float diffUp = Math.abs(i_fAngle - 90.f);
		float diffLeft = Math.abs(i_fAngle - 180.f);
		float diffDown = Math.abs(i_fAngle - 270.f);
		
		if(diffUp < 45)
		{
			return EDirection_Up;
		}
		else if(diffLeft < 45)
		{
			return EDirection_Left;
		}
		else if(diffDown < 45)
		{
			return EDirection_Down;
		}
		else
		{
			return EDirection_Right;
		}
	}
}

class MovementData
{
	EDirection m_eCurrentDirection = EDirection.EDirection_None;
	EDirection m_eNextDirection = EDirection.EDirection_None;
	
	void set(EDirection i_eDirection)
	{
		if(m_eCurrentDirection == EDirection.EDirection_None)
		{
			m_eCurrentDirection = i_eDirection;
		}
		else 
		{
			m_eNextDirection = i_eDirection;
		}
	}
	
	void set(float i_fDirectionAngle)
	{
		set(EDirection.fromAngle(i_fDirectionAngle));
	}
	
	void onDirectionEnd()
	{
		m_eCurrentDirection = m_eNextDirection;
		m_eNextDirection = EDirection.EDirection_None;
	}
}

public class GameBoard
{
	enum EContentType
	{
		EContentType_WallTop,
		EContentType_WallLeft,
		EContentType_WallBottom,
		EContentType_WallRight,

		EContentType_Player,
		EContentType_Goal,
		EContentType_WallDiagonalForward,
		EContentType_WallDiagonalBack,

		EContentType_WallTopBottom,
		EContentType_WallOpenRight,
		EContentType_WallOpenLeft,
		EContentType_WallLeftRight,

		EContentType_Bit,
		EContentType_WallFilled,
		
		EContentType_None;
		
		static EContentType toEnum(char c)
		{			
			if(c == 'o')
			{
				return EContentType_Player;
			}
			else if (c == '_')
			{
				return EContentType_WallBottom;
			}
			else if (c == 'r')
			{
				return EContentType_WallRight;
			}
			else if (c == 'l')
			{
				return EContentType_WallLeft;
			}
			else if (c == '-')
			{
				return EContentType_WallTop;
			}
			else if (c == '\\')
			{
				return EContentType_WallDiagonalBack;
			}
			else if (c == '/')
			{
				return EContentType_WallDiagonalForward;
			}
			else if(c == 'e')
			{
				return EContentType_Goal;
			}
			else if(c == '=')
			{
				return EContentType_WallTopBottom;
			}
			else if(c == '[')
			{
				return EContentType_WallOpenRight;
			}
			else if(c == ']')
			{
				return EContentType_WallOpenLeft;
			}
			else if(c == 'H')
			{
				return EContentType_WallLeftRight;
			}
			else if(c == '.')
			{
				return EContentType_Bit;
			}
			else if(c == '0')
			{
				return EContentType_WallFilled;
			}
			else
			{
				return EContentType_None;
			}
		}

		public boolean isStopper()
		{
			switch(this)
			{
				default: 
					return false;

				case EContentType_WallTop:
				case EContentType_WallBottom:
				case EContentType_WallLeft:
				case EContentType_WallRight:
				case EContentType_WallTopBottom:
				case EContentType_WallLeftRight:
				case EContentType_WallOpenLeft:
				case EContentType_WallOpenRight:
				case EContentType_WallFilled:
					return true;
			}
		}
	}
	
	private EContentType[][] m_aContent = new EContentType[KGridConfig.GRID_WIDTH][KGridConfig.GRID_HEIGHT];
	
	private static SpriteSheetGDX s_pSpriteSheet = null;
	
	public MovementData m_pMovementData = new MovementData();
	private int m_iMovementCounter;
	
	private Point m_vPlayerPosition = null;
	
	private boolean m_bIsActive = false;
	
	public GameBoard()
	{
		// If the sprite sheet is null, it has not been loaded yet. Do so once (as it is statically loaded).
		if (s_pSpriteSheet == null)
		{
			s_pSpriteSheet = new SpriteSheetGDX("data/Textures/BoardContents.png", 4, 4, 16, 16);
		}
		
		// Init the content enum values
		clear();
	}
	public boolean isActive()
	{
		return m_bIsActive;
	}
	
	public void onFling(float angle)
	{
		if(m_bIsActive)
		{
			// Angle is in degrees
			m_pMovementData.set(angle);
		}
	}
	
	private void clear()
	{
		// Reset all cells to empty
		for(int i = 0; i < KGridConfig.GRID_HEIGHT; ++i)
		{
			for(int j = 0; j < KGridConfig.GRID_WIDTH; ++j)
			{
				m_aContent[i][j] = EContentType.EContentType_None;
			}
		}
		
		m_vPlayerPosition = null;
		m_bIsActive = false;
	}
	
	private void stopPlayer()
	{
		m_pMovementData.onDirectionEnd();
	}
	
	private void onGoalReached()
	{
		clear();
	}
	
	private void doMovePlayer(EDirection i_eDirection)
	{
		if(m_vPlayerPosition == null)
		{
			// There is no player. Assert and return.
			Assertion.imperative.triggerWith("There is no player to move.");
			return;
		}
		
		// Check what is in front of the player
		Point vFrontPosition = new Point(m_vPlayerPosition);
		
		switch (i_eDirection)
		{
			case EDirection_Up:
				vFrontPosition.y--;
				break;
			case EDirection_Down:
				vFrontPosition.y++;
				break;

			case EDirection_Left:
				vFrontPosition.x--;
				break;
			case EDirection_Right:
				vFrontPosition.x++;
				break;
				
			default:
				break;
		}
		
		final boolean bIsOutOfBounds = vFrontPosition.x < 0 || vFrontPosition.y < 0 || vFrontPosition.x >= KGridConfig.GRID_WIDTH || vFrontPosition.y >= KGridConfig.GRID_HEIGHT;
		if(bIsOutOfBounds)
		{
			stopPlayer();
			return;
		}
		
		// Get the object in front of us
		EContentType eFrontContent = m_aContent[vFrontPosition.x][vFrontPosition.y];
		
		// Handle front-content differently according to its type; we are about to move there!
		if(eFrontContent.isStopper())
		{
			stopPlayer();
			return;
		}
		else
		{
			// Handle diagonals
			if (eFrontContent == EContentType.EContentType_WallDiagonalBack || eFrontContent == EContentType.EContentType_WallDiagonalForward)
			{
				EDirection eNewDirection;
				
				if (eFrontContent == EContentType.EContentType_WallDiagonalBack)
				{
					if (i_eDirection == EDirection.EDirection_Up)
					{
						eNewDirection = EDirection.EDirection_Left;
					}
					else if (i_eDirection == EDirection.EDirection_Left)
					{
						eNewDirection = EDirection.EDirection_Up;
					}
					else if (i_eDirection == EDirection.EDirection_Down)
					{
						eNewDirection = EDirection.EDirection_Right;
					}
					else if (i_eDirection == EDirection.EDirection_Right)
					{
						eNewDirection = EDirection.EDirection_Down;
					}
				}
				else
				{
					if (i_eDirection == EDirection.EDirection_Up)
					{
						eNewDirection = EDirection.EDirection_Right;
					}
					else if (i_eDirection == EDirection.EDirection_Right)
					{
						eNewDirection = EDirection.EDirection_Up;
					}
					else if (i_eDirection == EDirection.EDirection_Down)
					{
						eNewDirection = EDirection.EDirection_Left;
					}
					else if (i_eDirection == EDirection.EDirection_Left)
					{
						eNewDirection = EDirection.EDirection_Down;
					}
				}

				m_aContent[m_vPlayerPosition.x][m_vPlayerPosition.y] = EContentType.EContentType_None; // TODO: Add after-images
				m_vPlayerPosition = vFrontPosition;
				m_aContent[m_vPlayerPosition.x][m_vPlayerPosition.y] = EContentType.EContentType_Player;
			}
			else 
			{
				// Move the player to the new position
				m_aContent[m_vPlayerPosition.x][m_vPlayerPosition.y] = EContentType.EContentType_None; // TODO: Add after-images
				m_vPlayerPosition = vFrontPosition;
				m_aContent[m_vPlayerPosition.x][m_vPlayerPosition.y] = EContentType.EContentType_Player;
				
				if(eFrontContent == EContentType.EContentType_Goal)
				{
					stopPlayer();
					onGoalReached();
					return;
				}
			}
		}
	}
	
	public void update(int i_iDelta)
	{
		// Move the player towards their current direction (if we have one)
		if(m_pMovementData.m_eCurrentDirection != EDirection.EDirection_None)
		{
			m_iMovementCounter += i_iDelta;
			
			if(m_iMovementCounter >= KGridConfig.PLAYER_SPEED)
			{
				m_iMovementCounter -= KGridConfig.PLAYER_SPEED;
				
				doMovePlayer(m_pMovementData.m_eCurrentDirection);
			}
		}
		else
		{
			m_iMovementCounter = 0;
		}
	}

	public void render(SpriteBatch i_pBatch, final int i_iX, final int i_iY)
	{
		// The image we will draw		
		for(int i = 0; i < KGridConfig.GRID_HEIGHT; ++i)
		{
			for(int j = 0; j < KGridConfig.GRID_WIDTH; ++j)
			{
				if(m_aContent[j][i] != EContentType.EContentType_None)
				{
					i_pBatch.draw(s_pSpriteSheet.getRegion(m_aContent[j][i].ordinal()), i_iX + j * 16, i_iY - i * 16);
				}
			}
		}
	}
	
	public void loadLayout(String filename)
	{
		BufferedReader reader;
		
		// Clear the saved player position
		clear();
		
		try
		{
			reader = new BufferedReader(new FileReader(filename));
			
			try 
			{
			    String line = reader.readLine();

			    int lineIndex = 0;
			    while (line != null && lineIndex < KGridConfig.GRID_HEIGHT) 
			    {
			    	for(int i = 0; i < Math.min(KGridConfig.GRID_WIDTH, line.length()); ++i)
			    	{
			    		char c = line.charAt(i);
			    		
			    		EContentType ee = EContentType.toEnum(c);
			    		m_aContent[i][lineIndex] = ee;
			    		if(m_aContent[i][lineIndex] == EContentType.EContentType_Player)
			    		{
			    			Assertion.warning.assertTrue(m_vPlayerPosition == null);
			    			m_vPlayerPosition = new Point(i, lineIndex);
			    		}
			    	}
			    	
			        // Next line
			    	++lineIndex;
			        line = reader.readLine();
			    }
			    
			    m_bIsActive = true;
			} 
			finally
			{
				reader.close();
			}
		} 
		catch (FileNotFoundException e)
		{
			clear();
			e.printStackTrace();
		}
		catch (IOException e)
		{
			clear();
			e.printStackTrace();
		}
	}

	public boolean keyDown(int keycode)
	{
		if(!m_bIsActive)
		{
			return false;
		}
		
		switch(keycode)
		{
			case Input.Keys.UP:
				m_pMovementData.set(EDirection.EDirection_Up);
				break;
			case Input.Keys.DOWN:
				m_pMovementData.set(EDirection.EDirection_Down);
				break;
			case Input.Keys.LEFT:
				m_pMovementData.set(EDirection.EDirection_Left);
				break;
			case Input.Keys.RIGHT:
				m_pMovementData.set(EDirection.EDirection_Right);
				break;
			default:
				return false;
		}
		
		return true;
	}
}
