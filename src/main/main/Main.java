package main;
import static java.lang.System.out;
import java.awt.*;
/**
 * The goal is to start up a grid GUI as quickly as possible.
 * The grid is adaptive (responsive design) in size and form to the screen
 * and allows to rearrange the grid tiles.
 * Every tile hosts an "app". A program embedded into the grid.
 * Apps can be created by humans directly in the grid or aquired in the market.
 */
class Main {
	/**
   * Welcome to the entry point of <b>handmade_java</b>!
	 * @param args ignored
 	 */
  public static void main(String[] args){
    out.println("I AM APPLICATION");

		var graphics_environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//TODO: use all monitors
		var screen = graphics_environment.getDefaultScreenDevice();
		var window = new Frame("THE_GRID", screen.getDefaultConfiguration());
		screen.setFullScreenWindow(window);
		
  }
}
