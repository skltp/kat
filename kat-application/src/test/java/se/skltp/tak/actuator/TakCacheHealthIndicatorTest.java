package se.skltp.tak.actuator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import se.skltp.tak.services.TakCacheService;
import se.skltp.takcache.TakCacheLog;

import static org.junit.Assert.*;

public class TakCacheHealthIndicatorTest {

  @Mock
  TakCacheService takCacheServiceMock;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testNotInitialized() {
    TakCacheHealthIndicator indicator = new TakCacheHealthIndicator(takCacheServiceMock);
    Health health = indicator.health();
    assertEquals(Status.DOWN, health.getStatus());
  }

  @Test
  public void testInitializedOk() {
    TakCacheLog log = new TakCacheLog();
    log.setRefreshStatus(TakCacheLog.RefreshStatus.REFRESH_OK);
    Mockito.when(takCacheServiceMock.isInitalized()).thenReturn(true);
    Mockito.when(takCacheServiceMock.getLastRefreshLog()).thenReturn(log);

    TakCacheHealthIndicator indicator = new TakCacheHealthIndicator(takCacheServiceMock);
    Health health = indicator.health();
    assertEquals(Status.UP, health.getStatus());
  }

  @Test
  public void testRefreshFailed() {
    TakCacheLog log = new TakCacheLog();
    log.setRefreshStatus(TakCacheLog.RefreshStatus.REFRESH_FAILED);
    Mockito.when(takCacheServiceMock.isInitalized()).thenReturn(false);
    Mockito.when(takCacheServiceMock.getLastRefreshLog()).thenReturn(log);

    TakCacheHealthIndicator indicator = new TakCacheHealthIndicator(takCacheServiceMock);
    Health health = indicator.health();
    assertEquals(Status.DOWN, health.getStatus());
  }
}
