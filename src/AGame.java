import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

public abstract class AGame extends GameCanvas implements Runnable {

	public AGame() 
	{ 
		super(false);
		DBG.Log("AGame.constructor");
		s_game = this;
		
        // set reference to the application
        s_application = (MIDlet) application;

        // set the display object
        s_display = (Display) display;
	}
	
    //The only instance of this class
    static AGame s_game = null;
    
    //reference to the display
    static Display s_display = null;

    //Multi-entry control. To be sure that we are only once into the paint method.
    private static boolean s_game_isInPaint = false;

    //Reference to the application.
    static MIDlet s_application = null;
	
	//------------------------------------------------------------------------------
    //Function to be implemented in every game.
    //This is where you put the code of your game. This function will be called once per frame,
    //You have to do the game Logic/Ai and Painting from whithin.
    //This function is called from the paint call of this canvas.
    //------------------------------------------------------------------------------
    abstract void Game_update();
	
	public void run()
	{
		Game_Run();
	}
	
	// To Implement
	public void Game_Run() {}
	
	public static boolean IsEmulator()
	{
		try {
		// GLEmulator
		if (s_application.getAppProperty("EMU://EndNamedEvent") != null)
		{
			return true;
		}
		
		// KEmulator nnmod
		if (Class.forName("emulator.Emulator") != null)
		{
			return true;
		}}catch (ClassNotFoundException e) {}
		return false;
	}
}