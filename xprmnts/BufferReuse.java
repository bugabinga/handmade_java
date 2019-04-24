import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/*
 * Demonstrates, that reusing the back buffer of the double buffer strategy used by AWT/Swing is unreliable.
 * Depending on your OS+JVM+WM combo, this experiment might show, that painting a smaller region while invalidating
 * a larger encompassing one:
 * - will reuse the back buffer (meaning the previously drawn stuff is preserved perfectly).
 * - will clear the larger region before drawing the smaller one.
 * - will fill the larger region with garbage, before drawing the smaller one.
 */
public class BufferReuse
{
private static boolean draw_squares = false;
/**
 * @param args ignored
 */
public static void main(String[] args)
{
  SwingUtilities.invokeLater(() ->
	{
    var right = new JComponent()
    {
      @Override public void paint(Graphics gfx)
      {
        if (draw_squares)
        {
          gfx.setColor(Color.GREEN);
          gfx.fillRect(0, getHeight() - 10, 10, 10);
          gfx.fillRect(getWidth() - 10, 0, 10, 10);
        }
        else
        {
          gfx.setColor(Color.LIGHT_GRAY);
          gfx.fillRect(0, 0, getWidth(), getHeight());
          gfx.setColor(Color.BLUE);
          var defaultFont = gfx.getFont();
          gfx.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
          gfx.drawString("Right", 10, getHeight() - 10);
          gfx.setFont(defaultFont);
        }
      }
    };
    right.setOpaque(true);
    right.setPreferredSize(new Dimension(350, 350));
    var left = new JComponent()
    {
      @Override public void paint(Graphics gfx)
      {
        gfx.setColor(Color.WHITE);
        gfx.fillRect(0, 0, getWidth(), getHeight());
        gfx.setColor(Color.BLUE);
        Font defaultFont = gfx.getFont();
        gfx.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        gfx.drawString("Left", 10, getHeight() - 10);
        gfx.setFont(defaultFont);
        gfx.setColor(Color.BLACK);
        gfx.drawString("Press 1 to paint squares in the right area", 7, 40);
        gfx.drawString("Press 2 to repaint the content", 7, 57);
      }
    };
    left.setOpaque(true);
    left.setFocusable(true);
    left.setPreferredSize(new Dimension(350, 350));
    left.addKeyListener(new KeyAdapter()
		{
      @Override public void keyPressed(KeyEvent event)
      {
        if (event.getKeyCode() == KeyEvent.VK_1)
        {
          left.paintImmediately(0, 0, left.getWidth(), left.getHeight());
          //Here we invalidate the whole region, but inside the draw call, only a smaller region inside
          //will be updated.
          draw_squares = true;
          right.paintImmediately(0, 0, right.getWidth(), right.getHeight());
          draw_squares = false;
        }
        else if (event.getKeyCode() == KeyEvent.VK_2)
        {
          left.repaint();
          right.repaint();
        }
      }
    });
    var  parent = new JPanel();
    parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
    parent.add(left);
    parent.add(right);
    var frame = new JFrame("Buffer reuse");
    frame.getContentPane().add(parent);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  });
}
}
