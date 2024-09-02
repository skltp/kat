# KAT 

KAT är producent för tjäntekontrakten:
 - urn:riv:infrastructure:itintegration:registry:GetSupportedServiceContractsResponder:2
 - urn:riv:infrastructure:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:2
 
KAT hämtar behörighetsinformation från TAKen och håller detta i en intern cache, till detta används projektet:
https://github.com/skltp/takcache
Cachen uppdateras vid uppstart samt vid anrop till resetCache REST tjänsten, se konfigurering nedan. 
Cachen används sedan för att ge svar på ovan nämnda tjänstekontrakt.

Projektet består av tre delar:
- kat-application
Springboot applikation. som är själva producentapplikationen
- kat-tak-mock
Springboot applikation. En mocktjänst av TAK services, används vi lokala tester av kat-application.
- kat-soapui-test
Ett soapui projekt som används för att köra tester mot kat-application.
    
### Köra lokala tester:
 - Starta kat-tak-mock som springbootapplikation.
 - Starta kat-application som springbootapplikation
 - Använd soapui och kat-soapui-test projektet för att göra anrop mot kat-application
 
## Konfigureringar

### kat-application
Se application.properties i kat-application projektet för aktuella default inställningar.
```sh
# Application listening port 
server.port=8080

# Basepath to webservices
cxf.path=/kat/ws

# KAT - subpaths to webservices
kat.getsupportedservicecontracts-v2-path=/GetSupportedServiceContracts/v2
kat.getlogicaladdresseesbyservicecontract-v2-path=/GetLogicalAddresseesByServiceContract/v2

# KAT - Path to REST resetcache service
kat.resetcache.path=/kat/resetcache

# Configurations for TakCache
takcache.use.behorighet.cache=true
takcache.use.vagval.cache=false
takcache.persistent.file.name=
takcache.endpoint.address=http://localhost:8882/takmockservice
takcache.header.user.agent=SKLTP VP/3.1
```

### kat-tak-mock
Se application.properties i kat-tak-mock projektet för aktuella default inställningar.
```sh
# Path to mocked services 
tak.mock.localWsHost=http://localhost:8882/takmockservice
```



