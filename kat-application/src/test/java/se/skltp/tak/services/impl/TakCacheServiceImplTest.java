package se.skltp.tak.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.createAuthorization;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.skltp.tak.mock.ws.utils.TestTakDataDefines;
import se.skltp.tak.mock.ws.utils.VagvalSchemasTestListsUtil;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.takcache.BehorigheterCache;
import se.skltp.takcache.TakCache;
import se.skltp.takcache.TakCacheLog;

@ExtendWith(MockitoExtension.class)
class TakCacheServiceImplTest {
  public static final String NAMNRYMD_1 = "namnrymd-1";
  public static final String NAMNRYMD_2 = "namnrymd-2";
  public static final String RECEIVER_1 = "receiver-1";
  public static final String SENDER_1 = "sender-1";
  public static final String SENDER_2 = "sender-2";
  public static final String SENDER_3 = "sender-3";
  public static final String DOMAIN_1 = "domain-1";

  @Mock
  TakCache takCache;

  @Mock
  BehorigheterCache behorigheterCache;

  TakCacheServiceImpl takCacheService;

  @BeforeEach
  void init(){
    takCacheService = new TakCacheServiceImpl(takCache);
  }

  @Test
  void refreshMethodShouldRefreshTakCache(){
    Mockito.when(takCache.refresh()).thenReturn(successfulRefreshLog());

    takCacheService.refresh();
    Mockito.verify(takCache, times(1)).refresh();
  }

  @Test
  void refreshMethodShouldSetLastRefreshLog(){
    Mockito.when(takCache.refresh()).thenReturn(successfulRefreshLog());

    TakCacheLog refreshLog = takCacheService.refresh();
    TakCacheLog lastLog = takCacheService.getLastRefreshLog();
    assertEquals(refreshLog, lastLog);
  }

  @Test
  void isInitializedShouldBeSetAfterRefreshOk(){
    Mockito.when(takCache.refresh()).thenReturn(successfulRefreshLog());

    assertFalse(takCacheService.isInitalized());
    takCacheService.refresh();
    assertTrue(takCacheService.isInitalized());
  }

  @Test
  void isInitializedShouldNotBeSetAfterRefreshFailed(){
    TakCacheLog failedLog = new TakCacheLog();
    failedLog.setRefreshStatus(TakCacheLog.RefreshStatus.REFRESH_FAILED);
    Mockito.when(takCache.refresh()).thenReturn(failedLog);

    takCacheService.refresh();
    assertFalse(takCacheService.isInitalized());
  }

  @Test
  void getAllSupportedNamespacesByLogicalAddressAndConsumerShouldGiveCorrectResult(){
    stubAuthorizationData(createAnropsBehorigheter());

    Set<String> ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(RECEIVER_1,SENDER_1);
    assertEquals(2, ns.size());
    assertTrue(ns.contains(NAMNRYMD_1));
    assertTrue(ns.contains(NAMNRYMD_2));

    ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(RECEIVER_1,SENDER_2);
    assertEquals(1, ns.size());
    assertTrue(ns.contains(NAMNRYMD_1));

  }

  @Test
  void getAllSupportedNamespacesByLogicalAddressAndConsumerShouldCheckValidTimes() {
    stubAuthorizationData(createAnropsBehorigheter());

    Set<String> ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(RECEIVER_1,SENDER_3);
    assertTrue(ns.isEmpty());
  }

  @Test
  void getLogicalAddressesByServiceContractAndConsumer() {
    stubAuthorizationData(createAnropsBehorigheter());

    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_1);
    assertEquals(1, addresses.size());
    assertEquals(RECEIVER_1, addresses.get(0).getLogicalAddress());
    assertEquals(1, addresses.get(0).getFilter().size());
    assertEquals(DOMAIN_1, addresses.get(0).getFilter().get(0).getServiceDomain());
  }

  @Test
  void getLogicalAddressesByServiceContractAndConsumerShouldCheckValidTimes() {
    stubAuthorizationData(createAnropsBehorigheter());

    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_3);
    assertTrue(addresses.isEmpty());
  }

  @Test
  void getLogicalAddressesByServiceContractAndConsumerShouldNotGiveDuplicates() {
    stubAuthorizationData(createAnropsBehorigheterWithDuplicates());

    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_1);
    assertEquals(1, addresses.size());
    assertEquals(RECEIVER_1, addresses.get(0).getLogicalAddress());
  }

  private void stubAuthorizationData(List<AnropsBehorighetsInfoType> behorigheter) {
    Mockito.when(takCache.getBehorigeterCache()).thenReturn(behorigheterCache);
    Mockito.when(behorigheterCache.getAnropsBehorighetsInfos()).thenReturn(behorigheter);
  }

  private TakCacheLog successfulRefreshLog() {
    TakCacheLog log = new TakCacheLog();
    log.setRefreshStatus(TakCacheLog.RefreshStatus.REFRESH_OK);
    return log;
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