package se.skltp.tak.ws.getlogicaladdressesbyservicecontract;

import java.util.List;
import jakarta.jws.WebService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontract.v2.rivtabp21.GetLogicalAddresseesByServiceContractResponderInterface;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractResponseType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.skltp.tak.services.TakCacheService;

@Slf4j
@AllArgsConstructor
@WebService(serviceName = "GetLogicalAddresseesByServiceContractResponderService",
    portName = "GetLogicalAddresseesByServiceContractResponderPort",
    targetNamespace = "urn:riv:infrastructure:itintegration:registry:GetLogicalAddresseesByServiceContract:2:rivtabp21")
public class GetLogicalAddresseesByServiceContractV2Impl implements GetLogicalAddresseesByServiceContractResponderInterface {

  @NonNull
  private final TakCacheService takCacheService;

  @Override
  public GetLogicalAddresseesByServiceContractResponseType getLogicalAddresseesByServiceContract(
      String logicalAddress, GetLogicalAddresseesByServiceContractType request) {

    GetLogicalAddresseesByServiceContractResponseType response = new GetLogicalAddresseesByServiceContractResponseType();
    if (!requestIsValidAccordingToRivSpec(request)) {
      throw new IllegalArgumentException("Request missing expected parameters");
    }

    String consumerId = request.getServiceConsumerHsaId();
    String namespace = request.getServiceContractNameSpace().getServiceContractNamespace();
    List<LogicalAddresseeRecordType> logicalAddresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(namespace, consumerId);
    response.getLogicalAddressRecord().addAll(logicalAddresses);

    log.info("getLogicalAddresseesByServiceContract.v2 found {} logical addresses for consumerHsaId: {}, namespace: {}",
        logicalAddresses.size(), consumerId, namespace);

    return response;
  }


  static boolean requestIsValidAccordingToRivSpec(GetLogicalAddresseesByServiceContractType request) {
    return request.getServiceConsumerHsaId() != null && request.getServiceContractNameSpace() != null
        && request.getServiceContractNameSpace().getServiceContractNamespace() != null;
  }


}
