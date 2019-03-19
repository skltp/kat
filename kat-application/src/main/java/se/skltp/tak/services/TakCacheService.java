package se.skltp.tak.services;

import java.util.List;
import java.util.Set;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.skltp.takcache.TakCacheLog;

public interface TakCacheService {

  TakCacheLog refresh();

  Set<String> getAllSupportedNamespacesByLogicalAddressAndConsumer(String logicalAddress, String consumerId);

  List<LogicalAddresseeRecordType> getLogicalAddressesByServiceContractAndConsumer(String serviceContractNS, String consumerId);
}
