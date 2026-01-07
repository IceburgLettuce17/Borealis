import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import javax.microedition.lcdui.*;

public final class BorealisDemoGame extends MIDlet {

	public Display m_display;
	public static String s_gameVer = null;
	public Game m_game;
	
	public BorealisDemoGame() {
		s_gameVer = getAppProperty("MIDlet-Version");
		m_game = new Game(this);
		Game._sVersion = s_gameVer;
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		notifyDestroyed();
	}

	protected void pauseApp() {
		Game.Pause();
		notifyPaused();
	}

	protected void startApp() throws MIDletStateChangeException {
        if (this.m_display == null) {
            this.m_display = Display.getDisplay(this);
        }
        this.m_display.setCurrent(this.m_game);

	}
	
	// $FF: renamed from: a () void
	public final void quitApp() {
		resetApp();
		try {
			this.destroyApp(true);
		} 
		catch (MIDletStateChangeException e) {}
	}
	
	private void resetApp() {
		this.m_game = null;
		this.m_display = null;
		System.gc();
	}

}
