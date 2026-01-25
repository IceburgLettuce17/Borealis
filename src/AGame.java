import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

public abstract class AGame extends GameCanvas implements Runnable {



	public AGame(Object application, Object display) 
	{ 
		super(false);
		DBG.Log("AGame.constructor");
		s_game = this;
		if (DEF.bDumpConfig)
		{
			//DumpDefs();
		}
        // set reference to the application
        s_application = (MIDlet) application;

        // set the display object
        s_display = (Display) display;
        
        try {
			s_isGLEmu = Class.forName("com.gameloft.aurora.BitmapFont") != null;
		} catch (ClassNotFoundException e) {
			DBG.Log("Is not GLEmulator");
			e.printStackTrace();
		}
        try {
			s_is_kEmNNMod = Class.forName("emulator.Emulator") != null;
		} catch (ClassNotFoundException e) {
			DBG.Log("Is not KEmulator");
			e.printStackTrace();
		} 
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
    
    /*void DumpDefs() {
        System.out.println("");
        System.out.println("");
        System.out.println("Borealis Engine configuration (after the merge with your DEF.java file):");
        System.out.println("");
    }*/
    
    //Interrupt notifier. Set to true when an interrupt occured.
    static boolean s_game_interruptNotify      = false;
    
    //Current game state.
    static int s_game_state = 0;
    
    //Current time when the frame started.
    static long s_game_timeWhenFrameStart = 0;

	static boolean s_game_isPaused = false;

	private long m_frameCoheranceTimer;
	
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
    
    //Similar to s_game_timeWhenFrameStart but internally used for DEF.bUseFakeInterruptHandling
    static long s_game_lastFrameTime = 0;
    
    //Delta time between the previous frame and this one. @note valid only if DEF.bbUseFrameDT is true.
    static int s_game_frameDT = 0;
    //End time of the previous frame. @note valid only if DEF.bbUseFrameDT is true.
	static private long s_game_frameDTTimer = 0;
    //Total game execution time. @note valid only if DEF.bbUseFrameDT is true.
    static int s_game_totalExecutionTime = 0; 
	
    static int m_keys_pressed;
    static int m_keys_released;
    static int m_keys_state;
    static int m_current_keys_state;
    static int m_current_keys_pressed;
    static int m_current_keys_released;
    static int m_last_key_pressed;
    
    //Average fps * 100. @note valid only if DEF.bUseFrameDT is true.
    static int                                                s_game_FPSAverage           = 0;

    //Current frame number. Increased every frame.
    static int s_game_currentFrameNB = 0;

    //Graphics context where all rendering operation will happen.
    static public Graphics g = null;

    //reference on the latest gaphic context as passed by paint()
    static private Graphics s_lastPaintGraphics = null;
    
    static long s_game_keyPressedTime;
    
    static boolean s_isGLEmu,
    s_is_kEmNNMod;
    
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
	
    public void paint(final Graphics _g) {
        this.Game_Paint(_g);
    }
    
    //------------------------------------------------------------------------------
    //AGame main rendering. AGame.Game_update will be called from this function.
    //------------------------------------------------------------------------------
    private void Game_Paint(final Graphics _g) {
        if (DEF.bUseFakeInterruptHandling) {
            final long elapsedTime = System.currentTimeMillis() - AGame.s_game_lastFrameTime;
            AGame.s_game_lastFrameTime = System.currentTimeMillis();
            if (elapsedTime > DEF.FAKE_INTERRUPT_TH && AGame.s_game_lastFrameTime != 0L) {
                Pause();
                this.Resume();
            }
        }
        if (AGame.s_game_isPaused || AGame.s_game_isInPaint) {
            return;
        }
        AGame.s_game_isInPaint = true;
        this.UpdateKeypad();
        AGame.s_game_timeWhenFrameStart = System.currentTimeMillis();
        if (DEF.bUseFrameDT) {
            AGame.s_game_frameDT = (int)(AGame.s_game_timeWhenFrameStart - AGame.s_game_frameDTTimer);
            if (AGame.s_game_frameDT < 0) {
                AGame.s_game_frameDT = 0;
            }
            if (AGame.s_game_frameDT > 1000) {
                AGame.s_game_frameDT = 1000;
            }
            AGame.s_game_frameDTTimer = AGame.s_game_timeWhenFrameStart;
            AGame.s_game_totalExecutionTime += AGame.s_game_frameDT;
            AGame.s_game_FPSAverage = 100000 * AGame.s_game_currentFrameNB / (AGame.s_game_totalExecutionTime + 1);
        }
        ++AGame.s_game_currentFrameNB;
        try {
        		AGame.s_lastPaintGraphics = _g;
                AGame.g = _g;
                this.Game_update();
            }
        catch (final Exception e) {
            DBG.Log("!!FATAL ERROR!! in Game_paint()." + e);
            e.printStackTrace();
            AGame.s_game_state = -1;
        }
        if (DEF.LOW_MEMORY_LIMIT > 0 && Runtime.getRuntime().freeMemory() < DEF.LOW_MEMORY_LIMIT) {
            System.gc();
        }
        AGame.s_game_interruptNotify = false;
        AGame.s_game_isInPaint = false;
    }
   
    
    private void UpdateKeypad() {
       AGame.m_keys_pressed = AGame.m_current_keys_pressed;
            AGame.m_keys_released = AGame.m_current_keys_released;
            AGame.m_keys_state = AGame.m_current_keys_state;
            AGame.m_current_keys_pressed = 0;
            AGame.m_current_keys_released = 0;
            AGame.s_game_keyPressedTime = AGame.s_game_timeWhenFrameStart;
    }
    
	public static boolean IsEmulator()
	{
		// GLEmulator
		try {
			return s_isGLEmu || Class.forName("emulator.Emulator") != null;
			
		} catch (ClassNotFoundException e) {}
		return false;
	}
	
    protected static void Pause() {
        s_game_isPaused = true;
        DBG.Log("AGame.pause");
        /*if (GLLibConfig.sound_useStopSoundsOnInterrupt) {
            try {
                //GLLibPlayer.Snd_StopAllSounds();
                //GLLibPlayer.Snd_ForceExecOnThreadOnGamePause();
            }
            catch (final Exception ex) {}
        }*/
    }
   
    protected void Resume() {
        DBG.Log("AGame.resume");
        if (AGame.s_game_isPaused) {
            final long time = AGame.s_game_frameDTTimer = (AGame.s_game_timeWhenFrameStart = System.currentTimeMillis());
            this.m_frameCoheranceTimer = time;
            AGame.s_game_isPaused = false;
            this.SetupDisplay();
            AGame.s_game_interruptNotify = true;
            this.repaint();
            ResetKey();
        }
    }
    
    public static void ResetKey() {
            AGame.m_keys_pressed = 0;
            AGame.m_keys_released = 0;
            AGame.m_keys_state = 0;
            AGame.m_current_keys_state = 0;
            AGame.m_current_keys_pressed = 0;
            AGame.m_current_keys_released = 0;
    }
}