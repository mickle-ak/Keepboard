package kb;

import kb.gui.Key;
import kb.utils.ExecutorServices;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class RobotSupport {
	private static RobotSupport INSTANCE;
	
	private final boolean robotSupported;
	private final CountDownLatch latch = new CountDownLatch(1);
	private Robot robot;
	
	private RobotSupport() {
		boolean robotSupportedLocal;
		try {
			robot = new Robot();
			robot.setAutoWaitForIdle(true);
			robotSupportedLocal = true;
		} catch (AWTException e) {
			// Robot is not supported on this platform
			robotSupportedLocal = false;
		}
		robotSupported = robotSupportedLocal;
	}
	
	public static boolean isRobotSupported() {
		try {
			return ExecutorServices.ROBOT_EXECUTOR_SERVICE.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return getInstance().robotSupported;
				}
			}).get(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			return false;
		}
	}

	private static RobotSupport getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RobotSupport();
		}
		return INSTANCE;
	}

	public static void typeKeyAndWait(final Key key) {
		try {
			submitTypeKey(key).get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			// Ignored
		}
	}

	public static Future<?> submitTypeKey(final Key key) {
		return ExecutorServices.ROBOT_EXECUTOR_SERVICE.submit(new Runnable() {
			@Override
			public void run() {
				getInstance().doTypeKey(key);
			}
		});
	}

	private void doTypeKey(Key key) {
		int modifiers = key.getModifiers();

		boolean control = (modifiers & InputEvent.CTRL_MASK) != 0;
		boolean shift = (modifiers & InputEvent.SHIFT_MASK) != 0;
		boolean alt = (modifiers & InputEvent.ALT_MASK) != 0;
		boolean altGraph = (modifiers & InputEvent.ALT_GRAPH_MASK) != 0;
		boolean meta = (modifiers & InputEvent.META_MASK) != 0;

		pressModifierKeys(control, shift, alt, altGraph, meta);
		robot.keyPress(key.getKeyCode());
		robot.keyRelease(key.getKeyCode());
		releaseModifierKeys(control, shift, alt, altGraph, meta);
	}

	private void pressModifierKeys(boolean control, boolean shift, boolean alt, boolean altGraph, boolean meta) {
		if (control) {
			robot.keyPress(KeyEvent.VK_CONTROL);
		}
		if (shift) {
			robot.keyPress(KeyEvent.VK_SHIFT);
		}
		if (alt) {
			robot.keyPress(KeyEvent.VK_ALT);
		}
		if (altGraph) {
			robot.keyPress(KeyEvent.VK_ALT_GRAPH);
		}
		if (meta) {
			robot.keyPress(KeyEvent.VK_META);
		}
	}

	private void releaseModifierKeys(boolean control, boolean shift, boolean alt, boolean altGraph, boolean meta) {
		if (meta) {
			robot.keyRelease(KeyEvent.VK_META);
		}
		if (altGraph) {
			robot.keyRelease(KeyEvent.VK_ALT_GRAPH);
		}
		if (alt) {
			robot.keyRelease(KeyEvent.VK_ALT);
		}
		if (shift) {
			robot.keyRelease(KeyEvent.VK_SHIFT);
		}
		if (control) {
			robot.keyRelease(KeyEvent.VK_CONTROL);
		}
	}
}
