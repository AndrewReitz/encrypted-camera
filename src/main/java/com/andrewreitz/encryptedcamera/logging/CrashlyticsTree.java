package com.andrewreitz.encryptedcamera.logging;

import com.crashlytics.android.Crashlytics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public final class CrashlyticsTree extends Timber.HollowTree {
  private static final Pattern ANONYMOUS_CLASS = Pattern.compile("\\$\\d+$");

  private enum LogLevel {
    INFO,
    ERROR,
    WARNING
  }

  @Override public void i(String message, Object... args) {
    log(LogLevel.INFO, createTag(), formatString(message, args));
  }

  @Override public void i(Throwable t, String message, Object... args) {
    log(LogLevel.INFO, createTag(), formatString(message, args), t);
  }

  @Override public void w(String message, Object... args) {
    log(LogLevel.WARNING, createTag(), formatString(message, args));
  }

  @Override public void w(Throwable t, String message, Object... args) {
    log(LogLevel.WARNING, createTag(), formatString(message, args), t);
  }

  @Override public void e(String message, Object... args) {
    log(LogLevel.ERROR, createTag(), formatString(message, args));
  }

  @Override public void e(Throwable t, String message, Object... args) {
    log(LogLevel.ERROR, createTag(), formatString(message, args), t);
  }

  private void log(LogLevel logLevel, String tag, String message) {
    log(logLevel, tag, message, null);
  }

  private void log(LogLevel logLevel, String tag, String message, Throwable t) {
    Crashlytics.log(String.format("%s/%s: %s", logLevel, tag, message));
    Crashlytics.logException(t); // always log even if null, crashylitcs ignores them
  }

  /**
   * Create a tag from the calling class (4 up).
   * This is not exactly fast but then again this really shouldn't be called a lot
   */
  private static String createTag() {
    String tag = new Throwable().getStackTrace()[4].getClassName();
    Matcher m = ANONYMOUS_CLASS.matcher(tag);
    if (m.find()) {
      tag = m.replaceAll("");
    }
    return tag.substring(tag.lastIndexOf('.') + 1);
  }

  private static String formatString(String message, Object... args) {
    return args.length == 0 ? message : String.format(message, args);
  }
}
