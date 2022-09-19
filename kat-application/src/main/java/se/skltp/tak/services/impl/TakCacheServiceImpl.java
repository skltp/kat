package se.skltp.tak.services.impl;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.FilterType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.skltp.tak.services.TakCacheService;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.FilterInfoType;
import se.skltp.takcache.TakCache;
import se.skltp.takcache.TakCacheLog;
import se.skltp.takcache.util.XmlGregorianCalendarUtil;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;

@Slf4j
@Service
public class TakCacheServiceImpl implements TakCacheService {

  private TakCache takCache;

  @Autowired
  public TakCacheServiceImpl(TakCache takCache) {
    this.takCache = takCache;
  }

  @Override
  @Synchronized
  public TakCacheLog refresh(){
    TakCacheLog takCacheLog = takCache.refresh();
    log.info("Initial result of takCache.refresh. \nSuccessful: {}\nNumberBehorigheter: {}\nNumberVagval: {}",
        takCacheLog.isRefreshSuccessful(),
        takCacheLog.getNumberBehorigheter(),
        takCacheLog.getNumberVagval());
    return takCacheLog;
  }


  @Override
  @Synchronized
  public Set<String> getAllSupportedNamespacesByLogicalAddressAndConsumer(String logicalAddress, String consumerId) {
    final Set<String> namespaces = new HashSet<>();

    for (final AnropsBehorighetsInfoType authInfo : geAnropsBehorigheter()) {
      if (authInfo.getReceiverId().equalsIgnoreCase(logicalAddress) && isConsumerIdMatchingOrNull(authInfo, consumerId) && isValidAccordingToTime(authInfo) ) {
        namespaces.add(authInfo.getTjansteKontrakt());
      }
    }
    return namespaces;
  }

  @Override
  @Synchronized
  public List<LogicalAddresseeRecordType> getLogicalAddressesByServiceContractAndConsumer(String serviceContractNS, String consumerId){

    Map<String, LogicalAddresseeRecordType> uniqueLogicalAddresses = new HashMap<>();
    for (final AnropsBehorighetsInfoType authInfo : geAnropsBehorigheter()) {
      if ( !uniqueLogicalAddresses.containsKey(authInfo.getReceiverId()) &&
        matchNamespaceAndConsumer(authInfo, serviceContractNS, consumerId) &&
            isValidAccordingToTime(authInfo) ) {

        LogicalAddresseeRecordType logicalAddressee = new LogicalAddresseeRecordType();
        logicalAddressee.setLogicalAddress(authInfo.getReceiverId());
        addFilterToResponse(authInfo, logicalAddressee);

        uniqueLogicalAddresses.put(authInfo.getReceiverId(), logicalAddressee);
      }
    }

    return new ArrayList(uniqueLogicalAddresses.values());
  }

  public List<AnropsBehorighetsInfoType> geAnropsBehorigheter(){
    return takCache.getBehorigeterCache().getAnropsBehorighetsInfos();
  }

  private void addFilterToResponse(AnropsBehorighetsInfoType authInfo,
      LogicalAddresseeRecordType logicalAddressee) {

    if(authInfo == null || authInfo.getFilterInfo() == null){
      return;
    }

    for (FilterInfoType filterInfoType : authInfo.getFilterInfo()) {
      FilterType filter = new FilterType();
      filter.setServiceDomain(filterInfoType.getServiceDomain());
      filter.getCategorization().addAll(filterInfoType.getCategorization());
      logicalAddressee.getFilter().add(filter);
    }
  }


  private boolean isConsumerIdMatchingOrNull(AnropsBehorighetsInfoType authInfo, String consumerHsaId){
    return consumerHsaId==null || authInfo.getSenderId().equalsIgnoreCase(consumerHsaId);
  }

  private boolean matchNamespaceAndConsumer(AnropsBehorighetsInfoType authInfo, String namespace, String consumerId) {
    return authInfo.getSenderId().equals(consumerId) && authInfo.getTjansteKontrakt().equals(namespace);
  }

  private boolean isValidAccordingToTime(AnropsBehorighetsInfoType authInfo) {
    XMLGregorianCalendar requestTime = XmlGregorianCalendarUtil.getNowAsXMLGregorianCalendar();
    return requestTime.compare(authInfo.getFromTidpunkt()) != DatatypeConstants.LESSER
        && requestTime.compare(authInfo.getTomTidpunkt()) != DatatypeConstants.GREATER;
  }

}
