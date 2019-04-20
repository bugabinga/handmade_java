import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
/**
 * Demonstrates, that AWT/Swing drops requests to paint, when it deems it necessary.
 * For example, if the previously queued requests are taking too long to fullfill them all.
 * @see <a href="https://pavelfatin.com/low-l ing-in-awt-and-swing/">Low Latency Palonging in AWT and Swing by Pavel Fatin</a>
 * @see <a href="https://github.com/pavelfatin/low-latency-painting-in-awt-and-swing/blob/master/RequestSkip.java">Source</a>
 */
public class RequestSkip
{
private static volatile int my_request_to_post;
private static volatile int my_request_count;
private static int my_paint_count;
/**
 * @param args ignored
 */
public static void main(String[] args)
{
  SwingUtilities.invokeLater(() ->
  {
    var content = new JComponent()
    {
      @Override
      public void paint(Graphics gfx)
      {
        gfx.setColor(Color.BLACK);
        gfx.fillRect(0, 0, getWidth(), getHeight());
        gfx.setColor(Color.WHITE);
        gfx.drawString("Press 1 to request 100 repaints", 7, 20);
        gfx.drawString("Requests: " + my_request_count, 7, 37);
        gfx.drawString("Repaints: " + my_paint_count, 7, 54);
        /*
         * Here we stall the paint longer, than we the interval in which we are requesting new paints...
         */
        pause(100);
        my_paint_count++;
      }
    };
    content.setFocusable(true);
    content.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent event)
      {
        if (event.getKeyCode() == KeyEvent.VK_1)
        {
          my_request_to_post = 100;
        }
      }
    });
    var pool = Executors.newScheduledThreadPool(1);
    pool.scheduleAtFixedRate(() ->
		{
      if (my_request_to_post > 0)
      {
        my_request_count++;
        content.repaint();
        my_request_to_post--;
      }
    }, 0, 50, TimeUnit.MILLISECONDS);
    var frame = new JFrame("Painting request skip");
    frame.addWindowListener(new WindowAdapter()
		{
      @Override
      public void windowClosed(WindowEvent __)
      {
        pool.shutdown();
      }
    });
    frame.getContentPane().add(content);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(400, 300));
    frame.pack();
    frame.setVisible(true);
  });
}
private static void pause(int delay)
{
  try
  {
    Thread.sleep(delay);
  } catch (InterruptedException exception)
  {
    throw new RuntimeException(exception);
  }
}
}
