package se.skltp.tak.ws.getsupportedservicecontracts;

import java.util.Set;
import javax.jws.WebService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import se.rivta.itintegration.registry.getsupportedservicecontracts.v1.rivtabp21.GetSupportedServiceContractsResponderInterface;
import se.rivta.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsResponseType;
import se.rivta.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsType;
import se.rivta.itintegration.registry.v1.ServiceContractNamespaceType;
import se.skltp.tak.services.TakCacheService;

@Slf4j
@AllArgsConstructor
@WebService(
    serviceName = "GetSupportedServiceContractsResponderService",
    portName = "GetSupportedServiceContractsResponderPort",
    targetNamespace = "urn:riv:itintegration:registry:GetSupportedServiceContracts:1:rivtabp21")
public class GetSupportedServiceContractsServiceV1Impl implements
    GetSupportedServiceContractsResponderInterface {

  @NonNull
  private final TakCacheService takCacheService;

  @Override
  public GetSupportedServiceContractsResponseType getSupportedServiceContracts( String logicalAddress, GetSupportedServiceContractsType request) {

    log.info("Request to GetSupportedServiceContractsService V1");

    final String requestLogicalAdress = request.getLogicalAdress();
    if (requestLogicalAdress == null || requestLogicalAdress.trim().equals("")) {
      throw new IllegalArgumentException("LogicalAddress must not be empty or null");
    }

    final String requestConsumerHsaId = request.getServiceConsumerHsaId();
    if (requestConsumerHsaId == null || requestConsumerHsaId.trim().equals("")) {
      throw new IllegalArgumentException("ServiceConsumerHsaId must not be empty or null");
    }

    final GetSupportedServiceContractsResponseType response = new GetSupportedServiceContractsResponseType();
    final Set<String> ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(requestLogicalAdress, requestConsumerHsaId);

    for (final String s : ns) {
      final ServiceContractNamespaceType sc = new ServiceContractNamespaceType();
      sc.setServiceContractNamespace(s);

      response.getServiceContractNamespace().add(sc);
    }

    log.info("Response returned from GetSupportedServiceContractsService V1. Number service contracts found:"+ ns.size());
    return response;
  }




}
