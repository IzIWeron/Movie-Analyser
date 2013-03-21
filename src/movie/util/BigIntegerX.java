package movie.util;
import java.math.BigInteger;

/**Additional implementation of functions that does not included to the default BigInteger class */
public class BigIntegerX {
	/** Returns the correctly rounded positive square root of a BigInteger value.
	 *Newton's method (n/g + g)/2 is used. */
	public static BigInteger sqrt(BigInteger n) {
		BigInteger g = new BigInteger((n.shiftRight((n.bitLength() + 1) / 2)).toString());
		BigInteger LastG = null;
		BigInteger One = new BigInteger("1");
		while (true) {
			LastG = g;
			g = n.divide(g).add(g).shiftRight(1);
			int i = g.compareTo(LastG);
			if (i == 0)
				return g;
			if (i < 0) {
				if (LastG.subtract(g).compareTo(One) == 0)
					if (g.multiply(g).compareTo(n) < 0 && LastG.multiply(LastG).compareTo(n) > 0)
						return g;
			} else {
				if (g.subtract(LastG).compareTo(One) == 0)
					if (LastG.multiply(LastG).compareTo(n) < 0 && g.multiply(g).compareTo(n) > 0)
						return LastG;
			}
		}
	}
}
