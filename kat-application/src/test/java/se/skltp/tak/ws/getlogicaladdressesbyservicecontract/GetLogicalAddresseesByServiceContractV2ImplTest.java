package se.skltp.tak.ws.getlogicaladdressesbyservicecontract;

import static org.mockito.ArgumentMatchers.anyString;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractResponseType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.rivta.infrastructure.itintegration.registry.v2.ServiceContractNamespaceType;
import se.skltp.tak.services.TakCacheService;


public class GetLogicalAddresseesByServiceContractV2ImplTest {

  @Mock
  TakCacheService takCacheService;

  GetLogicalAddresseesByServiceContractV2Impl getLogicalAddresseesByServiceContractV2;

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);

    getLogicalAddresseesByServiceContractV2 = new GetLogicalAddresseesByServiceContractV2Impl(takCacheService);
  }

  @Test
  public void resultContainsCorrectLogicalAddresses() {
    LogicalAddresseeRecordType recordType1 = createLogicalAddress("receiver-1");
    LogicalAddresseeRecordType recordType2 = createLogicalAddress("receiver-2");
    List<LogicalAddresseeRecordType> mockResult = Arrays.asList(recordType1, recordType2);
    Mockito.when(takCacheService.getLogicalAddressesByServiceContractAndConsumer(anyString(), anyString()))
        .thenReturn(mockResult);

    GetLogicalAddresseesByServiceContractResponseType response = getLogicalAddresseesByServiceContractV2
        .getLogicalAddresseesByServiceContract("toTest", createRequest("sender-1", "namespace-1"));

    Assert.assertEquals(2, response.getLogicalAddressRecord().size());
    Assert.assertEquals("receiver-1", response.getLogicalAddressRecord().get(0).getLogicalAddress());
    Assert.assertEquals("receiver-2", response.getLogicalAddressRecord().get(1).getLogicalAddress());
  }

  @Test(expected = IllegalArgumentException.class)
  public void noSenderInRequestShouldThrowException() throws Exception {
    GetLogicalAddresseesByServiceContractType request = createRequest(null, "sender-1");
    getLogicalAddresseesByServiceContractV2.getLogicalAddresseesByServiceContract("toTest", request);
  }

  @Test(expected = IllegalArgumentException.class)
  public void noContractInRequestShouldThrowException() throws Exception {
    GetLogicalAddresseesByServiceContractType request = createRequest("receiver-1", null);
    getLogicalAddresseesByServiceContractV2.getLogicalAddresseesByServiceContract("toTest", request);
  }

  private LogicalAddresseeRecordType createLogicalAddress(String logicalAddress) {
    LogicalAddresseeRecordType recordType1 = new LogicalAddresseeRecordType();
    recordType1.setLogicalAddress(logicalAddress);
    return recordType1;
  }

  private GetLogicalAddresseesByServiceContractType createRequest(String senderId, String contractNamespace) {
    GetLogicalAddresseesByServiceContractType request = new GetLogicalAddresseesByServiceContractType();
    request.setServiceConsumerHsaId(senderId);
    ServiceContractNamespaceType serviceContractNamespaceType = new ServiceContractNamespaceType();
    serviceContractNamespaceType.setServiceContractNamespace(contractNamespace);
    request.setServiceContractNameSpace(serviceContractNamespaceType);
    return request;
  }

}