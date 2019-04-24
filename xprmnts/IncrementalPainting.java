import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Demonstrates the potetntial performance inprovements to be gained, by blitting over already drawn regions into
 * other regions.
 * @see <a href="https://pavelfatin.com/low-latency-painting-in-awt-and-swing/">Low Latency Painting in AWT and Swing by Pavel Fatin</a>
 * @see <a href="https://github.com/pavelfatin/low-latency-painting-in-awt-and-swing/blob/master/IncrementalPainting.java">Source</a>
 */
public class IncrementalPainting
{
/**
 * @param args ignored
 */
public static void main(String[] args)
{
  SwingUtilities.invokeLater(() ->
	{
    var content = new JComponent()
    {
      @Override public void paint(Graphics g)
      {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawString("Press 1 to draw some text", 7, 20);
        g.drawString("Press 2 to clone the text area", 7, 37);
      }
    };
    content.setOpaque(true);
    content.setFocusable(true);
    content.addKeyListener(new KeyAdapter()
		{
      @Override public void keyPressed(KeyEvent event)
      {
        if (event.getKeyCode() == KeyEvent.VK_1 || event.getKeyCode() == KeyEvent.VK_2)
        {
	        var g = content.getGraphics();
	        var font = g.getFont();
          long before = System.nanoTime();
          if (event.getKeyCode() == KeyEvent.VK_1)
          {
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
            g.setColor(Color.RED);
            g.drawString("Some text here", 20, 100);
            g.setFont(new Font(Font.SERIF, Font.BOLD, 12));
            g.setColor(Color.GREEN.darker());
            g.drawString("Some text here", 20, 130);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            g.setColor(Color.BLUE);
            g.drawString("Some text here", 20, 160);
          }
          else
          {
	          //This amazing draw call allows us to copy and translate an already drawn area!
            g.copyArea(10, 70, 120, 120, 130, 0);
          }
          long delay = (System.nanoTime() - before) / 1_000;
          g.setFont(font);
	        g.setColor(Color.WHITE);
          g.fillRect(7, 44, 200, 10);
          g.setColor(Color.BLACK);
          g.drawString("Delay: " + delay +"µs", 7, 54);
        }
      }
    });
    var frame = new JFrame("Incremental painting");
    frame.getContentPane().add(content);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(350, 350));
    frame.pack();
    frame.setVisible(true);
  });
}
}
