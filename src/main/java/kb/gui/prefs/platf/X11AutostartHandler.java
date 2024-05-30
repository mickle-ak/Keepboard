package kb.gui.prefs.platf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class X11AutostartHandler extends AutostartHandler {
	private String autostartDirectory;
	
	public X11AutostartHandler() {
		try {
			autostartDirectory = new X11AutostartDirectoryFinder().find();
		} catch (Throwable t) {
			// Ignored
		}
	}
	
	@Override
	public boolean isAutostartSupported() {
		return autostartDirectory != null;
	}

	@Override
	public void setAutostart(boolean autostart) {
		if (autostart) {
			enableAutostart();
		} else {
			disableAutostart();
		}
	}

	private void enableAutostart() {
		try {
			Files.write(Paths.get(autostartDirectory + "/keepboard.desktop"), getAutostartFileContent().getBytes());
		} catch (IOException e) {
			// Ignored
		}
	}

	private String getAutostartFileContent() {
		String startCommand = getStartCommand();
		
		return "[Desktop Entry]\n"
		       + "Type=Application\n"
		       + "Exec=" + startCommand + "\n"
		       + "Hidden=false\n"
		       + "NoDisplay=false\n"
		       + "Name[en_US]=keepboard\n"
		       + "Name=" + startCommand + "\n";
	}
	
	private String getStartCommand() {
		return Snap.isAppRunningInSnapEnvironment()
				? "keepboard"
				: System.getProperty("user.dir") + "/start.sh";
	}

	private void disableAutostart() {
		try {
			Files.deleteIfExists(Paths.get(autostartDirectory + "/keepboard.desktop"));
		} catch (IOException e) {
			// Ignored
		}
	}
}
