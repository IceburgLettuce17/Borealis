import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.Graphics;

//--------------------------------------------------------------------------------------------------------------------
// Generic Borealis game class.
//--------------------------------------------------------------------------------------------------------------------
public final class Game extends AGame implements Runnable 
{
	/*
	 * The amount of games currently running.
	 */
	public static int _gameCounter;
	
	/*
	 * The version of the game. 
	 * This gets set when the MIDlet launches.
	 */
	public static String _sVersion;

	public static ASprite font;

	public Game(BorealisDemoGame game) {
		super(s_application, s_display);
		if (s_isGLEmu)
		{
			DBG.Log("GLEmulator!");
		}
		if (s_is_kEmNNMod)
		{
			DBG.Log("KEmulator!");
		}
		
	}

	void Game_update() {
		g.drawString("Hello World!", 1, 1, 0);
	}


}
