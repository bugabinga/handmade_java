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
class Main
implements WindowListener
{
	/**
   * Welcome to the entry point of <b>handmade_java</b>!
	 * @param args ignored
 	 */
  public static void main(String[] args)
  {
    out.println("I AM GRID");
		var graphics_environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//TODO: use all monitors
		var screen = graphics_environment.getDefaultScreenDevice();
		printInfoAboutDevice(screen);
		var window = new Frame("THE_GRID", screen.getDefaultConfiguration());
		window.setMenuBar(null);
		window.setResizable(false);
		window.setUndecorated(true);
		window.setExtendedState(Frame.MAXIMIZED_BOTH);
		window.setBackground(Color.BLACK);
		//TODO: will setting the image have some nice effect?
		window.setIconImage(null);
		window.addWindowListener(new Main());
		screen.setFullScreenWindow(window);
		printSupportedframeStates(window.getToolkit());
  }
  public void windowClosing(WindowEvent __)
  {
	  /*
	   * Cold shutdown.
	   * Let the OS worry about cleaning up the ressources.
	   */
	  System.exit(0);
  }
  public void windowClosed(WindowEvent __)
  {
	  //TODO: something useful here...
  }
  public void windowOpened(WindowEvent __)
  {
	  //TODO: something useful here...
  }
  public void windowIconified(WindowEvent __)
  {
	  //TODO: something useful here...
  }
  public void windowDeiconified(WindowEvent __)
  {
	  //TODO: something useful here...
  }
  public void windowActivated(WindowEvent __)
  {
	  //TODO: something useful here...
  }
  public void windowDeactivated(WindowEvent __)
  {
	  //TODO: something useful here...
  }
  /**
   * Print the state of a given graphics device to standard out.
   * @param device Any graphics device.
   */
  static void printInfoAboutDevice(GraphicsDevice device)
  {
	  var modes = device.getDisplayModes();
	  var configs = device.getConfigurations();
	  var type = device.getType();
		out.printf("Graphics Device: %s\n", device);
		out.printf("               : Available Accelerated Memory: %d bytes\n", device.getAvailableAcceleratedMemory());
		out.printf("							 : ID: %s\n", device.getIDstring());
		out.printf("               : Display Change Supported? %s\n",device.isDisplayChangeSupported());
		out.printf("               : Exclusive FullScreen Supported? %s\n", device.isFullScreenSupported());
		out.printf("               : Window Translucency Supported (per pixel)? %s\n", device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT));
		out.printf("               : Window Translucency Supported (per pixel on or off)? %s\n", device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT));
		out.printf("               : Window Translucency Supported (all pixels)? %s\n", device.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT));
		switch (type)
		{
			case GraphicsDevice.TYPE_RASTER_SCREEN:
				{
					out.println("               : Device is a raster screen.");
				}
			break;
			case GraphicsDevice.TYPE_PRINTER:
				{
					out.println("               : Device is a printer.");
				}
			break;
			case GraphicsDevice.TYPE_IMAGE_BUFFER:
				{
					out.println("               : Device is an image buffer.");
				}
			break;
		}
		out.printf("               : Found %d display modes.\n", modes.length);
		for (var mode : modes)
		{
			out.printf("               : Display Mode: %s\n", mode);
		}
		out.printf("               : Found %d graphics configurations.\n", configs.length);
		for (var conf : configs)
		{
			out.printf("               : Graphics Configuration: %d\n",indexOf(configs,conf));
			out.printf("               :                       : Bounds: %s\n", conf.getBounds());
			var buffer_capas = conf.getBufferCapabilities();
			out.println("               :                       : Buffer Capabilities:");
			out.printf("               :                       :                    : FullScreen required? %s\n", buffer_capas.isFullScreenRequired());
			out.printf("               :                       :                    : MultiBuffer available? %s\n", buffer_capas.isMultiBufferAvailable());
			out.printf("               :                       :                    : Page flipping? %s\n", buffer_capas.isPageFlipping());
			out.printf("               :                       :                    : Front Buffer Capabilities: %s\n", formatImageCapabilities(buffer_capas.getFrontBufferCapabilities()));
			out.printf("               :                       :                    : Back Buffer Capabilities:  %s\n", formatImageCapabilities(buffer_capas.getBackBufferCapabilities()));
			out.printf("               :                       :                    : Flip Contents:  %s\n", buffer_capas.getFlipContents());
			//TODO: print detailed colormodel
			out.printf("               :                       : Color Model: %s\n", conf.getColorModel());
			out.printf("               :                       : Default Transform: %s\n", conf.getDefaultTransform());
			out.printf("               :                       : Image Capabilities: %s\n", formatImageCapabilities(conf.getImageCapabilities()));
			out.printf("               :                       : Normalizing Transform: %s\n", conf.getNormalizingTransform());
		}
  }
  /**
   * Format state of @{link ImageCapabilities} into a string.
   * @param Some image capabilities.
   * @return String of image capabilities state.
   */
  static String formatImageCapabilities(ImageCapabilities capas)
  {
	  return String.format("accelerated=%s, volatile=%s", capas.isAccelerated(), capas.isTrueVolatile());
  }
  /**
   * Finds the index of some object in some array.
   * If the array does not contain the object, returns -1.
   * Attention: This method uses a reference equality check, because it is intended to be used while iterating over known arrays.
   * @param array Some array. Cannot be NULL.
   * @param key Some object to search for. Cannot be NULL.
   * @return The index of `key`, or -1 if `key` could not be found.
   */
  static int indexOf(Object[] array, Object key)
  {
	  assert array != null;
	  assert key != null;
	  
	  int index = 0;
	  int size = array.length;
	  while (index < size)
	  {
		  if(array[index] == key) return index;
		  index = index + 1;
	  }
	  assert index == size;
	  return -1;
  }
  /**
   * Print the frame states (@{link Frame#setExtendedState(int)}) supported by
   * the current system to standard out.
   * @param tk The @{link Toolkit} of some @{link Window}.
   */
	static void printSupportedframeStates(Toolkit tk)
	{
	  if (tk.isFrameStateSupported(Frame.ICONIFIED))
	  {
		  out.println( "Your system supports ICONIFIED.");
	  }
	  else
	  {
      out.println( "Your system doesn't support ICONIFIED.");
	  }
	  if (tk.isFrameStateSupported(Frame.MAXIMIZED_VERT))
	  {
		  out.println( "Your system supports MAXIMIZED_VERT.");
	  }
	  else
	  {
      out.println( "Your system doesn't support MAXIMIZED_VERT.");
	  }
	  if (tk.isFrameStateSupported(Frame.MAXIMIZED_HORIZ))
	  {
		  out.println( "Your system supports MAXIMIZED_HORIZ.");
	  }
	  else
	  {
      out.println( "Your system doesn't support MAXIMIZED_HORIZ.");
	  }
	  if (tk.isFrameStateSupported(Frame.MAXIMIZED_BOTH))
	  {
      out.println( "Your system supports MAXIMIZED_BOTH.");
	  }
	  else
	  {
      out.println( "Your system doesn't support MAXIMIZED_BOTH.");
	  }
  }
}
