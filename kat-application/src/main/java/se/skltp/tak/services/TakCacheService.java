package se.skltp.tak.services;

import java.util.List;
import java.util.Set;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.skltp.takcache.TakCacheLog;

public interface TakCacheService {

  TakCacheLog refresh();

  /**
   * Returns supported service contract namespaces for a logical address/consumer pair.
   *
   * <p>Identifier matching is intentionally case-insensitive for both logical address and consumer id.
   * This behavior is part of the current interoperability contract and should not be changed without
   * coordinated analysis of external specifications and consumers.</p>
   */
  Set<String> getAllSupportedNamespacesByLogicalAddressAndConsumer(String logicalAddress, String consumerId);

  /**
   * Returns logical addresses for a given service contract namespace and consumer id.
   *
   * <p>Identifier matching is intentionally case-sensitive for both namespace and consumer id.
   * This behavior is part of the current interoperability contract and should not be changed without
   * coordinated analysis of external specifications and consumers.</p>
   */
  List<LogicalAddresseeRecordType> getLogicalAddressesByServiceContractAndConsumer(String serviceContractNS, String consumerId);

  boolean isInitialized();

  TakCacheLog getLastRefreshLog();
}
