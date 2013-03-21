import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.math.BigInteger;

import movie.util.NumberInstance;

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
		for (int i = 0; i < curCol.length; i++)
			if (max.compareTo(curCol[i].count) == -1) // If cur value is bigger (Comparison)
				max = curCol[i].count;
		return max;
	}

	/** Draws a palette on the plot */
	public static void drawPalette(Graphics2D g, HSBColor[] palette, int xShift, int yShift, int paletteHeight) {
		for (int i = 0; i < palette.length; i++) {
			g.setColor(new Color(Color.HSBtoRGB(palette[i].hue, palette[i].saturation, 0.8f)));
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
	}

	public static void axisDelimeter(Graphics2D g, int plotHeight, int xShift, int yShift, int xLength, int yLength) {
		g.setColor(Color.GRAY);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));

		float dash[] = { 5.0f };
		g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f));

		// Vertical lines (not needed anymore). Bad but works. To be rewritten
		/*
		 * for (int i = 0; i <= 5; i++) { int x = (800 / 5) * i; if (i == 5) x--; g.drawLine(x + xShift + 12, yShift, x
		 * + xShift + 12, yShift - yLength); }
		 */
		for (int i = 0; i < GraphDrawer.PROPORTION; i++) {
			int curY = (i + 1) * plotHeight / GraphDrawer.PROPORTION;
			g.draw(new Line2D.Double(xShift, yShift - curY, xShift + xLength, yShift - curY));
		}

		g.setStroke(new BasicStroke());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

	}

	/** Draws a point on a linear plot */
	public static void linPaintCurPoint(Graphics2D g, int xShift, int yShift, int plotHeight, int xPos, BigInteger value, BigDecimal maxValue) {
		g.setColor(COLOR_DEFAULT);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.80f));

		BigInteger realHeight = value.multiply(new BigInteger(Integer.toString(plotHeight)));
		realHeight = realHeight.divide(maxValue.add(BigDecimal.ONE).toBigInteger());

		g.draw(new Line2D.Double(xShift + xPos, yShift - realHeight.intValue(), xShift + xPos, yShift));
	}

	/** Draws a point on a log plot */
	public static void logPaintCurPoint(Graphics2D g, int xShift, int yShift, int plotHeight, int xPos, double base, BigInteger value) {
		g.setColor(COLOR_DEFAULT);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.80f));

		double a = Math.log10(value.doubleValue()) / Math.log10(base);
		a = plotHeight * (a / GraphDrawer.PROPORTION);

		if (!value.equals(BigInteger.ZERO))
			g.draw(new Line2D.Double(xShift + xPos, yShift - a, xShift + xPos, yShift));
	}

	/** Draws a number near the Y line according to PROPORTION */
	public static void PaintNumbers(Graphics2D g, boolean linear, BigDecimal maxValue, double base, int xShift, int yShift, int plotHeight) {
		g.setColor(Color.BLUE);
		Font font = new Font("Arial", Font.TRUETYPE_FONT, 11);
		g.setFont(font);

		String drawString = "";
		BigDecimal prop = new BigDecimal(Integer.toString(GraphDrawer.PROPORTION));
		for (int i = 0; i <= GraphDrawer.PROPORTION; i++) {
			if (linear) {
				BigDecimal curD = maxValue.divide(prop, 10, BigDecimal.ROUND_CEILING).multiply(new BigDecimal(i + ""));
				drawString = curD.toString();
				if (!drawString.matches("0") && !drawString.matches("0E-10")) {
					double curA = Double.parseDouble("0" + drawString.substring(drawString.indexOf("."), drawString.indexOf(".") + 8));
					drawString = curD.toBigInteger().add(new BigInteger("" + Math.round(curA))).toString();
				}
				else
					drawString = "0";
			} else
				drawString = i == 0 ? "0" : Math.round(Math.pow(base, i)) + "";
			int curY = i * plotHeight / GraphDrawer.PROPORTION;
			drawString = NumberInstance.format(drawString);
			double width = g.getFontMetrics().getStringBounds(drawString, g).getWidth();
			g.drawString(drawString, xShift - (int) width - 2, (int) (yShift - curY) + 5);
		}
	}
}
