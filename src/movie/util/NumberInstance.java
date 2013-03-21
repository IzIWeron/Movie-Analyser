package movie.util;

public class NumberInstance {

	public static String format(String num) {
		StringBuilder out = new StringBuilder(num);
		for (int i = num.length() - 3; i > 0; i -= 3)
			out.insert(i, ' ');
		return out.toString();
	}
}
