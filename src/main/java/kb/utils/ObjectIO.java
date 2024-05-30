package kb.utils;

import kb.ItemGroupHolder;
import kb.gui.prefs.platf.Snap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ObjectIO {

	private static final String baseFolder = Snap.isAppRunningInSnapEnvironment()
			? Snap.getSnapUserCommonDirectory() + "/"
			: "";

	public void writeText(final String text, final String filePath) {
		ExecutorServices.FILE_OPERATIONS_EXECUTOR_SERVICE.submit(new Runnable() {
			@Override
			public void run() {
				createDirIfNotExists(filePath);
				doWriteText(text, filePath);
			}
		});
	}
	
	private static void createDirIfNotExists(String filePath) {
		File dir = new File(baseFolder + filePath).getParentFile();
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	private void doWriteText(String text, String filePath) {
		BufferedWriter out = null;
		try {
		    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(baseFolder + filePath), "UTF-8"));
		    write(text, out);
		} catch (IOException e) {
			// Ignored
		} finally {
			Utils.close(out);
		}
	}

	private void write(String text, BufferedWriter out) throws IOException {
		boolean first = true;
		for (String line : Utils.getLines(text)) {
			if (first) {
				first = false;
			} else {
				out.newLine();
			}
			out.write(line);
		}
	}

	public String readText(final String filePath) {
		try {
			return submitTextFileRead(filePath).get();
		} catch (Exception e) {
			return null;
		}
	}
	
	private Future<String> submitTextFileRead(final String filePath) {
		return ExecutorServices.FILE_OPERATIONS_EXECUTOR_SERVICE.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return doReadText(filePath);
			}
		});
	}
	
	private String doReadText(String filePath) {
		BufferedReader in = null;
		try {
			StringBuilder resultBuilder = new StringBuilder();
		    in = new BufferedReader(new InputStreamReader(new FileInputStream(baseFolder + filePath), "UTF-8"));
		    read(in, resultBuilder);
		    return resultBuilder.toString();
		} catch (IOException e) {
			return null;
		} finally {
			Utils.close(in);
		}
	}

	private void read(BufferedReader in, StringBuilder resultBuilder) throws IOException {
		String str;
		boolean first = true;
		while ((str = in.readLine()) != null) {
			if (first) {
				first = false;
			} else {
				resultBuilder.append("\n");
			}
		    resultBuilder.append(str);
		}
	}
	
	public void delete(final String fileName) {
		ExecutorServices.FILE_OPERATIONS_EXECUTOR_SERVICE.submit(new Runnable() {
			@Override
			public void run() {
				new File(baseFolder + fileName).delete();
			}
		});
	}
	
	public void saveImage(final Image image, final String filePath) {
		ExecutorServices.FILE_OPERATIONS_EXECUTOR_SERVICE.submit(new Runnable() {
			@Override
			public void run() {
				createDirIfNotExists(filePath);
				doSaveImage(image, filePath);
			}
		});
	}
	
	private void doSaveImage(Image image, String filePath) {
		try {
			ImageIO.write(Utils.toBufferedImage(image), "png", new File(baseFolder + filePath));
		} catch (IOException e) {
			// Ignored
		}
	}
	
	public Image readImage(final String filePath) {
		try {
			return submitImageFileRead(filePath).get();
		} catch (Exception e) {
			return null;
		}
	}
	
	private Future<Image> submitImageFileRead(final String filePath) {
		return ExecutorServices.FILE_OPERATIONS_EXECUTOR_SERVICE.submit(new Callable<Image>() {
			@Override
			public Image call() throws Exception {
				return doReadImage(filePath);
			}
		});
	}

	private Image doReadImage(String filePath) {
		try {
			return ImageIO.read(new File(baseFolder + filePath));
		} catch (IOException e) {
			return null;
		}
	}

	public void savePreviewImage(final Image image, final String filePath) {
		ExecutorServices.FILE_OPERATIONS_EXECUTOR_SERVICE.submit(new Runnable() {
			@Override
			public void run() {
				createDirIfNotExists(filePath);
				doSavePreviewImage(image, filePath);
			}
		});
	}
	
	private void doSavePreviewImage(Image image, String filePath) {
		try {
			ImageIO.write(Utils.toBufferedImage(new ImageIcon(Utils.scale(image, 300, 80)).getImage()), "png", new File(baseFolder + filePath));
		} catch (IOException e) {
			// Ignored
		}
	}

	public static boolean takeLock() {
		try {
			String lockFileName = "history/session.lck";
			createDirIfNotExists(lockFileName);
			return new FileOutputStream(baseFolder + lockFileName).getChannel().tryLock() != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static void deleteErrorFilesIfExist() {
		String folderPath = Snap.isAppRunningInSnapEnvironment()
				? Snap.getSnapUserCommonDirectory()
				: System.getProperty("user.dir");
		for (String fileName : new File(folderPath).list()) {
			if (fileName.startsWith("hs_err_pid") && fileName.endsWith(".log")) {
				new ObjectIO().delete(fileName);
			}
		}
	}

	public static boolean isFirstTimeStart() {
		boolean historyFileExists = new File(baseFolder + ItemGroupHolder.HISTORY_FILE_NAME).exists();
		return !historyFileExists;
	}
}
