package se.skltp.tak.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.skltp.tak.services.TakCacheService;
import se.skltp.takcache.TakCacheLog;
import se.skltp.takcache.TakCacheLog.RefreshStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    when(takCacheService.refresh())
        .thenReturn(takCacheLogResultData());

    String result = resetCacheController.resetCache();

    verify(takCacheService, times(1)).refresh();
    assertEquals("<br>Test init ok<br>Good luck", result);
  }

  @Test
  void htmlInLogRowShouldBeSanitized() {
    TakCacheLog takCacheLog = new TakCacheLog();
    takCacheLog.addLog("<script>alert(1)</script>");
    when(takCacheService.refresh()).thenReturn(takCacheLog);

    String result = resetCacheController.resetCache();

    assertEquals("<br>", result);
  }

  @Test
  void nullAndEmptyLogRowsShouldBeHandled() {
    TakCacheLog takCacheLog = new TakCacheLog();
    takCacheLog.addLog(null);
    takCacheLog.addLog("");
    when(takCacheService.refresh()).thenReturn(takCacheLog);

    String result = resetCacheController.resetCache();

    assertEquals("<br><br>", result);
  }

  @Test
  void emptyLogShouldGiveEmptyResult() {
    when(takCacheService.refresh()).thenReturn(new TakCacheLog());

    String result = resetCacheController.resetCache();

    assertEquals("", result);
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