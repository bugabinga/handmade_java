import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * Demonstrates, that painting a component, also paints its upper hierarchy (parent).
 * So while it might seem, that a paint method should do "nothing", visible effects can
 * occur.
 * @see <a href="https://pavelfatin.com/low-latency-painting-in-awt-and-swing/">Low Latency Palonging in AWT and Swing by Pavel Fatin</a>
 * @see <a href="https://github.com/pavelfatin/low-latency-painting-in-awt-and-swing/blob/master/ComponentOpacity.java">Source</a>
 */
public class ComponentOpacity
{
private static boolean drawing_enabled = true;
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
        if (drawing_enabled)
        {
          gfx.setColor(Color.BLACK);
          gfx.fillRect(0, 0, getWidth(), getHeight());
          gfx.setColor(Color.WHITE);
          gfx.drawString("Press 1 to paint nothing in internal area", 7, 40);
          gfx.drawString("Press 2 to repaint the content", 7, 67);
        }
      }
    };
    content.setFocusable(true);
	  content.addKeyListener(new KeyAdapter()
		{
      @Override public void keyPressed(KeyEvent event)
      {
        if (event.getKeyCode() == KeyEvent.VK_1)
        {
	        /*
	         * Even though we disable drawing, we see an effect.
	         */
          drawing_enabled = false;
          content.paintImmediately(87, 87, 175, 175);
          drawing_enabled = true;
        }
        else if (event.getKeyCode() == KeyEvent.VK_2)
        {
          content.repaint();
        }
      }
    });
    var parent = new JPanel(new BorderLayout());
    parent.setBackground(Color.GREEN);
    parent.add(content, BorderLayout.CENTER);
    var frame = new JFrame("Component opacity");
    frame.getContentPane().add(parent);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(400, 300));
    frame.pack();
    frame.setVisible(true);
  });
}
}
