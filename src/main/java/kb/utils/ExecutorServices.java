package kb.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ExecutorServices {
	public static final ExecutorService FILE_OPERATIONS_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
	public static final ExecutorService CLIPBOARD_CHANGE_LISTENER_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
	public static final ExecutorService CLIPBOARD_ACCESSOR_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
	public static final ExecutorService EXTERN_EVENT_LISTENTER = Executors.newSingleThreadExecutor();
	public static final ExecutorService ROBOT_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

	private ExecutorServices() {
		// private constructor
	}
	
	public static void shutdownEcexutorServices() {
		initiateShutdown();
		awaitTermination();
	}

	private static void initiateShutdown() {
		FILE_OPERATIONS_EXECUTOR_SERVICE.shutdown();
	}
	
	private static void awaitTermination() {
		awaitTermination(FILE_OPERATIONS_EXECUTOR_SERVICE);
	}
	
	private static void awaitTermination(ExecutorService executorService) {
		try {
			executorService.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException ignored) {
			// Ignored
		}
	}
}
