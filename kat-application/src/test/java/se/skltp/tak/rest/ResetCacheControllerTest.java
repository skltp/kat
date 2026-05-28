package se.skltp.tak.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.skltp.tak.services.TakCacheService;
import se.skltp.takcache.TakCacheLog;
import se.skltp.takcache.TakCacheLog.RefreshStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ResetCacheControllerTest {

  @Mock
  TakCacheService takCacheService;

  ResetCacheController resetCacheController;

  @BeforeEach
  void init() {
    resetCacheController = new ResetCacheController(takCacheService);
 }

  @Test
  void takCacheServiceShouldBeCalled() {
    Mockito.when(takCacheService.refresh())
        .thenReturn(takCacheLogResultData());

    String result = resetCacheController.resetCache();

    Mockito.verify(takCacheService, Mockito.times(1)).refresh();
    assertEquals("<br>Test init ok<br>Good luck", result);
  }

  private TakCacheLog takCacheLogResultData() {
    TakCacheLog takCacheLog = new TakCacheLog();
    takCacheLog.setRefreshStatus(RefreshStatus.REFRESH_OK);
    takCacheLog.setNumberVagval(5);
    takCacheLog.setNumberBehorigheter(10);
    takCacheLog.addLog("Test init ok");
    takCacheLog.addLog("Good luck");
    return takCacheLog;
  }
}