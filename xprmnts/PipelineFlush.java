import static java.lang.System.out;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
public class PipelineFlush
{
private static double our_delay = 0.0;
public static void main(String[] args) throws Exception
{
  System.setProperty("sun.java2d.opengl", "true"); // optional
	//System.setProperty("sun.java2d.d3d", "true");
	//System.setProperty("sun.java2d.xrender", "true");
	out.printf("opengl: %s%nd3d: %s%nxrender: %s%n", System.getProperty("sun.java2d.opengl"), System.getProperty("sun.java2d.d3d"), System.getProperty("sun.java2d.xrender"));
  var gfx_env = GraphicsEnvironment.getLocalGraphicsEnvironment();
  out.println("center point:" + gfx_env.getCenterPoint());
  var main_screen = gfx_env.getDefaultScreenDevice();
  var robot = new Robot(main_screen);
  SwingUtilities.invokeLater(() -> run(robot,main_screen));
}
private static void run(Robot robot, GraphicsDevice screen)
{
  var executor_service = Executors.newSingleThreadExecutor();
	var gfx_config = screen.getDefaultConfiguration();
	out.println("bounds: "+ gfx_config.getBounds());
	var frame = new JFrame("Rendering pipeline flush", gfx_config);
  var content = new JComponent()
  {
    @Override public void paint(Graphics gfx)
    {
      gfx.setColor(Color.WHITE);
      gfx.fillRect(0, 0, getWidth(), getHeight());
      gfx.setColor(Color.BLACK);
      gfx.drawString("Press 1 to measure latency without flush", 7, 20);
      gfx.drawString("Press 2 to measure latency with flush", 7, 37);
      if (our_delay != 0.0)
			{
				gfx.drawString("Delay: " + our_delay, 7, 54);
      }
    }
	};
  content.setFocusable(true);
	content.addKeyListener(new KeyAdapter()
	{
    @Override public void keyPressed(KeyEvent event)
    {
      if (event.getKeyCode() == KeyEvent.VK_1 || event.getKeyCode() == KeyEvent.VK_2)
      {
        var bounds = frame.getBounds();
        boolean flush = event.getKeyCode() == KeyEvent.VK_2;
        our_delay = measurePaintingDelay(executor_service, content.getGraphics(), content.getSize(), flush, robot);
        content.repaint();
      }
    }
  });
  frame.getContentPane().add(content);
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  if (screen.isFullScreenSupported())
  {
	  frame.setResizable(false);
	  frame.setUndecorated(true);
	  screen.setFullScreenWindow(frame);
	}
	else
	{
		frame.setPreferredSize(new Dimension(350, 350));
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
}
private static long measurePaintingDelay(Executor executor, Graphics graphics, Dimension size, boolean flush, Robot robot)
{
  long before = System.nanoTime();
  executor.execute(() ->
	{
	  graphics.setColor(Color.GREEN);
    graphics.fillRect(0, 0, size.width, size.height);
    if (flush)
    {
      Toolkit.getDefaultToolkit().sync();
    }
  });
	Color color;
  do
  {
    color = robot.getPixelColor(10,10);
	  out.println(color);
  }
  while (!Color.GREEN.equals(color));
  return (System.nanoTime() - before) / 1_000;
}
}
