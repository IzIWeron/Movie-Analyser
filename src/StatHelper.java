import java.math.BigInteger;

import movie.util.BigIntegerX;

public class StatHelper {
	
	static BigInteger meanSquares(HSBColor[] palette) {
		BigInteger meansq = BigInteger.ZERO;
		for (HSBColor bi : palette)
			meansq = meansq.add(bi.count.pow(2));
		
		return meansq.divide(new BigInteger(Integer.toString(palette.length)));
	}
	
	static BigInteger squareMean(HSBColor[] palette) {
		BigInteger sqmean = BigInteger.ZERO;
		for (HSBColor bi : palette)
			sqmean = sqmean.add(bi.count);

		return sqmean.divide(new BigInteger(Integer.toString(palette.length))).pow(2);
	}

	static BigInteger dispersion(HSBColor[] palette) {		
		return meanSquares(palette).subtract(squareMean(palette));
	}

	static BigInteger deviation(HSBColor[] palette) {		
		return BigIntegerX.sqrt(meanSquares(palette).subtract(squareMean(palette)));
	}

}
