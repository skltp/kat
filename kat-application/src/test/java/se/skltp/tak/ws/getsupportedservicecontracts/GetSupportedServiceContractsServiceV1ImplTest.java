package se.skltp.tak.ws.getsupportedservicecontracts;

import static org.mockito.ArgumentMatchers.anyString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.rivta.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsResponseType;
import se.rivta.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsType;
import se.skltp.tak.services.TakCacheService;


public class GetSupportedServiceContractsServiceV1ImplTest {

  @Mock
  TakCacheService takCacheService;

  GetSupportedServiceContractsServiceV1Impl getSupportedServiceContractsServiceV1;

  @Before
  public void init(){
    MockitoAnnotations.openMocks(this);

    getSupportedServiceContractsServiceV1 = new GetSupportedServiceContractsServiceV1Impl(takCacheService);
  }

  @Test
  public void contractNamespacesShouldBeInResult() throws Exception {
    Set<String> ns = new HashSet<>(Arrays.asList("ns-1", "ns-2"));
    Mockito.when(takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(anyString(), anyString()))
        .thenReturn(ns);
    GetSupportedServiceContractsType request = createRequest("receiver-1", "sender-1");
    GetSupportedServiceContractsResponseType response =  getSupportedServiceContractsServiceV1.getSupportedServiceContracts("", request);
    Assert.assertEquals(2, response.getServiceContractNamespace().size());
    Assert.assertEquals("ns-2", response.getServiceContractNamespace().get(0).getServiceContractNamespace());
    Assert.assertEquals("ns-1", response.getServiceContractNamespace().get(1).getServiceContractNamespace());
  }

  @Test(expected = IllegalArgumentException.class)
  public void noReceiverInRequestShouldThrowException() throws Exception {
    GetSupportedServiceContractsType request = createRequest(null, "sender-1");
    getSupportedServiceContractsServiceV1.getSupportedServiceContracts("toTest", request);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyReceiverInRequestShouldThrowException() throws Exception {
    GetSupportedServiceContractsType request = createRequest("", "sender-1");
    getSupportedServiceContractsServiceV1.getSupportedServiceContracts("toTest", request);
  }

  @Test(expected = IllegalArgumentException.class)
  public void noSederInRequestShouldThrowException() throws Exception {
    GetSupportedServiceContractsType request = createRequest("receiver-1", null);
    getSupportedServiceContractsServiceV1.getSupportedServiceContracts("toTest", request);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptySenderInRequestShouldThrowException() throws Exception {
    GetSupportedServiceContractsType request = createRequest("receiver-1", "");
    getSupportedServiceContractsServiceV1.getSupportedServiceContracts("toTest", request);
  }


  private GetSupportedServiceContractsType createRequest(String receiverId, String senderId) {
    GetSupportedServiceContractsType request = new GetSupportedServiceContractsType();
    request.setServiceConsumerHsaId(senderId);
    request.setLogicalAdress(receiverId);
    return request;
  }

}