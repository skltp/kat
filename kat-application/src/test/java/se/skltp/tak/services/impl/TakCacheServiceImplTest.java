package se.skltp.tak.services.impl;

import static org.mockito.Mockito.times;
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
import se.skltp.takcache.BehorigheterCache;
import se.skltp.takcache.TakCache;
import se.skltp.takcache.TakCacheLog;

public class TakCacheServiceImplTest {
  public static final String ADDRESS_1 = "address-1";
  public static final String ADDRESS_2 = "address-2";
  public static final String RIV20 = "RIVTABP20";
  public static final String RIV21 = "RIVTABP21";
  public static final String NAMNRYMD_1 = "namnrymd-1";
  public static final String NAMNRYMD_2 = "namnrymd-2";
  public static final String RECEIVER_1 = "receiver-1";
  public static final String RECEIVER_2 = "receiver-2";
  public static final String SENDER_1 = "sender-1";
  public static final String SENDER_2 = "sender-2";
  public static final String SENDER_3 = "sender-3";
  public static final String DOMAIN_1 = "domain-1";
  public static final String CATEGORIZATION_1 = "cat-1";

  @Mock
  TakCache takCache;

  @Mock
  BehorigheterCache behorigheterCache;

  TakCacheServiceImpl takCacheService;

  @Before
  public void init(){
    MockitoAnnotations.openMocks(this);
    TakCacheLog log = new TakCacheLog();
    log.setRefreshStatus(TakCacheLog.RefreshStatus.REFRESH_OK);
    Mockito.when(takCache.refresh()).thenReturn(log);
    Mockito.when(takCache.getBehorigeterCache()).thenReturn(behorigheterCache);
    Mockito.when(behorigheterCache.getAnropsBehorighetsInfos()).thenReturn(createAnropsBehorigheter());

    takCacheService = new TakCacheServiceImpl(takCache);
  }

  @Test
  public void refreshMethodShouldRefreshTakCache(){
    takCacheService.refresh();
    Mockito.verify(takCache, times(1)).refresh();
  }

  @Test
  public void refreshMethodShouldSetLastRefreshLog(){
    TakCacheLog refreshLog = takCacheService.refresh();
    TakCacheLog lastLog = takCacheService.getLastRefreshLog();
    Assert.assertEquals(refreshLog, lastLog);
  }

  @Test
  public void isInitializedShouldBeSetAfterRefreshOk(){
    Assert.assertFalse(takCacheService.isInitalized());
    takCacheService.refresh();
    Assert.assertTrue(takCacheService.isInitalized());
  }

  @Test
  public void isInitializedShouldNotBeSetAfterRefreshFailed(){
    TakCacheLog failedLog = new TakCacheLog();
    failedLog.setRefreshStatus(TakCacheLog.RefreshStatus.REFRESH_FAILED);
    Mockito.when(takCache.refresh()).thenReturn(failedLog);

    takCacheService.refresh();
    Assert.assertFalse(takCacheService.isInitalized());
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
    Mockito.when(takCache.getBehorigeterCache().getAnropsBehorighetsInfos()).thenReturn(createAnropsBehorigheterWithDuplicates());

    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_1);
    Assert.assertEquals(1, addresses.size());
    Assert.assertEquals(RECEIVER_1, addresses.get(0).getLogicalAddress() );
  }

  private List<AnropsBehorighetsInfoType> createAnropsBehorigheter() {
    return VagvalSchemasTestListsUtil.getStaticBehorighetList();
  }

  public static List<AnropsBehorighetsInfoType> createAnropsBehorigheterWithDuplicates() {
    List<AnropsBehorighetsInfoType> authorization = new ArrayList<>();
    authorization.add(createAuthorization(TestTakDataDefines.SENDER_1, TestTakDataDefines.NAMNRYMD_1, TestTakDataDefines.RECEIVER_1));
    authorization.add(createAuthorization(TestTakDataDefines.SENDER_1, TestTakDataDefines.NAMNRYMD_1, TestTakDataDefines.RECEIVER_1));
    return authorization;
  }
}