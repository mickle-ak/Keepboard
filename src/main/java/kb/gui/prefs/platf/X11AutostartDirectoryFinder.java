package kb.gui.prefs.platf;

import java.io.File;

public class X11AutostartDirectoryFinder {
	
	public String find() {
		String result = null;
		if (Snap.isAppRunningInSnapEnvironment()) {
			result = Snap.getSnapUserDataDirectory() + "/.config/autostart";
			File dir = new File(result);
			if (!dir.exists()) {
				dir.mkdir();
			}
		}
		if (result == null) {
			result = findUsing_XDG_CONFIG_HOME();
		}
		if (result == null) {
			result = findUsing_XDG_CONFIG_DIRS();
		}
		if (result == null) {
			result = findAmongPreferenceOrderedDirectories("/etc/xdg");
		}
		return result;
	}
	
	private String findUsing_XDG_CONFIG_HOME() {
		String XDG_CONFIG_HOME = getEnvVar("XDG_CONFIG_HOME");
		if (XDG_CONFIG_HOME != null) {
			return findAmongPreferenceOrderedDirectories(XDG_CONFIG_HOME);
		} else {
			String HOME = getEnvVar("HOME");
			if (HOME != null) {
				return HOME + "/.config/autostart";
			}
		}
		return null;
	}
	
	private String getEnvVar(String string) {
		String result = System.getenv(string);
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result;
	}
	
	private String findAmongPreferenceOrderedDirectories(String... dirs) {
		for (String dir : dirs) {
			String path = dir + "/autostart";
			if (exists(path)) {
				return path;
			}
		}
		return null;
	}

	private boolean exists(String directory) {
		return new File(directory).exists();
	}

	private String findUsing_XDG_CONFIG_DIRS() {
		String variableValue = getEnvVar("XDG_CONFIG_DIRS");
		if (variableValue != null) {
			return findAmongPreferenceOrderedDirectories(variableValue.split(":"));
		}
		return null;
	}
}
