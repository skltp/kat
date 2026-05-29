package se.skltp.tak.ws.getsupportedservicecontracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsResponseType;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsType;
import se.skltp.tak.services.TakCacheService;

/**
 * Created by gorerk on 3/15/2019.
 */
@ExtendWith(MockitoExtension.class)
class GetSupportedServiceContractsServiceV2ImplTest {
  @Mock
  TakCacheService takCacheService;

  GetSupportedServiceContractsServiceV2Impl getSupportedServiceContractsServiceV2;

  @BeforeEach
  void init(){

    getSupportedServiceContractsServiceV2 = new GetSupportedServiceContractsServiceV2Impl(takCacheService);
  }

  @Test
  void contractNamespacesShouldBeInResult() {
    Set<String> ns = new HashSet<>(Arrays.asList("ns-1", "ns-2"));
    Mockito.when(takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(anyString(), anyString()))
        .thenReturn(ns);
    GetSupportedServiceContractsType request = createRequest("receiver-1", "sender-1");
    GetSupportedServiceContractsResponseType response =  getSupportedServiceContractsServiceV2.getSupportedServiceContracts("", request);
    assertEquals(2, response.getServiceContractNamespace().size());
    assertEquals("ns-2", response.getServiceContractNamespace().get(0).getServiceContractNamespace());
    assertEquals("ns-1", response.getServiceContractNamespace().get(1).getServiceContractNamespace());
  }

  @Test
  void noReceiverInRequestShouldThrowException() {
    GetSupportedServiceContractsType request = createRequest(null, "sender-1");
    assertThrows(IllegalArgumentException.class,
        () -> getSupportedServiceContractsServiceV2.getSupportedServiceContracts("toTest", request));
  }

  @Test
  void emptyReceiverInRequestShouldThrowException() {
    GetSupportedServiceContractsType request = createRequest("", "sender-1");
    assertThrows(IllegalArgumentException.class,
        () -> getSupportedServiceContractsServiceV2.getSupportedServiceContracts("toTest", request));
  }

  private GetSupportedServiceContractsType createRequest(String receiverId, String senderId) {
    GetSupportedServiceContractsType request = new GetSupportedServiceContractsType();
    request.setServiceConsumerHsaId(senderId);
    request.setLogicalAdress(receiverId);
    return request;
  }

}