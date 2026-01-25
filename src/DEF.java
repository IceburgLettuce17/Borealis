// DEF.java
////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  Author(s): Ionut Matasaru (ionut.matasaru@gameloft.com)
//
////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  This inteface will be removed during the obfusction process.
//
////////////////////////////////////////////////////////////////////////////////////////////////////

interface DEF
{
	final static boolean bEmu			= AGame.IsEmulator();	// emulator
	final static boolean bRelease		= true; 	// release version
	final static boolean bSnd 			= !true; 	// sound suport enable
	final static boolean bSnd_test		= false;	// test the sound
	final static boolean bErr 			= !false;	// catch errors (exceptions)
	final static boolean bASSERT 		= false;	// enables DBG.ASSERT

	// enable/disable "System.out.println"
	final static boolean bDbgO 			= !false;	// generic debug out (System.out.println)
    final static boolean bDbgT 			= false;	// trace calls and params (lots of output)
	final static boolean bDbgM 			= !false;	// debug out for memory
	final static boolean bDbgS 			= false;	// debug out for cSprite.Load()
	final static boolean bDbgI			= false;	// debug out for images (cached sprites)

	final static boolean DbgDrawFPS		= false;	// shows the FPS
	final static boolean DbgDrawMem		= false;	// shows the free memory
	final static boolean DbgDrawFrameNr	= false;	// shoes the frame number (game counter)
	final static boolean bDbgInfo		= (bErr || bDbgO || DbgDrawFPS || DbgDrawMem || DbgDrawFrameNr);

	final static boolean bSlowMode		= !false;	// slow mode can be switched with the SOFT_M key

	final static boolean LIMIT_FPS		= true; 	// limit framerate
	final static int 	 MAX_FPS		= 14;		// maximum fps (if limited)

	final static int     SCR_W   		= 800;
	final static int     SCR_H  		= 480;
	
	final static boolean bDumpConfig	= true;		// dump values

	//////////////////////////////////////////////////
	// Workarounds for some issues... And some configs

	final static boolean bClippingBug	= false;
	final static boolean bStreamReadBug	= false;
	final static boolean bMotorolaV300	= true;
	final static boolean bSleepInsteadOfYield = true;
	final static boolean bCallSerially = false;
	static final boolean bServiceRepaints = true;
	static final boolean bDisableNotifyDestroyed = false;
	static final boolean bUseFakeInterruptHandling = true;
	static final boolean bUseFrameDT				= true;
	final static int LOW_MEMORY_LIMIT = 0;
	static final int FAKE_INTERRUPT_TH = 3250;

	//////////////////////////////////////////////////

	final static int FIXED_PRECISION	= 8;		// fixed point precision

	//////////////////////////////////////////////////
	// [game pixels] = game pixels = [screen pixels] * ZOOM_XY_DIV / ZOOM_XY
	// [pixels]      = screen pixels = [game pixels] * ZOOM_XY / ZOOM_XY_DIV
	// [tiles]       = map tiles = [pixels] / TILE_WH

	// Game to screen zoom [screen pixels] / [game pixels]...
	final static int ZOOM_X		= 1; // zoom in
	final static int ZOOM_Y		= 1;
	final static int ZOOM_X_DIV = 1; // zoom out
	final static int ZOOM_Y_DIV = 1;

	// Screen view [pixels]...
	final static int SV_W 		= SCR_W;			// screen view width
	final static int SV_H 		= SCR_H;			// screen view height
	final static int SV_X 		= 0;				// screen view position x
	final static int SV_Y 		= 0;				// screen view position y

	// Game view [game pixels]...
//	final static int GV_X 		=					// _nGV_X = -(_level.x >> 8)
//	final static int GV_Y 		=					// _nGV_Y = -(_level.y >> 8)
	final static int GV_W 		= SV_W * ZOOM_X_DIV / ZOOM_X;	// game view width
	final static int GV_H 		= SV_H * ZOOM_Y_DIV / ZOOM_Y;	// game view height

	//////////////////////////////////////////////////
	// Localisation...

	final static boolean CHINESE_VERSION = false;

	//////////////////////////////////////////////////

	//...
}

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
