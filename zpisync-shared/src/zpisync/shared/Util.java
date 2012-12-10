package zpisync.shared;

public class Util {
	
	public static <T> T either(T first, T second) {
		if (first != null)
			return first;
		return second;
	}

	public static String getHomeDir() {
		return System.getProperty("user.home");
	}

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

	public static void require(boolean condition) {
		require(condition, null);
	}
	
	public static void require(boolean condition, String message) {
		if (!condition)
			throw new AssertionError(message);
	}
	
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}