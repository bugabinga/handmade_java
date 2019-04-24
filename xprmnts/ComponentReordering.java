import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
/**
 * Demonstrates, that the order of incoming paint requests does not guarantee tho execution order of those requests.
 * This example might need to be run multiple times to see the effect, because the exact ordering seems platform dependent.
 * That of course means, that if your JVM+platform combo happens to order paint request in insertion order, this example will
 * never demonstrate the "wrong" order. 
 * @see <a href="https://pavelfatin.com/low-latency-painting-in-awt-and-swing/">Low Latency Painting in AWT and Swing by Pavel Fatin</a>
 * @see <a href="https://github.com/pavelfatin/low-latency-painting-in-awt-and-swing/blob/master/ComponentReordering.java">Source</a>
 */
public class ComponentReordering
{
private static java.util.List<String> ourPaintingOrder = new ArrayList<>();
/**
 * @param args ignored
 */
public static void main(String[] args)
{
  SwingUtilities.invokeLater(() ->
	{
    var frame = new JFrame("Component painting order");
    var left = new JComponent()
    {
      @Override public void paint(Graphics gfx)
      {
	      gfx.setColor(Color.GREEN);
        gfx.fillRect(0, 0, getWidth(), getHeight());
        ourPaintingOrder.add("Left");
      }
    };
    left.setPreferredSize(new Dimension(10, 10));
    var right = new JComponent()
    {
      @Override public void paint(Graphics gfx)
      {
        gfx.setColor(Color.BLUE);
        gfx.fillRect(0, 0, getWidth(), getHeight());
        ourPaintingOrder.add("Right");
      }
    };
    right.setPreferredSize(new Dimension(10, 10));
    var content = new JPanel()
    {
      @Override public void paintComponent(Graphics gfx)
      {
        gfx.setColor(Color.BLACK);
        gfx.fillRect(0, 0, getWidth(), getHeight());
        gfx.setColor(Color.WHITE);
        gfx.drawString("Press 1 to request repaints: left, right", 17, 40);
      }
    };
    content.setLayout(new BorderLayout());
    content.add(left, BorderLayout.WEST);
    content.add(right, BorderLayout.EAST);
    content.setFocusable(true);
    content.addKeyListener(new KeyAdapter()
		{
      @Override public void keyPressed(KeyEvent event)
      {
        if (event.getKeyCode() == KeyEvent.VK_1)
        {
          ourPaintingOrder.clear();
          /*
           * Here we seemingly specify the order of repaints.
           * However, the paint queue is free to reorder those calls, so the order cannot be guaranteed.
           */
          left.repaint();
          right.repaint();
          SwingUtilities.invokeLater(() ->
					{
            var gfx = content.getGraphics();
		        gfx.setColor(Color.WHITE);
            gfx.drawString("First: " + ourPaintingOrder.get(0), 17, 117);
	          gfx.drawString("Second: " + ourPaintingOrder.get(1), 17, 164);
	        });
	      }
		  }
	  });
	  frame.getContentPane().add(content);
	  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	  frame.setPreferredSize(new Dimension(400, 300));
	  frame.pack();
	  frame.setVisible(true);
	});
}
}
