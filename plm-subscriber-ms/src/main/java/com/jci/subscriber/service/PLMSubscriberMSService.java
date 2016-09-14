package com.jci.subscriber.service;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;

import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;

public interface PLMSubscriberMSService {

	public ServiceBusContract azureConnectionSetup();

	public List<ServiceInstance> serviceInstancesByApplicationName(String applicationName);

	public boolean azureMessagePublisher(ServiceBusContract service, String message);

	public boolean azureMessageSubscriber(ServiceBusContract service) throws ServiceException;

	public boolean readBlobXML(String ecnNo);

}
