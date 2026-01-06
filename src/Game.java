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

	public Game(BorealisDemoGame game) {
		super(false);
		
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}

	protected void paint(Graphics arg0) {
		// TODO Auto-generated method stub

	}

	public static void Pause() {
		// TODO Auto-generated method stub
		
	}

}
