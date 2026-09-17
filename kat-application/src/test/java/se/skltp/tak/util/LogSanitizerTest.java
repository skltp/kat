package se.skltp.tak.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LogSanitizerTest {

  @SuppressWarnings("ConstantValue") // null check
  @Test
  void nullValueShouldRemainNull() {
    assertNull(LogSanitizer.sanitize(null));
  }

  @Test
  void controlCharactersShouldBeReplaced() {
    assertEquals("abc__def", LogSanitizer.sanitize("abc\r\ndef"));
  }
}
