package kb;

import kb.utils.ObjectIO;

import javax.swing.*;
import java.awt.*;

public class ImageClipboardContent implements ClipboardContent {
	
	private volatile Image image;
	private volatile String fileName;
	
	public ImageClipboardContent(Image image) {
		this.image = image;
	}
	
	public ImageClipboardContent(String fileName) {
		this.fileName = fileName;
	}

	public Image getImage() {
		if (image == null) {
			// Multiple lazy initialization is acceptable
			image = new ImageIcon(new ObjectIO().readImage(fileName)).getImage();
		}
		return image;
	}
}
