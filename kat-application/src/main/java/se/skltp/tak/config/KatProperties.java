package se.skltp.tak.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "kat")
public class KatProperties {

  /**
   * The service path where GetSupportedServiceContracts V1 should be published.
   */
  private String getsupportedservicecontractsV1Path="";

  /**
   * The service path where GetSupportedServiceContracts V2 should be published.
   */
  private String getsupportedservicecontractsV2Path="";

  /**
   * The service path where GetLogicalAddresseesByServiceContract V2 should be published.
   */
  private String getlogicaladdresseesbyservicecontractV2Path="";

}
