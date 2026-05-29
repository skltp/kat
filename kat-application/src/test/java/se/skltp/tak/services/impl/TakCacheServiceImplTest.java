package se.skltp.tak.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.CATEGORIZATION_1;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.DOMAIN_1;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.NAMNRYMD_1;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.NAMNRYMD_2;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.RECEIVER_1;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.SENDER_1;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.SENDER_2;
import static se.skltp.tak.mock.ws.utils.TestTakDataDefines.SENDER_3;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.AN_HOUR_AGO;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.IN_ONE_HOUR;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.createFilterInfo;
import static se.skltp.tak.mock.ws.utils.VagvalSchemasTestUtil.getRelativeDate;
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

    assertFalse(takCacheService.isInitialized());
    takCacheService.refresh();
    assertTrue(takCacheService.isInitialized());
  }

  @Test
  void isInitializedShouldNotBeSetAfterRefreshFailed(){
    TakCacheLog failedLog = new TakCacheLog();
    failedLog.setRefreshStatus(TakCacheLog.RefreshStatus.REFRESH_FAILED);
    Mockito.when(takCache.refresh()).thenReturn(failedLog);

    takCacheService.refresh();
    assertFalse(takCacheService.isInitialized());
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
  void getAllSupportedNamespacesByLogicalAddressAndConsumerShouldHandleNullConsumerAndCaseInsensitiveIds() {
    stubAuthorizationData(createAnropsBehorigheter());

    Set<String> ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(RECEIVER_1.toUpperCase(), null);
    assertEquals(2, ns.size());
    assertTrue(ns.contains(NAMNRYMD_1));
    assertTrue(ns.contains(NAMNRYMD_2));

    ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(RECEIVER_1, SENDER_1.toUpperCase());
    assertEquals(2, ns.size());
    assertTrue(ns.contains(NAMNRYMD_1));
    assertTrue(ns.contains(NAMNRYMD_2));
  }

  @Test
  void getAllSupportedNamespacesByLogicalAddressAndConsumerShouldIncludeOnlyCurrentlyValidAuthorizations() {
    List<AnropsBehorighetsInfoType> authorizations = new ArrayList<>();
    authorizations.add(createAuthorization(SENDER_1, NAMNRYMD_1, RECEIVER_1, getRelativeDate(AN_HOUR_AGO), getRelativeDate(IN_ONE_HOUR)));
    authorizations.add(createAuthorization(SENDER_1, NAMNRYMD_2, RECEIVER_1, getRelativeDate(IN_ONE_HOUR), getRelativeDate(IN_ONE_HOUR)));
    authorizations.add(createAuthorization(SENDER_1, NAMNRYMD_2, RECEIVER_1, getRelativeDate(AN_HOUR_AGO), getRelativeDate(AN_HOUR_AGO)));
    stubAuthorizationData(authorizations);

    Set<String> ns = takCacheService.getAllSupportedNamespacesByLogicalAddressAndConsumer(RECEIVER_1, SENDER_1);
    assertEquals(1, ns.size());
    assertTrue(ns.contains(NAMNRYMD_1));
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
  void getLogicalAddressesByServiceContractAndConsumerShouldBeCaseSensitive() {
    stubAuthorizationData(createAnropsBehorigheter());

    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1.toUpperCase(), SENDER_1);
    assertTrue(addresses.isEmpty());

    addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_1.toUpperCase());
    assertTrue(addresses.isEmpty());
  }

  @Test
  void getLogicalAddressesByServiceContractAndConsumerShouldNotGiveDuplicates() {
    stubAuthorizationData(createAnropsBehorigheterWithDuplicates());

    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_1);
    assertEquals(1, addresses.size());
    assertEquals(RECEIVER_1, addresses.get(0).getLogicalAddress());
  }

  @Test
  void getLogicalAddressesByServiceContractAndConsumerShouldMapAllFilterCategorizations() {
    AnropsBehorighetsInfoType authorization = createAuthorization(SENDER_1, NAMNRYMD_1, RECEIVER_1);
    authorization.getFilterInfo().add(createFilterInfo(DOMAIN_1, CATEGORIZATION_1 + ",cat-2"));
    stubAuthorizationData(List.of(authorization));

    List<LogicalAddresseeRecordType> addresses = takCacheService.getLogicalAddressesByServiceContractAndConsumer(NAMNRYMD_1, SENDER_1);
    assertEquals(1, addresses.size());
    assertEquals(1, addresses.get(0).getFilter().size());
    assertEquals(DOMAIN_1, addresses.get(0).getFilter().get(0).getServiceDomain());
    assertEquals(2, addresses.get(0).getFilter().get(0).getCategorization().size());
    assertTrue(addresses.get(0).getFilter().get(0).getCategorization().contains(CATEGORIZATION_1));
    assertTrue(addresses.get(0).getFilter().get(0).getCategorization().contains("cat-2"));
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