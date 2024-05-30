package kb.gui.prefs.platf;

import kb.utils.Utils;

public class Snap {

    private static final String SNAP_USER_COMMON = "SNAP_USER_COMMON";
    private static final String SNAP_USER_DATA = "SNAP_USER_DATA";

    private Snap() {}

    public static boolean isAppRunningInSnapEnvironment() {
        return Utils.isEnvVariableSet(SNAP_USER_COMMON) && Utils.isEnvVariableSet(SNAP_USER_DATA);
    }

    public static String getSnapUserCommonDirectory() {
        return System.getenv(SNAP_USER_COMMON);
    }

    public static String getSnapUserDataDirectory() {
        return System.getenv(SNAP_USER_DATA);
    }
}
