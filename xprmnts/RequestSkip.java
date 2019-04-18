import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * Demonstrates, that AWT/Swing drops requests to paint, when it deems it necessary.
 * For example, if the previously queued requests are taking too long to fullfill them all.
 * @see <a href="https://pavelfatin.com/low-l ing-in-awt-and-swing/">Low Latency Palonging in AWT and Swing by Pavel Fatin</a>
 * @see <a href="https://github.com/pavelfatin/low-latency-painting-in-awt-and-swing/blob/master/RequestSkip.java">Source</a>
 */
public class RequestSkip
{
private static volatile int myRequestsToPost;
private static volatile int myRequestCount;
private static int myPaintCount;
public static void main(String[] args)
{
  SwingUtilities.invokeLater(() ->
  {
    var content = new JComponent()
    {
      @Override
      public void paint(Graphics g)
      {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.drawString("Press 1 to request 100 repaints", 7, 20);
        g.drawString("Requests: " + myRequestCount, 7, 37);
        g.drawString("Repaints: " + myPaintCount, 7, 54);
        /*
         * Here we stall the paint longer, than we the interval in which we are requesting new paints...
         */
        pause(100);
        myPaintCount++;
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
          myRequestsToPost = 100;
        }
      }
    });
    var pool = Executors.newScheduledThreadPool(1);
    pool.scheduleAtFixedRate(() ->
		{
      if (myRequestsToPost > 0)
      {
        myRequestCount++;
        content.repaint();
        myRequestsToPost--;
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
    frame.setLocationByPlatform(true);
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
