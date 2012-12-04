import java.math.BigInteger;

public class HSBColor {

	public float hue;
	public float saturation;
	public float brightness;
	public BigInteger count = BigInteger.ZERO;

	public HSBColor(float hue, float saturation, float brightness) {
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
	}
}
