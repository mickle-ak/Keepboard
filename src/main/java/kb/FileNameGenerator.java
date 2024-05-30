package kb;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FileNameGenerator {
	private static final Set<String> usedNames = new HashSet<String>();
	
	private static final Random random = new Random();
	
	private FileNameGenerator() {
		// Private constructor
	}
	
	public static String generate(String prefix, String suffix) {
		String result;
		
		do {
			result = prefix + Math.abs(getRandomIntExcludeMinInt()) + "." + suffix;
		} while (usedNames.contains(result));
		
		usedNames.add(result);
		
		return result;
	}

	private static int getRandomIntExcludeMinInt() {
		int result;
		
		do {
			result = random.nextInt();
		} while (result == Integer.MIN_VALUE);
		
		return result;
	}
	
	public static void addToReservedNames(String name) {
		usedNames.add(name);
	}
}
