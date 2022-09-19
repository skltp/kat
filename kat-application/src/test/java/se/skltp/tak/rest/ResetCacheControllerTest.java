package se.skltp.tak.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.services.TakCacheService;
import se.skltp.takcache.TakCacheLog;
import se.skltp.takcache.TakCacheLog.RefreshStatus;

public class ResetCacheControllerTest {

  @Mock
  TakCacheService takCacheService;

  ResetCacheController resetCacheController;

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);
    resetCacheController = new ResetCacheController(takCacheService);
 }

  @Test
  public void takCacheServiceShouldBeCalled() {
    Mockito.when(takCacheService.refresh())
        .thenReturn(takCacheLogResultData());

    String result = resetCacheController.resetCache();

    Mockito.verify(takCacheService, Mockito.times(1)).refresh();
    Assert.assertEquals("<br>Test init ok<br>Good luck", result );
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