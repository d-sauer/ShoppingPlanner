package troskovnik.gui;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TUtil {

	public static String formatCijena(double input, String valuta) {
		String c = parseCijenaFromDouble(input);
		c = c.substring(0, c.indexOf(".") + 3);
		c = c.replace(".", "");
		return formatCijena(c, valuta);
	}

	public static String formatCijena(String input, String valuta) {
		if (input.length() == 0)
			input = "0.00";
		else if (input.length() == 1)
			input = "0.0" + input;
		else if (input.length() == 2)
			input = "0." + input;
		else
			input = input.substring(0, input.length() - 2) + "." + input.substring(input.length() - 2);

		// ukloni vodecu nulu (023.87)
		if (input.length() > 4 && input.startsWith("0")) {
			input = input.substring(1);
		}

		return input + " " + valuta;
	}

	public static String parseCijenaFromDouble(Double d) {
		String output = d.toString();
		if (output.indexOf(".") == output.length() - 2)
			output = output + "0";
		return output;
	}

	public static String addZeroBefore(int input, int max) {
		return addZeroBefore("" + input, max);
	}

	public static String addZeroBefore(String input, int max) {
		int n = input.length();
		String b = "";
		if (n < max) {
			n = max - n;
			for (int k = 0; k < n; k++)
				b += "0";
		}
		input = b + input;
		return input;
	}

	/**
	 * 
	 * @param input
	 *          - dd.mm.yyyy
	 * @return
	 */
	public static Date parseDate(String datum) {
		Date dt = new Date();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			dt = sdf.parse(datum);			
		} catch (Exception e) {
			dt = null;
		}

		return dt;

	}

}
