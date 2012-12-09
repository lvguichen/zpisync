package zpisync.desktop;

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
}