package se.skltp.tak.config;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.xml.ws.Endpoint;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.services.TakCacheService;
import se.skltp.takcache.TakCacheLog;

@SpringBootTest(properties = {
    "management.endpoints.web.exposure.include=health,metrics,prometheus",
    "spring.security.user.name=actuator",
    "spring.security.user.password=test-password",
    "spring.security.user.roles=ACTUATOR"
})
@AutoConfigureMockMvc
@AutoConfigureObservability
class SecurityConfigTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  TakCacheService takCacheService;

  @MockitoBean(name = "getSokVagvalsInfoEndpoint")
  Endpoint takMockEndpoint;

  @BeforeEach
  void setUp() {
    TakCacheLog log = new TakCacheLog();
    log.addLog("ok");
    when(takCacheService.refresh()).thenReturn(log);
    when(takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(Mockito.any(), Mockito.any()))
        .thenReturn(Set.of("urn:test:contract:1"));
    when(takCacheService.getLogicalAddressesByServiceContractAndConsumer(Mockito.any(), Mockito.any()))
        .thenReturn(java.util.List.of());
  }

  @Test
  void wsdlShouldBeReachableWithoutCredentials() throws Exception {
    mockMvc.perform(get("/kat/ws/GetSupportedServiceContracts/v2").param("wsdl", ""))
        .andExpect(status().is(not(anyOf(is(401), is(403)))));
  }

  @Test
  void soapPostShouldNotBeBlockedBySecurity() throws Exception {
    mockMvc.perform(post("/kat/ws/GetSupportedServiceContracts/v2")
            .contentType(MediaType.TEXT_XML)
            .content("<invalid/>"))
        .andExpect(status().is(not(anyOf(is(401), is(403)))));
  }

  @Test
  void resetCacheShouldRemainOpen() throws Exception {
    mockMvc.perform(get("/kat/resetcache"))
        .andExpect(status().isOk())
        .andExpect(content().string("<br>ok"));
  }

  @Test
  void healthShouldBeReachableWithoutCredentials() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().is(not(anyOf(is(401), is(403)))));
  }

  @Test
  void prometheusShouldBeReachableWithoutCredentials() throws Exception {
    mockMvc.perform(get("/actuator/prometheus"))
        .andExpect(status().isOk());
  }

  @Test
  void unknownPathShouldBeDeniedByDefault() throws Exception {
    mockMvc.perform(get("/not-defined-path"))
        .andExpect(status().is(anyOf(is(401), is(403))));
  }
}
