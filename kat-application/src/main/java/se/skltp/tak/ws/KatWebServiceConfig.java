package se.skltp.tak.ws;

import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import jakarta.xml.ws.Endpoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import se.skltp.tak.config.KatProperties;
import se.skltp.tak.services.TakCacheService;
import se.skltp.tak.ws.getlogicaladdressesbyservicecontract.GetLogicalAddresseesByServiceContractV2Impl;
import se.skltp.tak.ws.getsupportedservicecontracts.GetSupportedServiceContractsServiceV2Impl;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(KatProperties.class)
public class KatWebServiceConfig {

  @NonNull
  private final Bus bus;

  @NonNull
  private final TakCacheService takCacheService;

  @NonNull
  private final KatProperties katProperties;

  @Value("${kat.cxf.logging.limit:8192}")
  private int cxfLoggingLimit;

  @Value("${kat.cxf.logging.pretty:false}")
  private boolean cxfPrettyLogging;

  @Bean
  public Endpoint getSupportedServiceContractsV2Endpoint() {
    return publishEndpoint(
        new GetSupportedServiceContractsServiceV2Impl(takCacheService),
        katProperties.getGetsupportedservicecontractsV2Path());
  }

  @Bean
  public Endpoint getLogicalAddresseesByServiceContractV2Endpoint() {
    return publishEndpoint(
        new GetLogicalAddresseesByServiceContractV2Impl(takCacheService),
        katProperties.getGetlogicaladdresseesbyservicecontractV2Path());
  }

  private Endpoint publishEndpoint(Object implementor, String servicePath) {
    final EndpointImpl endpoint = new EndpointImpl(this.bus, implementor);
    endpoint.getFeatures().add(loggingFeature());
    endpoint.publish(servicePath);
    return endpoint;
  }

  private LoggingFeature loggingFeature() {
    LoggingFeature loggingFeature = new LoggingFeature();
    loggingFeature.setLimit(cxfLoggingLimit);
    loggingFeature.setPrettyLogging(cxfPrettyLogging);

    return loggingFeature;
  }

}
