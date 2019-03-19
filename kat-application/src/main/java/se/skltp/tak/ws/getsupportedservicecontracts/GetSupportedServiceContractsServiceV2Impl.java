package se.skltp.tak.ws.getsupportedservicecontracts;

import java.util.Set;
import javax.jws.WebService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontracts.v2.rivtabp21.GetSupportedServiceContractsResponderInterface;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsResponseType;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsType;
import se.rivta.infrastructure.itintegration.registry.v2.ServiceContractNamespaceType;
import se.skltp.tak.services.TakCacheService;

@Slf4j
@AllArgsConstructor
@WebService(
    serviceName = "GetSupportedServiceContractsResponderService",
    portName = "GetSupportedServiceContractsResponderPort",
    targetNamespace = "urn:riv:infrastructure:itintegration:registry:GetSupportedServiceContracts:2:rivtabp21")
public class GetSupportedServiceContractsServiceV2Impl implements GetSupportedServiceContractsResponderInterface {

  @NonNull
  private final TakCacheService takCacheService;

  @Override
  public GetSupportedServiceContractsResponseType getSupportedServiceContracts(
      String logicalAddress, GetSupportedServiceContractsType supportedServiceContractsType) {
    log.info("Request to GetSupportedServiceContractsService V2");

    final String requestLogicalAdress = supportedServiceContractsType.getLogicalAdress();
    if (requestLogicalAdress == null || requestLogicalAdress.trim().equals("")) {
      throw new IllegalArgumentException("LogicalAddress must not be empty or null");
    }

    final String requestConsumerHsaId = supportedServiceContractsType.getServiceConsumerHsaId();

    final GetSupportedServiceContractsResponseType response = new GetSupportedServiceContractsResponseType();
    final Set<String> ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(requestLogicalAdress, requestConsumerHsaId);

    for (final String s : ns) {
      final ServiceContractNamespaceType sc = new ServiceContractNamespaceType();
      sc.setServiceContractNamespace(s);

      response.getServiceContractNamespace().add(sc);
    }

    log.info("Response returned from GetSupportedServiceContractsService V2. Number service contracts found:"+ns.size());
    return response;
  }


}
