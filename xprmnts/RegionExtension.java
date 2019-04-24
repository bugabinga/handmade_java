import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
/**
 * Demonstrates the fact, that the automatic batching of paint requests in the AWT/Swing
 * drawing pipeline can lead to unoptimal results.
 * This example shows the update of two small paint regions, that lie far apart.
 * The result of batching merges the small regions, so that the interim space gets updated
 * as well.
 * In the worst case, this can mean repainting the whole screen.
 * @see <a href="https://pavelfatin.com/low-latency-painting-in-awt-and-swing/">Low Latency Painting in AWT and Swing by Pavel Fatin</a>
 * @see <a href="https://github.com/pavelfatin/low-latency-painting-in-awt-and-swing/blob/master/RegionExtension.java">Source</a>
 */
public class RegionExtension
{
private static Color mySquareColor = Color.BLUE;
/**
 * @param args ignored
 */
public static void main(String[] args)
{
  SwingUtilities.invokeLater(() ->
	{
    JFrame frame = new JFrame("Painting area extension")
    {
      @Override
      public void paint(Graphics gfx)
      {
        gfx.setColor(Color.BLACK);
        gfx.fillRect(0, 0, getWidth(), getHeight());
        Rectangle r = gfx.getClipBounds();
        String bounds = String.format("Last painting bounds: (%d, %d, %d, %d)", r.x, r.y, r.width, r.height);
        gfx.setColor(Color.WHITE);
        gfx.drawString("Press 1 to request repaints of the squares", 7, 40);
        gfx.drawString(bounds, 7, 80);
        gfx.setColor(mySquareColor);
        gfx.fillRect(0, getHeight() - 10, 10, 10);
        gfx.fillRect(getWidth() - 10, 0, 10, 10);
      }
    };
    frame.addKeyListener(new KeyAdapter()
		{
      @Override
      public void keyPressed(KeyEvent event)
      {
        if (event.getKeyCode() == KeyEvent.VK_1)
        {
          mySquareColor = Color.BLUE.equals(mySquareColor) ? Color.GREEN : Color.BLUE;
          /*
           * Here we request the painting of two specific (smaller) regions from the overall region.
           * However, since paint requests get batched and "optimized", it is possible (likely?), that
           * painting regions get merged into one.
           * That might optimize the painting (less paint calls) but it also might slow it down (too large paint region).
           */
          frame.repaint(0, frame.getHeight() - 10, 10, 10);
          frame.repaint(frame.getWidth() - 10, 0, 10, 10);
        }
      }
    });
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(400, 300));
    frame.pack();
    frame.setVisible(true);
  });
}
}
