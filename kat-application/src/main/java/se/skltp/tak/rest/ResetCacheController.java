package se.skltp.tak.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.skltp.tak.services.TakCacheService;
import se.skltp.takcache.TakCacheLog;

@RestController
public class ResetCacheController {

  @Autowired
  TakCacheService takCacheService;

  @GetMapping("${kat.resetcache.path}")
  String resetCache(){
    TakCacheLog takCacheLog = takCacheService.refresh();
    return getResultAsString(takCacheLog);
  }

  private String getResultAsString(TakCacheLog takCacheLog) {
    StringBuilder resultAsString = new StringBuilder();
    for (String processingLog : takCacheLog.getLog()) {
      resultAsString.append("<br>").append(processingLog);
    }
    return resultAsString.toString();
  }

}
