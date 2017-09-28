package autodetect.android;

/**
 * This checked exception will be thrown if there is no ANDROID_HOME environment variable
 * @author Narayana
 *
 */
class ADBPathNotFound extends Exception {
	ADBPathNotFound(String message) {
		super(message);
	}
}
