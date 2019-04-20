import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;

/**
 * Demonstrates the fact, that the asynch ing pipeline in AWT/Swing results in an unpredictable
 * drawing order.
 * @see <a href="https://pavelfatin.com/low-latency-painting-in-awt-and-swing/">Low Latency Palonging in AWT and Swing by Pavel Fatin</a>
 * @see <a href="https://github.com/pavelfatin/low-latency-painting-in-awt-and-swing/blob/master/QueueDelay.java">Source</a>
 */
public class QueueDelay
{
  public static void main(String[] __)
  {
	 	var random = ThreadLocalRandom.current();
    SwingUtilities.invokeLater(() ->
    {
			var our_background = new AtomicReference<>(Color.BLACK);
     	var key_press_time = new AtomicLong(0L);
	    var frame = new JFrame("Event queue delay")
		  {
        @Override
        public void paint (Graphics g) {
          g.setColor(our_background.get());
          g.fillRect(0, 0, getWidth(), getHeight());
          g.setColor(Color.WHITE);
          g.drawString("Press 1 to request a repaint", 24, 100);
          if (key_press_time.get() != 0L)
          {
	          g.drawString(String.format("Elapsed time since key press: %s ms.", Long.toString(System.currentTimeMillis() - key_press_time.get())), 24, 40);
	        }
        }
      };
      frame.addKeyListener(new KeyAdapter()
		  {
        @Override
        public void keyPressed(KeyEvent event)
        {
          if (event.getKeyCode() == KeyEvent.VK_1)
          {
	          var source = (Component) event.getSource();
	         	key_press_time.set(System.currentTimeMillis());
	          /*
	           * By invoking the pause later, one might think it would happen, after the repaint.
	           * It does, however, not.
	           */
            SwingUtilities.invokeLater(() -> pause(500));
            our_background.set(Color.BLACK.equals(our_background.get()) ? new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)): Color.BLACK);
            frame.repaint();
            /*
             * Again, the pause statement placed after the repaint call, normally suggests, that it would happen afterwards.
					   * Not so in this case.
					   * The asynchrounous reordering of paint calls in AWT/Swing necessitates the buffering of all instructions on the EDT.
					   * One helpful mental model might be: think "requestPaint" instead of "repaint".
	           */
            pause(500);
          }
        }
      });
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setPreferredSize(new Dimension(400,300));
      frame.pack();
      frame.setLocationByPlatform(true);
      frame.setVisible(true);
    });
  }
  private static void pause(long delay)
  {
    try
    {
      Thread.sleep(delay);
    }
    catch (InterruptedException __)
    {
      throw new RuntimeException(__);
    }
  }
}

