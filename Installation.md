# Delivery Manager Installation Guide #
## **Software requirements:** ##
  * Java Development Kit 5.0
  * JBoss Application Server 4.2.2 version with EJB 3.0 support
  * Secse Resistry  2.4.2 version

## **Network requirements:** ##
  * The delivery manager can connect to the 5555 tpc port of any computer
  * The delivery manager can connect to the 5556 tpc port of any computer
  * Any computer can contact the 5555 tpc port of your computer
  * Any computer can contact the 5556 tpc port of your computer

## **Installation:** ##
Insert in _JBOSS\_HOME/server/default/deploy_ folder the following files:
  1. _mail-service.xml_ and _deliveryManagerJMS-Service.xml_ files that are in descriptors folder of the zip file.
  1. _deliverymanager.ear_ file.

## **Configuration files** ##

### **_mail-service.xml_** ###
This file connects the JavaMail to a gmail account. Please notice that you have to substitute the _username_ and the _password_ with your values.

### **_deliverymanager.ear/deliverymanager.jar/config.properties_** ###
```
RegistryProxy.url=http://localhost:8080/SeCSERegistry/services/SeCSERegistry
RegistryProxy.username=deliveryManager
RegistryProxy.password=deliveryManagerPwd
RegistryProxy.name=deliveryManagerSp
LoggerProxy.url=http://localhost:8080/SeCSELogger/services/SeCSELogger
LoggerProxy.applicationId=deliverymanager
LoggerProxy.applicationName=deliverymanager
KeepAlive=false
Directory.synchronize=Manual
Gossip.Copyforwarding=2
Gossip.subscription_timeout=86400000
Gossip.partial_expire=8640000
Proxy.keys=gossip,ps
Proxy.value.gossip=deliverymanager/GossipProxy/local
Proxy.value.ps=deliverymanager/PubSubProxy/local
Directory.WSEndpoint=http://localhost:8080/deliverymanager/DirectoryLookupWS
Gossip.Port:5556
Gossip.URL:localhost
Reds.port:5555
RedsMBean.BROKER=reds-tcp:broker-url:5555
NotificationMethods.keys=email,queue
NotificationMethods.value.email=deliverymanager/Mailer/local
NotificationMethods.value.queue=deliverymanager/JmsSender/local
```


  * Substitute the hostname and port of RegistryProxy, whit _address:port_ of the computer in which the SecseRegistry component is deployed.
  * Substitute the hostname and port of LoggerProxy whit _address:port_ of the computer in which the SecseLogger component is deployed.
  * Substitute the hostname and port of Directory.WSEndpoint whit _address:port_ of the computer  in which the delivery manager is installed.
  * Substitute the hostname of Gossip whit _address_ of the computer in which the delivery manager is installed. Instead the 5556 port is fixed and it must not be changed.
  * Substitute RedsMBean.BROKER hostname (_broker-url_) with the machine hostname in witch the broker is installed.