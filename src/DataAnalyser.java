import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class DataAnalyser {

	public static final float[] COLORS_BRIGHTNESS = { 0.250f, 0.500f, 0.750f, 0.500f };
	public static final String[] OUTPUT_NAME = { "low", "mid", "high", "average" };

	public static final int INDEX_DARK = 0;
	public static final int INDEX_MEDIUM = 1;
	public static final int INDEX_BRIGHT = 2;
	public static final int INDEX_AVERAGE = 3;

	/** Number of colors in palette */
	public int colors;

	public HSBColor[][] hsbColors;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		System.out.println(new Date().toGMTString());
		long startTime = System.currentTimeMillis();

		// DataAnalyser analyser = new DataAnalyser(Integer.parseInt(args[0])); // Palette size
		// analyser.analyseData(args[2], Integer.parseInt(args[1])); // Destination folder, skip frames
		// analyser.writeData(args[3]); // Output Filename

		System.out.println(new Date().toGMTString());
		System.out.println("Time elapsed: " + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
	}

	public DataAnalyser(int colors) throws IOException {
		this.colors = colors;
		this.hsbColors = new HSBColor[4][];
		for (int i = 0; i < 4; i++)
			this.hsbColors[i] = GraphHelper.generateHSBColors(colors, 1.0f, COLORS_BRIGHTNESS[i]);
	}

	public void analyseData(String path, int skipf) throws IOException {
		int amount = new File(path).listFiles().length;
		for (int i = 1; i <= amount; i += skipf) {
			DecimalFormat format = new DecimalFormat("0000000");
			BufferedImage img = ImageIO.read(new File(path + "image-" + format.format(i) + ".png"));
			// if ((i - 1) % (skipf * 10) == 0)
			// System.out.println("image-" + format.format(i) + ".png");
			if ((i - 1) % 50 == 0)
				System.out.print(".");

			for (int x = 0; x < img.getWidth(); x++)
				for (int y = 0; y < img.getHeight(); y++) {
					Color c = new Color(img.getRGB(x, y));
					float[] d = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null); // HSB colors
					int curIndex = Math.round(d[0] * colors);
					if (curIndex == colors)
						curIndex--;
					if (d[2] <= COLORS_BRIGHTNESS[INDEX_DARK])
						hsbColors[INDEX_DARK][curIndex].count = hsbColors[INDEX_DARK][curIndex].count.add(BigInteger.ONE);
					else if (d[2] > COLORS_BRIGHTNESS[INDEX_DARK] && d[1] > COLORS_BRIGHTNESS[INDEX_DARK])
						hsbColors[INDEX_MEDIUM][curIndex].count = hsbColors[INDEX_MEDIUM][curIndex].count.add(BigInteger.ONE);
					else if (d[2] >= COLORS_BRIGHTNESS[INDEX_BRIGHT])
						hsbColors[INDEX_BRIGHT][curIndex].count = hsbColors[INDEX_BRIGHT][curIndex].count.add(BigInteger.ONE);
					hsbColors[INDEX_AVERAGE][curIndex].count = hsbColors[INDEX_AVERAGE][curIndex].count.add(BigInteger.ONE);
				}
		}
		System.out.println();
	}

	public void writeData(String outFilename) throws IOException {
		for (int i = 0; i < 4; i++) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename + "_" + OUTPUT_NAME[i]));

			writer.write(colors + " " + hsbColors[i][0].saturation + " " + hsbColors[i][0].brightness);
			writer.newLine();

			for (int j = 0; j < colors; j++) {
				writer.write(hsbColors[i][j].count.toString());
				writer.newLine();
			}

			writer.close();
		}

	}

}
