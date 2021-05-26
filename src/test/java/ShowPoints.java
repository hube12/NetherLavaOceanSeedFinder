

import com.seedfinding.neil.Main;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.seedutils.rand.JRand;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ShowPoints {
	public static final int SIZE = 500;

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(new PointPanel());
		f.setSize(SIZE, SIZE);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
}

class PointPanel extends JPanel {

	public PointPanel() {
		setBackground(Color.white);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		JRand rand = new JRand(42);
		for (BPos pos : Main.POSITIONS) {
			if (pos != null) {
				Ellipse2D e = getEllipse(pos);
				Color color = getRandomColor(rand);
				g2.setPaint(color);
				g2.fill(e);
			}
		}
	}

	public static Ellipse2D getEllipse(BPos pos) {
		return new Ellipse2D.Double(ShowPoints.SIZE / 2.0 + pos.getX() - 3, ShowPoints.SIZE / 2.0 + pos.getZ() - 3, 6, 6);
	}

	public static Color getRandomColor(JRand rand) {
		float r = rand.nextFloat() / 2f + 0.5f;
		float g = rand.nextFloat() / 2f + 0.5f;
		float b = rand.nextFloat() / 2f + 0.5f;
		return new Color(r, g, b);
	}
}



