package se.skltp.tak.util;

public final class LogSanitizer {

  private LogSanitizer() {
  }

  public static String sanitize(String value) {
    if (value == null) {
      return null;
    }
    return value.replaceAll("[\\p{Cntrl}]", "_");
  }
}
