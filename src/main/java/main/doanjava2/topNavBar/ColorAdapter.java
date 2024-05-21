package main.doanjava2.topNavBar;


import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.scene.paint.Color;

public class ColorAdapter extends XmlAdapter<String, Color> {

	@Override
	public String marshal(Color color) throws Exception {
		if (color == null) {
			return null;
		}

		return String.format("(%d,%d,%d)", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
				(int) (color.getBlue() * 255));
	}

	@Override
	public Color unmarshal(String colorString) throws Exception {
		if (colorString == null) {
			return null;
		}

		String[] rgb = colorString.replaceAll("[()\\s]", "").split(",");
		double red = Double.parseDouble(rgb[0]) / 255;
		double green = Double.parseDouble(rgb[1]) / 255;
		double blue = Double.parseDouble(rgb[2]) / 255;
		return Color.color(red, green, blue);
	}
}