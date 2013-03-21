import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;

import javax.imageio.ImageIO;

import movie.util.NumberInstance;

public class GraphDrawer {

	public String FILE2READ;
	public String TYPE;
	public float VALUE;
	public HSBColor[] palette;

	public static final int PROPORTION = 9; // Proportion of plot starting with zero

	public static void main(String[] args) throws IOException {
		Locale.setDefault(Locale.forLanguageTag("et_EE"));
		File[] folderlist = new File("data/").listFiles();

		System.out.println("MAKING PLOTS FOR EACH FILM...");

		for (File curFolder : folderlist) {
			File[] filelist = curFolder.listFiles();
			for (File curFile : filelist) {
				GraphDrawer drawer = new GraphDrawer();
				System.out.println(curFile.toString());
				drawer.readData(curFile);
				drawer.linImageBasis("plot/lin/" + curFile.getName() + "_lin");
				drawer.logImageBasis("plot/log/" + curFile.getName() + "_log");
			}
		}

		System.out.println("MAKING PLOTS FOR ALL FILMS IN ONE...");

		GraphDrawer drawer = new GraphDrawer();
		for (File curFolder : folderlist) {
			File[] filelist = curFolder.listFiles();
			for (File curFile : filelist) {
				System.out.println(curFile.toString());
				drawer.readData(curFile);
			}
		}
		drawer.linImageBasis("plot/total_lin");
		drawer.logImageBasis("plot/total_log");

		System.out.println("MAKING PLOTS FOR EACH GENRE...");

		for (File curFolder : folderlist) {
			GraphDrawer genredrawer = new GraphDrawer();
			File[] filelist = curFolder.listFiles();
			for (File curFile : filelist) {
				System.out.println(curFile.toString());
				genredrawer.readData(curFile);
			}
			genredrawer.linImageBasis("plot/total_lin_" + curFolder.getName());
			genredrawer.logImageBasis("plot/total_log_" + curFolder.getName());
		}
	}

	public void readData(File filename) throws IOException {
		Scanner sc = new Scanner(filename);
		int paletteSize = sc.nextInt();

		// Checking if we have already created a palette
		if (palette == null)
			palette = GraphHelper.generateHSBColors(paletteSize, sc.nextFloat(), sc.nextFloat());
		else { // If so then lets skip some data from input
			sc.nextFloat();
			sc.nextFloat();
		}

		for (int i = 0; i < paletteSize; i++)
			palette[i].count = palette[i].count.add(sc.nextBigInteger());
		sc.close();
	}

	/** Building a semi-logarithmic plot */
	public void logImageBasis(String outFilename) throws IOException {
		BufferedImage logImg = new BufferedImage(palette.length + 175, 700, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = logImg.createGraphics();
		GraphHelper.renderingHints(g2d);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, palette.length + 175, 700);

		GraphHelper.drawPalette(g2d, palette, 112, 555, 20);
		GraphHelper.paintAxis(g2d, 100, 550, palette.length + 25, 500);
		GraphHelper.axisDelimeter(g2d, 475, 100, 550, palette.length + 25, 500);

		BigDecimal maxValue = new BigDecimal(GraphHelper.maxValue(palette));
		double base = Math.pow(maxValue.doubleValue(), 1d / PROPORTION);

		GraphHelper.PaintNumbers(g2d, false, maxValue, base, 100, 550, 475);

		for (int i = 0; i < palette.length; i++)
			GraphHelper.logPaintCurPoint(g2d, 112, 550, 475, i, base, palette[i].count);

		Font font = new Font("Verdana", Font.TRUETYPE_FONT, 14);
		g2d.setFont(font);

		//g2d.drawString("Дисперсия: " + NumberInstance.format(StatHelper.dispersion(palette).toString()), 250, 630);
		//g2d.drawString("Стандартное отклонение: " + NumberInstance.format(StatHelper.deviation(palette).toString()), 250, 645);

		g2d.dispose();
		ImageIO.write(logImg, "png", new File(outFilename + ".png"));
	}

	/** Building a linear plot */
	public void linImageBasis(String outFilename) throws IOException {
		BufferedImage gImg = new BufferedImage(palette.length + 175, 700, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = gImg.createGraphics();
		GraphHelper.renderingHints(g2d);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, palette.length + 175, 700);

		GraphHelper.drawPalette(g2d, palette, 112, 555, 20);
		GraphHelper.paintAxis(g2d, 100, 550, palette.length + 25, 500);
		GraphHelper.axisDelimeter(g2d, 475, 100, 550, palette.length + 25, 500);

		BigDecimal maxValue = new BigDecimal(GraphHelper.maxValue(palette));

		GraphHelper.PaintNumbers(g2d, true, maxValue, 0, 100, 550, 475);

		for (int i = 0; i < palette.length; i++)
			GraphHelper.linPaintCurPoint(g2d, 112, 550, 475, i, palette[i].count, maxValue);

		Font font = new Font("Verdana", Font.TRUETYPE_FONT, 14);
		g2d.setFont(font);

		//g2d.drawString("Дисперсия: " + NumberInstance.format(StatHelper.dispersion(palette).toString()), 250, 630);
		//g2d.drawString("Стандартное отклонение: " + NumberInstance.format(StatHelper.deviation(palette).toString()), 250, 645);

		g2d.dispose();
		ImageIO.write(gImg, "png", new File(outFilename + ".png"));
	}
}
