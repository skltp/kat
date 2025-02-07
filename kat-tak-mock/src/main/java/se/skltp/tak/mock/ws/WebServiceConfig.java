package se.skltp.tak.mock.ws;

import jakarta.xml.ws.Endpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import se.skltp.tak.mock.config.TakMockProperties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(TakMockProperties.class)
public class WebServiceConfig {

  @NonNull
  private final TakMockProperties takMockProperties;

  @Bean
  public Endpoint getSokVagvalsInfoEndpoint() {
    SokVagvalsInfoMockWebService sokVagvalsInfoMockWebService = new SokVagvalsInfoMockWebService(takMockProperties.getLocalWsHost());
    return sokVagvalsInfoMockWebService.start();
  }
}
