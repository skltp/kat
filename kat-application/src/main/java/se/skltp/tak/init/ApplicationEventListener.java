package se.skltp.tak.init;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import se.skltp.tak.services.TakCacheService;

@Component
@AllArgsConstructor
@Slf4j
public class ApplicationEventListener {

  @NonNull
  private final TakCacheService takCacheService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.info("Context refreshed");
    takCacheService.refresh();
  }
}
