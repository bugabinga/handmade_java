import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Demonstrates the buffering of paint requests in AWTs drawing pipeline.
 * Two draw operations are interruptes by a pause.
 * The visual effect however, will seemingly update both operations at once, AFTER the pause.
 * @see <a href="https://pavelfatin.com/low-latency-painting-in-awt-and-swing/">Low Latency Painting in AWT and Swing by Pavel Fatin</a>
 * @see <a href="https://github.com/pavelfatin/low-latency-painting-in-awt-and-swing/blob/master/ShowingDelay.java">Source</a>
 */
public class ShowingDelay
{
private static boolean my_intermediate_pause = false;
private static long time_start_since_press = 0;
private static Color our_outer_color = Color.BLUE;
private static Color our_inner_color = Color.GREEN;
/**
 * @param args ignored
 */
public static void main(String[] args)
{
  SwingUtilities.invokeLater(() ->
	{
    var content = new JComponent()
    {
	    @Override public void paint(Graphics gfx)
			{
        gfx.setColor(Color.WHITE);
        gfx.fillRect(0, 0, getWidth(), getHeight());
        gfx.setColor(Color.BLACK);
        gfx.drawString("Press 1 re-draw the rectangles", 7, 40);
        //Even though this draw call is issued here, it will not paint immediatly.
        gfx.setColor(our_outer_color);
        gfx.fillRect(87, 87, 175, 175);
        if (my_intermediate_pause)
        {
	        //AWT buffers all paint requests in the current thread (EDT).
	        //That means any operation here contributes to painting latency.
          pause(1000);
          gfx.setColor(Color.BLACK);
          gfx.drawString("Time elapsed since press: " + ((System.nanoTime() - time_start_since_press) / 1_000_000) + "ms.", 7, 56);
        }
        gfx.setColor(our_inner_color);
        gfx.fillRect(137, 137, 75, 75);
			}
		};
		content.setOpaque(true);
		content.setFocusable(true);
		content.addKeyListener(new KeyAdapter()
		{
	    @Override public void keyPressed(KeyEvent event)
	    {
	      if (event.getKeyCode() == KeyEvent.VK_1)
	      {
	        var outer_color = our_outer_color;
	        our_outer_color = our_inner_color;
	        our_inner_color = outer_color;
	        my_intermediate_pause = true;
	        time_start_since_press = System.nanoTime();
	        content.paintImmediately(0, 0, content.getWidth(), content.getHeight());
	      }
	    }
	  });
	  var frame = new JFrame("Drawing order");
	  frame.getContentPane().add(content);
	  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	  frame.setPreferredSize(new Dimension(350, 350));
	  frame.pack();
	  frame.setVisible(true);
	});
}
private static void pause(int delay)
{
  try
  {
    Thread.sleep(delay);
  }
  catch (InterruptedException e)
  {
    throw new RuntimeException(e);
  }
}
}
