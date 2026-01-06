// DBG.java - generic debug system
////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  Author(s): Ionut Matasaru (ionut.matasaru@gameloft.com)
//
////////////////////////////////////////////////////////////////////////////////////////////////////
//
// To exclude debug code in the release build set bErr, bDbgO, bFPS and bMem to false in DEF class.
// All variables and function calls will be removed by ofuscator (e.g. JShrink).
// Usage:
//	if (DEF.bErr)     DBG.CatchException(e, "description");	// in each exception catch()
//	if (DEF.bDbgInfo) DBG.DrawDebugInfo(g); 				// in main paint() method
//
////////////////////////////////////////////////////////////////////////////////////////////////////

import javax.microedition.lcdui.*;

////////////////////////////////////////////////////////////////////////////////////////////////////

class DBG
{
	// Exceptions...
	private static Exception _exception;	//	= null;
	private static String    _error;		//	= null;

	// FPS
	private static int  _fps;				//	= 0;
	private static int  _fps_count;			//	= 0;
	private static long _last_fps_tick;		//	= 0;
//	private static long _last_tick;			//	= 0;

	// Free memory...
	private static long _free_mem;			//	= 0;

////////////////////////////////////////////////////////////////////////////////////////////////////

	static void CatchException(Exception e, String s)
	{
		if (_exception == null)
		{
			_exception = e;
			_error = s;
		}
		if (DEF.bDbgO) System.out.println("ERROR: " + s);
	//	if (DEF.bDbgO) System.out.println(e.toString());
		if (DEF.bDbgO) e.printStackTrace();
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	static void ClearException()
	{
		_exception = null;
		_error = null;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	static void ASSERT(boolean expr, String msg)
	{
		if (!expr) System.out.println("ASSERTION FAILED: " + msg);
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

	static void DrawDebugInfo(Graphics g)
	{
		Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);

		if (DEF.bClippingBug)
		{
			g.setClip(0, 0, DEF.SCR_W, DEF.SCR_H);
			g.clipRect(0, 0, DEF.SCR_W, DEF.SCR_H);
		}
		else g.setClip(0, 0, DEF.SCR_W, DEF.SCR_H);

		if (DEF.DbgDrawFPS)	// FPS
		{
			_fps_count++;
			long tick = System.currentTimeMillis();
			if (tick - _last_fps_tick > 1000)
			{
				_fps = (_fps_count * 10000) / (int)(tick - _last_fps_tick);
				_last_fps_tick = tick;
			/*
				if (tick - _last_fps_tick > 2000)
				{
					_last_fps_tick = tick;
					_fps = 0;
				}
				else
				{
					_last_fps_tick += 1000;
					_fps = fps_count;
				}
			*/
				_fps_count = 0;
			}
			g.setColor(0x000000);
			g.fillRect(0, 0, 30, 18);
			g.setFont(font);
			g.setColor(0xFF7F00);
			g.drawString("" + _fps, 1, 0, Graphics.LEFT | Graphics.TOP);
		}

		if (DEF.DbgDrawMem)	// free memory
		{
		//	if ((nFrameCounter % 10) == 0)
			{
			//	System.gc();
				_free_mem = Runtime.getRuntime().freeMemory() / 1024;   // [Kb]
			}
			g.setColor(0x000000);
			g.fillRect(DEF.SCR_W-30, 0, 30, 18);
			g.setFont(font);
			g.setColor(0xFF7F00);
			g.drawString("" + _free_mem + "K", DEF.SCR_W, 0, Graphics.RIGHT | Graphics.TOP);
		}
/*
		if (cDef.bLimitFPS)
		{
			// Framerate limitaion...
			long delta = (1000/cDef.FPS)+last_tick-System.currentTimeMillis();
			if (delta > 0) try { Thread.sleep(delta); } catch(Exception e) {}
			last_tick = System.currentTimeMillis();
		}
*/
		if (DEF.DbgDrawFrameNr)
		{
			g.setFont(font);
			g.setColor(0x000000);
			g.fillRect(DEF.SCR_W/2-20, 0, 40, 18);
			g.setColor(0xFF7F00);
			g.drawString(Integer.toString(Game._gameCounter), DEF.SCR_W/2, 0, Graphics.HCENTER | Graphics.TOP);
		}

		if (DEF.bErr)	// exceptions
		{
			if ((_error != null) || (_exception != null))
			{
				g.setFont(font);
				g.setColor(0xFFFF0000);
				g.drawString("v" + Game._sVersion,   5,  5, Graphics.LEFT | Graphics.TOP);
				g.drawString(_error,                 5, 20, Graphics.LEFT | Graphics.TOP);
				g.drawString("[" + _exception + "]", 5, 35, Graphics.LEFT | Graphics.TOP);
			}
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////
// Memory Statistics...

	private static long	_mem_free;	// = 0;
	private static long	_mem_alloc;	// = 0;
	private static int	_mem_cnt;	// = 0;

////////////////////////////////////////////////////////////////////////////////////////////////////

	static void TraceMem(String msg)
	{
		System.gc();
		long mem = Runtime.getRuntime().freeMemory();

		if (_mem_cnt == 0)
		{
			_mem_free = mem;
			long total_mem = Runtime.getRuntime().totalMemory();
			System.out.println(">>> TraceMem0 TOTAL_MEM: " + total_mem);
		}

		_mem_alloc = _mem_free - mem;
		_mem_free = mem;
		_mem_cnt++;

		System.out.println(">>> TraceMem" + _mem_cnt + " FREE: " + _mem_free + " ALLOC: " + _mem_alloc + " >>> " + msg);

	//	int max_free_mem_block = DBG.GetMaxFreeMemBlock();
	//	System.out.println("    MaxFreeMemBlock: " + max_free_mem_block + " (" + (max_free_mem_block/1024) + "K)");
	}

////////////////////////////////////////////////////////////////////////////////////////////////////
// returns the largest memory block that can be allocated with "new byte[]"

	static int GetMaxFreeMemBlock()
	{
		int mem1 = 0;
		int mem2 = 4*1024*1024; // 2 * (int)Runtime.getRuntime().totalMemory();

		int count = 0;

		byte[] buff = null;
		System.gc();

		// binary search...
		do
		{
			int mem = (mem1 + mem2) / 2;

			try
			{
				buff = new byte[mem];

				mem1 = mem;
			}
			catch (Error e)
			{
				mem2 = mem;
			}

			// forced dependency for 'buff', so it will not be removed by obfuscator
			if (buff != null)
			{
				count++;
				buff = null;
				System.gc();
			}
		}
		while (mem2 - mem1 > 256);

	//	System.out.println("    count: " + count);
		return (count == 0) ? -1 : mem1;
	}
	
	//////////////
	/// MODDED ///
	/////////////
	public static void Log(String msg)
	{
		if (DEF.bDbgO || DEF.bEmu)
		{
			System.out.println(msg);
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////

} // class DBG

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
