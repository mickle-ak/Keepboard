package kb.clipboard;

import kb.utils.Utils;

import java.awt.*;
import java.util.Arrays;

public class ImageEqualityChecker {
	
	public boolean areEquals(Image image1, Image image2) {
		return Arrays.equals(Utils.getByteArray(image1), Utils.getByteArray(image2));
	}
}
