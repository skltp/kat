package se.skltp.tak.mock.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "tak.mock")
public class TakMockProperties {
  /**
   * Hostname and port for this server on the form &lt;protocol&gt;://&lt;host&gt;:&lt;port&gt;/&lt;cxfservletpath&gt;
   * Used for publishing web service and to get the correct wsdl information
   */
  private String localWsHost = "http://localhost:8882/takmockservice";
}
