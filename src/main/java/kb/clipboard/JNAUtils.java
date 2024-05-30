package kb.clipboard;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class JNAUtils {

	public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary) Native.loadLibrary("User32", CLibrary.class);

        int GetClipboardSequenceNumber();
    }
	
	public static int getClipboardSequenceNumber() {
		return CLibrary.INSTANCE.GetClipboardSequenceNumber();
	}
}
