import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Demonstrates, that double buffering introduces a measurable overhead to the latency of the painting pipeline.
 * Curiously, the intended effect of showing, that a larger region painting "nothing" takes longer than a small region painting nothing, does not occur.
 * The reason is unknown.
 * For the record: OpenJDK 11 on Arch Linux in Wayland (sway), 04/2019.
 * On the other hand, the effect of turning of double buffering is very visible.
 * The time is roughly 2x larger with double buffering for both regions.
 * @see <a href="https://pavelfatin.com/low-latency-painting-in-awt-and-swing/">Low Latency Painting in AWT and Swing by Pavel Fatin</a>
 * @see <a href="https://github.com/pavelfatin/low-latency-painting-in-awt-and-swing/blob/master/BufferingOverhead.java">Source</a>
 */
public class BufferingOverhead
{
private static boolean	our_draw_content = true;
private static long			our_delay = 0;
private static boolean	is_dubbel_buffer = true;
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
        if (our_draw_content)
        {
          gfx.setColor(Color.BLACK);
          gfx.fillRect(0, 0, getWidth(), getHeight());
          gfx.setColor(Color.WHITE);
          gfx.drawString("Press 1 to paint nothing in 10x10 area", 7, 40);
          gfx.drawString("Press 2 to paint nothing in 1000x1000 area", 7, 67);
          gfx.drawString("Press 0 to toggle double buffering", 7, 97);
          if (our_delay != 0)
          {
	          gfx.drawString("Delay: " + our_delay + "µs", 7, 124);
	        }
	        gfx.drawString("Double Buffering:" + is_dubbel_buffer, 7, 152) ;
        }
      }
    };
    content.setOpaque(true);
    content.setFocusable(true);
    content.setPreferredSize(new Dimension(1000, 1000));
    content.addKeyListener(new KeyAdapter()
		{
      @Override public void keyPressed(KeyEvent event)
      {
        if (event.getKeyCode() == KeyEvent.VK_1 || event.getKeyCode() == KeyEvent.VK_2)
        {
	        /*
	         * Measure the time it takes to paint a small region vs. a large region.
	         * In both cases the update does "nothing".
	         */
          long before = System.nanoTime();
          our_draw_content = false;
          
					RepaintManager rm = RepaintManager.currentManager(content);
					boolean previous_dubbel_buffer = rm.isDoubleBufferingEnabled();
					rm.setDoubleBufferingEnabled(is_dubbel_buffer);
          if (event.getKeyCode() == KeyEvent.VK_1)
          {
            content.paintImmediately(0, 0, 10, 10);
          }
          else
          {
            content.paintImmediately(0, 0, content.getWidth(), content.getHeight());
          }
				  rm.setDoubleBufferingEnabled(previous_dubbel_buffer);
          our_draw_content = true;
          our_delay = (System.nanoTime() - before) / 1_000;
          content.repaint();
        }
        if (event.getKeyCode() == KeyEvent.VK_0)
        {
	        is_dubbel_buffer = !is_dubbel_buffer;
          content.repaint();
	      }
      }
    });
    var frame = new JFrame("Buffering overhead");
    frame.getContentPane().add(content);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  });
}
}
