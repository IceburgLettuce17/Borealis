import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

public abstract class AGame extends GameCanvas implements Runnable {

    //Current game state.
    static int s_game_state = 0;

	static boolean s_game_isPaused;

	private long m_frameCoheranceTimer;

	public AGame(Object application, Object display) 
	{ 
		super(false);
		DBG.Log("AGame.constructor");
		s_game = this;
		
        // set reference to the application
        s_application = (MIDlet) application;

        // set the display object
        s_display = (Display) display;
	}
	
    protected void SetupDisplay() {
        this.setFullScreenMode(true);
        if (s_display != null) {
            if (s_display.getCurrent() != this) {
                s_display.setCurrent(this);
            }
        }
    };
    /**/
    
    //The only instance of this class
    static AGame s_game = null;
    
    //reference to the display
    static Display s_display = null;

    //Multi-entry control. To be sure that we are only once into the paint method.
    private static boolean s_game_isInPaint = false;

    //Reference to the application.
    static MIDlet s_application = null;
    
    //Idle time to reach "ideal" fps. Allow to limit the frame rate on very powerful phones<br>
    //Max fps = 1000/m_FPSLimiter
    private static int m_FPSLimiter = (int)1000 / DEF.MAX_FPS;
	
	//------------------------------------------------------------------------------
    //Function to be implemented in every game.
    //This is where you put the code of your game. This function will be called once per frame,
    //You have to do the game Logic/Ai and Painting from whithin.
    //This function is called from the paint call of this canvas.
    //------------------------------------------------------------------------------
    abstract void Game_update();

    //------------------------------------------------------------------------------
    //Game engine main loop/thread. Override of Runnable.run.
    //@note The game should not call this function, it will be called automaticaly by the thread. The game code has to go into the abstract function AGame.Game_update.
    //\sa AGame.Game_update
    //------------------------------------------------------------------------------
	public void run()
	{
		if (!DEF.bCallSerially)
		{
			DBG.Log("AGame.run");
		}
	    try {
	            if (!DEF.bCallSerially) {
	                SetupDisplay();
	            }
	            s_game_isInPaint = false;
	            while (s_game_state >= 0) {
	                if (!s_game_isPaused) {
	                    repaint();
	                    if (DEF.bServiceRepaints) {
	                        serviceRepaints();
	                    }
	                    Game_Run();
	                    long curTime = System.currentTimeMillis();
	                    this.m_frameCoheranceTimer = Math.min(this.m_frameCoheranceTimer, curTime);
	                    if (DEF.bSleepInsteadOfYield) {
	                        try {
	                            Thread.sleep(Math.max(1L, m_FPSLimiter - (curTime - this.m_frameCoheranceTimer)));
	                        } catch (Exception e) {
	                        }
	                    } else {
	                        while (curTime - this.m_frameCoheranceTimer < m_FPSLimiter) {
	                            Thread.yield();
	                            curTime = System.currentTimeMillis();
	                            this.m_frameCoheranceTimer = Math.min(this.m_frameCoheranceTimer, curTime);
	                        }
	                    }
	                    this.m_frameCoheranceTimer = System.currentTimeMillis();
	                } else {
	                    this.m_frameCoheranceTimer = Math.min(this.m_frameCoheranceTimer, System.currentTimeMillis());
	                    if (DEF.bSleepInsteadOfYield) {
	                        try {
	                            Thread.sleep(1L);
	                        } catch (Exception e2) {
	                        }
	                    } else {
	                        Thread.yield();
	                    }
	                }
	                if (DEF.bCallSerially) {
	                    s_display.callSerially(this);
	                    return;
	                }
	            }
	        } catch (Exception e3) {
	            DBG.Log(new StringBuffer().append("!!FATAL ERROR!! in AGame.run().").append(e3).toString());
	            e3.printStackTrace();
	            s_game_state = -1;
	        }
	        DBG.Log("AGame.Quitting main loop");
	        UnInit();
	        if (!DEF.bDisableNotifyDestroyed) {
	            s_application.notifyDestroyed();
	        }
	}
	
	
    protected void UnInit() {
        DBG.Log("AGame.deInit");
        System.gc();
    }
    
	// To Implement
	public void Game_Run() throws Exception {}
	
	public static boolean IsEmulator()
	{
		// GLEmulator
		try {
			return Class.forName("com.gameloft.aurora.BitmapFont") != null || Class.forName("emulator.Emulator") != null;
			
		} catch (ClassNotFoundException e) {}
		return false;
	}
}