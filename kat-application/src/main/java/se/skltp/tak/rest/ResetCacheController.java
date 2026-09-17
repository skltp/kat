package se.skltp.tak.rest;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.skltp.tak.services.TakCacheService;
import se.skltp.takcache.TakCacheLog;

@RestController
public class ResetCacheController {

  private static final PolicyFactory NO_HTML = new HtmlPolicyBuilder().toFactory();

  private final TakCacheService takCacheService;

  @Autowired
  public ResetCacheController(TakCacheService takCacheService) {
    this.takCacheService = takCacheService;
  }

  @GetMapping(value = "${kat.resetcache.path}")
  String resetCache(){
    TakCacheLog takCacheLog = takCacheService.refresh();
    return getResultAsString(takCacheLog);
  }

  private String getResultAsString(TakCacheLog takCacheLog) {
    if (takCacheLog == null || takCacheLog.getLog() == null) {
      return "";
    }

    StringBuilder resultAsString = new StringBuilder();
    for (String processingLog : takCacheLog.getLog()) {
      String sanitized = NO_HTML.sanitize(processingLog == null ? "" : processingLog);
      resultAsString.append("<br>").append(sanitized);
    }
    return resultAsString.toString();
  }

}
