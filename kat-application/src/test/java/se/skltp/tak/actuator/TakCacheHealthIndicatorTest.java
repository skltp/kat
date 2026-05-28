package se.skltp.tak.actuator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import se.skltp.tak.services.TakCacheService;
import se.skltp.takcache.TakCacheLog;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TakCacheHealthIndicatorTest {

  @Mock
  TakCacheService takCacheServiceMock;

  @Test
  void testNotInitialized() {
    TakCacheHealthIndicator indicator = new TakCacheHealthIndicator(takCacheServiceMock);
    Health health = indicator.health();
    assertEquals(Status.DOWN, health.getStatus());
  }

  @Test
  void testInitializedOk() {
    Mockito.when(takCacheServiceMock.isInitalized()).thenReturn(true);

    TakCacheHealthIndicator indicator = new TakCacheHealthIndicator(takCacheServiceMock);
    Health health = indicator.health();
    assertEquals(Status.UP, health.getStatus());
  }

  @Test
  void testRefreshFailed() {
    TakCacheLog log = new TakCacheLog();
    log.setRefreshStatus(TakCacheLog.RefreshStatus.REFRESH_FAILED);
    Mockito.when(takCacheServiceMock.isInitalized()).thenReturn(false);
    Mockito.when(takCacheServiceMock.getLastRefreshLog()).thenReturn(log);

    TakCacheHealthIndicator indicator = new TakCacheHealthIndicator(takCacheServiceMock);
    Health health = indicator.health();
    assertEquals(Status.DOWN, health.getStatus());
  }
}
