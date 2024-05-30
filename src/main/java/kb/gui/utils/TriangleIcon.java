package kb.gui.utils;

import javax.swing.*;
import java.awt.*;

public class TriangleIcon implements Icon {

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int[] xc = {x + 0, x + 8, x + 4};
		int[] yc = {y + 0, y + 0, y + 10};
		g.setColor(Color.DARK_GRAY);
		g.fillPolygon(xc, yc, 3);
	}

	@Override
	public int getIconWidth() {
		return 8;
	}

	@Override
	public int getIconHeight() {
		return 10;
	}
}
