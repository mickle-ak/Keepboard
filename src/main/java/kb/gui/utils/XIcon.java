package kb.gui.utils;

import javax.swing.*;
import java.awt.*;

public class XIcon implements Icon {
	
	private final ButtonModel model;
	
	public XIcon(ButtonModel model) {
		this.model = model;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (model.isRollover()) {
			paintIconRollover(g, x, y);
		} else {
			paintIconUsual(g, x, y);
		}
	}

	private void paintIconUsual(Graphics g, int x, int y) {
		g.setColor(Color.BLACK);
		g.drawLine(x + 2, y + 2, x + 6, y + 6);
		g.drawLine(x + 6, y + 2, x + 2, y + 6);
	}

	private void paintIconRollover(Graphics g, int x, int y) {
		g.setColor(Color.RED);
		
		int[] xc = {x + 0, x + 7, x + 8, x + 1};
		int[] yc = {y + 1, y + 8, y + 7, y + 0};
		g.fillPolygon(xc, yc, 4);
		
		xc = new int[] {x + 7, x + 0, x + 1, x + 8};
		yc = new int[] {y + 0, y + 7, y + 8, y + 1};
		g.fillPolygon(xc, yc, 4);
	}

	@Override
	public int getIconWidth() {
		return 8;
	}

	@Override
	public int getIconHeight() {
		return 8;
	}
}
