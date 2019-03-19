package se.skltp.tak.services.impl;

import static org.mockito.Mockito.times;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.*;
//import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.DOMAIN_1;
//import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.NAMNRYMD_1;
//import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.NAMNRYMD_2;
//import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.RECEIVER_1;
//import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.SENDER_1;
//import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.SENDER_2;
//import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.SENDER_3;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.createAuthorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.skltp.tak.mock.ws.utils.TestTakDataDefines;
import se.skltp.tak.mock.ws.utils.VagvalSchemasTestListsUtil;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.takcache.TakCache;
import se.skltp.takcache.TakCacheLog;

public class TakCacheServiceImplTest {
  @Mock
  TakCache takCache;

  TakCacheServiceImpl takCacheService;

  @Before
  public void init(){
    MockitoAnnotations.initMocks(this);
    Mockito.when(takCache.refresh()).thenReturn(new TakCacheLog());
    Mockito.when(takCache.getAnropsBehorighetsInfos()).thenReturn(createAnropsBehorigheter());

    takCacheService = new TakCacheServiceImpl(takCache);
  }

  @Test
  public void refreshMethodShouldRefreshTakCache(){
    takCacheService.refresh();
    Mockito.verify(takCache, times(1)).refresh();
  }

  @Test
  public void getAllSupportedNamespacesByLogicalAddressAndConsumerShouldGiveCorrectResult(){
    Set<String> ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(RECEIVER_1,SENDER_1);
    Assert.assertEquals(2, ns.size());
    Assert.assertTrue(ns.contains(NAMNRYMD_1));
    Assert.assertTrue(ns.contains(NAMNRYMD_2));

    ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(RECEIVER_1,SENDER_2);
    Assert.assertEquals(1, ns.size());
    Assert.assertTrue(ns.contains(NAMNRYMD_1));

  }

  @Test
  public void getAllSupportedNamespacesByLogicalAddressAndConsumerShouldCheckValidTimes() {
    Set<String> ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(RECEIVER_1,SENDER_3);
    Assert.assertTrue(ns.isEmpty());
  }

    @Test
  public void getLogicalAddressesByServiceContractAndConsumer() {
    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_1);
    Assert.assertEquals(1, addresses.size());
    Assert.assertEquals(RECEIVER_1, addresses.get(0).getLogicalAddress() );
    Assert.assertEquals(1, addresses.get(0).getFilter().size() );
    Assert.assertEquals(DOMAIN_1, addresses.get(0).getFilter().get(0).getServiceDomain() );
  }

  @Test
  public void getLogicalAddressesByServiceContractAndConsumerShouldCheckValidTimes() {
    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_3);
    Assert.assertTrue( addresses.isEmpty());
  }

  @Test
  public void getLogicalAddressesByServiceContractAndConsumerShouldNotGiveDuplicates() {
    Mockito.when(takCache.getAnropsBehorighetsInfos()).thenReturn(createAnropsBehorigheterWithDuplicates());

    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_1);
    Assert.assertEquals(1, addresses.size());
    Assert.assertEquals(RECEIVER_1, addresses.get(0).getLogicalAddress() );
  }

  private List<AnropsBehorighetsInfoType> createAnropsBehorigheter() {
    return VagvalSchemasTestListsUtil.getStaticBehorighetList();
  }

  public static List<AnropsBehorighetsInfoType> createAnropsBehorigheterWithDuplicates() {
    List<AnropsBehorighetsInfoType> authorization = new ArrayList<AnropsBehorighetsInfoType>();
    authorization.add(createAuthorization(TestTakDataDefines.SENDER_1, TestTakDataDefines.NAMNRYMD_1, TestTakDataDefines.RECEIVER_1));
    authorization.add(createAuthorization(TestTakDataDefines.SENDER_1, TestTakDataDefines.NAMNRYMD_1, TestTakDataDefines.RECEIVER_1));
    return authorization;
  }
}