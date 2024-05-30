package kb.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Utils {
	
	private Utils() {
		throw new UnsupportedOperationException();
	}
	
	public static void sleepIgnoreInterrupt(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {
			// Ignored
		}
	}
	
	public static Clipboard getClipboard() {
		return Toolkit.getDefaultToolkit().getSystemClipboard();
	}
	
	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ignored) {
				// Ignored
			}
		}
	}
	
	public static boolean areAllElementsCorrectlyTyped(List<?> list, Class<?> clazz) {
		for (Object obj : list) {
			if (!clazz.isAssignableFrom(obj.getClass())) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean areEqualsNullsIncluded(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		
		return o1 != null && o1.equals(o2);
	}
	
	public static String[] getLines(String text) {
		String[] result = text.split("\n");
		int trailingEmptyLinesCount = getTrailingEmptyLinesCount(text);
		return trailingEmptyLinesCount > 0 ? appendEmptyLines(result, trailingEmptyLinesCount) : result;
	}

	private static int getTrailingEmptyLinesCount(String text) {
		int result = 0;
		for (int i = text.length() - 1; i >= 0; i--) {
			if (text.charAt(i) == '\n') {
				result++;
			} else {
				return result;
			}
		}
		return result;
	}
	
	private static String[] appendEmptyLines(String[] lines, int trailingEmptyLinesCount) {
		String[] result = Arrays.copyOf(lines, lines.length + trailingEmptyLinesCount);
		for (int i = result.length - trailingEmptyLinesCount; i < result.length; i++) {
			result[i] = "";
		}
		return result;
	}
	
	public static Image getImage(String path) {
		return Toolkit.getDefaultToolkit().createImage(Utils.class.getResource("/" + path));
	}
	
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		
        return createBufferedImage(image);
    }

	private static BufferedImage createBufferedImage(Image image) {
		int w = image.getWidth(null);
        int h = image.getHeight(null);
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage result = new BufferedImage(w, h, type);
        Graphics2D g2 = result.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return result;
	}
	
	public static byte[] getByteArray(Image image) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ImageIO.write(Utils.toBufferedImage(image), "png", output);
			return output.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}
	
	public static Image scale(Image image, final int maxWidth, final int maxHeight) {
		if (image.getWidth(null) <= maxWidth && image.getHeight(null) <= maxHeight) {
			return image;
		}
		
		double widthRatio = ((double) maxWidth) / image.getWidth(null);
		double heightRatio = ((double) maxHeight) / image.getHeight(null);
		
		if (widthRatio < heightRatio) {
			return image.getScaledInstance(maxWidth, -1, Image.SCALE_SMOOTH);
		} else {
			return image.getScaledInstance(-1, maxHeight, Image.SCALE_SMOOTH);
		}
	}
	
	public static <T> List<T> toList(T... elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}

	public static boolean isEnvVariableSet(String envVariable) {
		String envVariableValue = System.getenv(envVariable);
		return envVariableValue != null && !envVariableValue.isEmpty();
	}

	public static boolean isDesktopActionSupported(Desktop.Action desktopAction) {
		return Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(desktopAction);
	}
}
