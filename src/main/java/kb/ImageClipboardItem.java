package kb;

import kb.utils.ObjectIO;
import kb.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageClipboardItem extends ClipboardItem {
	
	private final String previewFileName;
	private final String hash;
	
	public ImageClipboardItem(String displayText, String fileName, String name, String previewFileName, String hash) {
		super(displayText, fileName, name);
		this.previewFileName = previewFileName;
		this.hash = hash;
	}

	public ImageClipboardItem(ImageIcon imageIcon) {
		super(createDisplayName(imageIcon), createFile(imageIcon), null);
		this.hash = calculateHash();
		this.previewFileName = createPreviewFile(new ImageIcon(new ObjectIO().readImage(getFileName())));
	}
	
	private String calculateHash() {
		ImageIcon imageIcon = getImageIcon();
		
		if (imageIcon == null) {
			throw new RuntimeException();
		}
		
		byte[] imageBytes = Utils.getByteArray(imageIcon.getImage());
		return getHexHash(getMD5Bytes(imageBytes));
	}

	private String getHexHash(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte aByte : bytes) {
			String hex = Integer.toHexString(aByte);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			result.append(hex);
		}
		return result.toString();
	}

	private byte[] getMD5Bytes(byte[] bytes) {
		try {
			return MessageDigest.getInstance("MD5").digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException();
		}
	}

	private static String createDisplayName(ImageIcon imageIcon) {
		return "<image " + imageIcon.getImage().getWidth(null) + " x " + imageIcon.getImage().getHeight(null) + ">";
	}

	private static String createFile(ImageIcon imageIcon) {
		String fileName = getNewFileName();
		new ObjectIO().saveImage(imageIcon.getImage(), fileName);
		return fileName;
	}
	
	private String createPreviewFile(ImageIcon imageIcon) {
		String fileName = FileNameGenerator.generate("history/", "imgpr");
		new ObjectIO().savePreviewImage(imageIcon.getImage(), fileName);
		return fileName;
	}
	
	@Override
	public ImageClipboardContent getContent() {
		return new ImageClipboardContent(getFileName());
	}

	@Override
	public boolean equalsByValue(ClipboardItem other, ClipboardContent otherContent) {
		if (!super.equalsByValue(other, otherContent)) {
			return false;
		}
		
		if (!(other instanceof ImageClipboardItem)) {
			return false;
		}
		
		ImageClipboardItem imageItem = (ImageClipboardItem) other;
		
		return hash.equals(imageItem.hash);
	}

	@Override
	public ImageClipboardItem createClonedInstance() {
		ImageClipboardItem result = new ImageClipboardItem(getImageIcon());
		result.setName(getName());
		return result;
	}

	private ImageIcon getImageIcon() {
		Image resultImage = new ObjectIO().readImage(getFileName());
		return resultImage != null ? new ImageIcon(resultImage) : null;
	}
	
	public String getPreviewFileName() {
		return previewFileName;
	}

	public Image getPreviewImage() {
		return new ObjectIO().readImage(previewFileName);
	}

	@Override
	public void deletePersistedContent() {
		super.deletePersistedContent();
		new ObjectIO().delete(previewFileName);
	}

	public String getHash() {
		return hash;
	}
}
