import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class GraphDrawer {

	public String FILE2READ;
	public String TYPE;
	public float VALUE;
	public HSBColor[] palette;

	public static final int PROPORTION = 9; // Proportion of plot starting with zero

	public static void main(String[] args) throws IOException {
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

		BigInteger maxValue = GraphHelper.maxValue(palette);
		double base = Math.pow(maxValue.doubleValue(), 1d / PROPORTION);

		GraphHelper.logPaintNumbers(g2d, base, PROPORTION, 95, 553, 475);

		for (int i = 0; i < palette.length; i++)
			GraphHelper.logPaintCurPoint(g2d, 112, 550, 475, i, base, palette[i].count, PROPORTION);

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

		BigInteger maxValue = GraphHelper.maxValue(palette);
		
		//TODO Numbers on Y Axis

		for (int i = 0; i < palette.length; i++)
			GraphHelper.paintCurPoint(g2d, 112, 550, 475, i, palette[i].count, maxValue);

		g2d.dispose();
		ImageIO.write(gImg, "png", new File(outFilename + ".png"));
	}
}
