package co.nodeath.encryptedcamera.business.log;

import android.util.Log;

import co.nodeath.encryptedcamera.BuildConfig;

/**
 * Utility for logging
 *
 * @author bkurinsk
 */
public class Logger {

    private static final String LOG_TAG = Logger.class.getPackage().getName();

    /**
     * Suppress default constructor for noninstantiability
     */
    private Logger() {
        throw new AssertionError();
    }

    /**
     * Prints a debug level log with the given class name and message. Ex: Logger.log(this, "Here is
     * my message.");
     *
     * @param currentObject a reference to the calling object
     * @param message       the message to log
     */
    public static void log(Object currentObject, String message) {
        log(LogLevel.DEBUG, currentObject, message);
    }

    /**
     * Prints a debug level log with the given class name and message. Ex:
     * Logger.log("com.nerdery.baseproject.activity.PlayerActivity", "Here is my message.");
     *
     * @param className the name of the current class with package
     * @param message   the message to log
     */
    public static void log(String className, String message) {
        log(LogLevel.DEBUG, className, message);
    }

    /**
     * Prints a message to the log with the given level, class name, and message. Ex:
     * Logger.log(Logger.WARN, this, "This is my message.");
     *
     * @param logLevel      the log level as a {@link LogLevel} enum object
     * @param currentObject a reference to the calling object
     * @param message       the message to log
     */
    public static void log(LogLevel logLevel, Object currentObject, String message) {
        createLogMessage(logLevel, currentObject.getClass().getCanonicalName(), message, null);
    }

    /**
     * Prints a message to the log with the given level, class name, and message. Ex:
     * Logger.log(Logger.WARN, "com.nerdery.baseproject.activity.PlayerActivity", "Here is my
     * message.");
     *
     * @param logLevel  the log level as a {@link LogLevel} enum object
     * @param className the name of the current class with package
     * @param message   the message to log
     */
    public static void log(LogLevel logLevel, String className, String message) {
        createLogMessage(logLevel, className, message, null);
    }

    /**
     * Prints a message to the log with the given level, class name, and message. Ex:
     * Logger.log(Logger.WARN, this, "This is my message.", throwable);
     *
     * @param logLevel      the log level as a {@link LogLevel} enum object
     * @param currentObject a reference to the calling object
     * @param message       the message to log
     * @param throwable     a throwable to log
     */
    public static void log(LogLevel logLevel, Object currentObject, String message,
            Throwable throwable) {
        createLogMessage(logLevel, currentObject.getClass().getCanonicalName(), message, throwable);
    }

    /**
     * Prints a message to the log with the given level, class name, and message. Ex:
     * Logger.log(Logger.WARN, "com.nerdery.baseproject.activity.PlayerActivity", "Here is my
     * message.", throwable);
     *
     * @param logLevel  the log level as a {@link LogLevel} enum object
     * @param className the name of the current class with package
     * @param message   the message to log
     * @param throwable a throwable to log
     */
    public static void log(LogLevel logLevel, String className, String message,
            Throwable throwable) {
        createLogMessage(logLevel, className, message, throwable);
    }

    private static void createLogMessage(LogLevel logLevel, String className, String message,
            Throwable t) {

        if (BuildConfig.DEBUG) {

            String logMessage = className + ": " + message;

            switch (logLevel) {
                case DEBUG:
                    Log.d(LOG_TAG, logMessage, t);
                    break;
                case ERROR:
                    Log.e(LOG_TAG, logMessage, t);
                    break;
                case INFO:
                    Log.i(LOG_TAG, logMessage, t);
                    break;
                case VERBOSE:
                    Log.v(LOG_TAG, logMessage, t);
                    break;
                case WARN:
                    Log.w(LOG_TAG, logMessage, t);
                    break;
                default:
                    Log.d(LOG_TAG, logMessage, t);
                    break;
            }
        }
    }

    /**
     * A enum for the log levels, this is written as a convenience as to not require two imports.
     * One for Logger and another for Android's Log class.
     */
    public enum LogLevel {
        DEBUG, ERROR, INFO, VERBOSE, WARN
    }
}
