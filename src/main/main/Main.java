package main;
import static java.lang.System.out;
import java.awt.*;
import java.awt.event.*;
/**
 * The goal is to start up a grid GUI as quickly as possible.
 * The grid is adaptive (responsive design) in size and form to the screen
 * and allows to rearrange the grid tiles.
 * Every tile hosts an "app". A program embedded into the grid.
 * Apps can be created by humans directly in the grid or aquired in the market.
 */
final class Main
implements WindowListener
{
private static final Main app = new Main();
/**
 * Welcome to the entry point of <b>handmade_java</b>!
 * Attempts to start the app in fullscreen mode, falling back to maximized mode otherwise.
 * @param args ignored
 */
public static void main(String[] args)
{
	var graphics_environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
	//TODO: use all monitors
	var screen = graphics_environment.getDefaultScreenDevice();
	var window = new Frame("THE_GRID", screen.getDefaultConfiguration());
	//TODO: we do not need a menu bar. check if nulling it achieves something.
	window.setMenuBar(null);
	window.setBackground(Color.BLACK);
	//TODO: will setting the image have some nice effect?
	window.setIconImage(null);
	window.addWindowListener(app);
	//Enter fullscreen mode, to give us more control over the rendering pipeline.
	//In this mode, our application needs not be coordinated with the window manager/compositor
	//of the OS, which gives us more room for performance and a simpler pipeline.
	//fullscreen mode seems to set the window visible.
	if (screen.isFullScreenSupported())
	{
		//A fullscreen app does neither need decorations by the OS nor the ability to be resized.
	  //Especially `resizable` needs to be `false` for us to stay on the active rendering path.
		window.setResizable(false);
		window.setUndecorated(true);
		screen.setFullScreenWindow(window);
	}
	else
	{
		//set the window to "maximized" as a fallback, in case fullscreen is not supported.
		window.setExtendedState(Frame.MAXIMIZED_BOTH);
		window.setVisible(true);
	}
}
@Override public void windowClosing(WindowEvent __)
{
   //Cold shutdown.
   //Let the OS worry about cleaning up the ressources.
  System.exit(0);
}
@Override public void windowClosed(WindowEvent __)
{
  //TODO: something useful here...
}
@Override public void windowOpened(WindowEvent __)
{
  //TODO: something useful here...
}
@Override public void windowIconified(WindowEvent __)
{
  //TODO: something useful here...
}
@Override public void windowDeiconified(WindowEvent __)
{
  //TODO: something useful here...
}
@Override public void windowActivated(WindowEvent __)
{
  //TODO: something useful here...
}
@Override public void windowDeactivated(WindowEvent __)
{
  //TODO: something useful here...
}
}
