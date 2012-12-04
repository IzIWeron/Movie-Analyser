import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.math.BigInteger;

public class GraphHelper {
	public static final Color COLOR_DEFAULT = Color.BLACK;

	public static HSBColor[] generateHSBColors(int n, float saturation, float brightness) {
		HSBColor[] cols = new HSBColor[n];
		for (int i = 0; i < n; i++)
			cols[i] = new HSBColor((float) i / (float) n, saturation, brightness);
		return cols;
	}

	/** Drawing preferences */
	public static void renderingHints(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
	}

	/** Returns the max value in given HSBColor array */
	public static BigInteger maxValue(HSBColor[] curCol) {
		BigInteger max = BigInteger.ZERO;
		for (int i = 1; i < curCol.length; i++)
			if (max.compareTo(curCol[i].count) == -1) // If cur value is bigger (Comparison)
				max = curCol[i].count;
		return max;
	}

	/** Draws a palette on the plot */
	public static void drawPalette(Graphics2D g, HSBColor[] palette, int xShift, int yShift, int paletteHeight) {
		for (int i = 0; i < palette.length; i++) {
			g.setColor(new Color(Color.HSBtoRGB(palette[i].hue, palette[i].saturation, palette[i].brightness)));
			g.drawLine(xShift + i, yShift, xShift + i, yShift + paletteHeight);
		}
	}

	/** Draws a border of the plot */
	public static void paintAxis(Graphics2D g, int xShift, int yShift, int xLength, int yLength) {
		g.setColor(COLOR_DEFAULT);
		g.drawLine(xShift, yShift, xShift + xLength, yShift); // Lower X Line
		g.drawLine(xShift, yShift, xShift, yShift - yLength); // Lower Y Line

		g.drawLine(xShift, yShift - yLength, xShift + xLength, yShift - yLength); // Upper X Line
		g.drawLine(xShift + xLength, yShift, xShift + xLength, yShift - yLength); // Upper Y Line

		g.setColor(Color.GRAY);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));

		// TODO Make another method, add X lines and check if we are building lin or log plot
		float dash[] = { 5.0f };
		g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f));

		for (int i = 1; i < 6; i++) {
			int x = (750 / 6) * i;
			g.drawLine(x + xShift, yShift, x + xShift, yShift - yLength);
		}

		g.setStroke(new BasicStroke());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}

	/** Draws a point on a linear plot */
	public static void paintCurPoint(Graphics2D g, int xShift, int yShift, int plotHeight, int xPos, BigInteger value, BigInteger max) {
		g.setColor(COLOR_DEFAULT);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.80f));

		BigInteger realHeight = value.multiply(new BigInteger(plotHeight + ""));
		realHeight = realHeight.divide(max.add(BigInteger.ONE));

		g.draw(new Line2D.Double(xShift + xPos, yShift - realHeight.intValue(), xShift + xPos, yShift));
	}

	/** Draws a point on a log plot */
	public static void logPaintCurPoint(Graphics2D g, int xShift, int yShift, int plotHeight, int xPos, double base, BigInteger value, int count) {
		g.setColor(COLOR_DEFAULT);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.80f));

		double a = Math.log10(value.doubleValue()) / Math.log10(base);
		a = plotHeight * (a / count);

		if (!value.equals(BigInteger.ZERO))
			g.draw(new Line2D.Double(xShift + xPos, yShift - a, xShift + xPos, yShift));
	}

	/** Draws a number near the Y line according to PROPORTION */
	public static void logPaintNumbers(Graphics2D g, double base, int count, int xShift, int yShift, int plotHeight) {
		g.setColor(Color.BLUE);
		Font font = new Font("Arial", Font.BOLD, 12);
		g.setFont(font);

		for (int i = 0; i <= count; i++) {
			String drawString = (int) Math.pow(base, i) + "";
			if (i == 0)
				drawString = "0";
			double width = g.getFontMetrics().getStringBounds(drawString, g).getWidth();
			g.drawString(drawString, xShift - (int) width, yShift - i * plotHeight / count);
		}
	}
}
