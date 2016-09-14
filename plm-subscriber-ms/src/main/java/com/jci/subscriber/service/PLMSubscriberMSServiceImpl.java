package com.jci.subscriber.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.jci.subscriber.dao.PLMSubscriberMSDao;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;

@Service
@Configuration
public class PLMSubscriberMSServiceImpl implements PLMSubscriberMSService {

	private static final Logger LOG = LoggerFactory.getLogger(PLMSubscriberMSServiceImpl.class);

	@Value("${azure.storage.namespace}")
	private String nameSpace;

	@Value("${azure.storage.saspolicykeyname}")
	private String sasPolicyKeyName;

	@Value("${azure.storage.saspolicykey}")
	private String sasPolicyKey;

	@Value("${azure.storage.servicebusrooturi}")
	private String serviceBusRootURI;

	@Value("${azure.storage.queuename}")
	private String queueName;

	@Value("${hashmap.key.ecnnumber}")
	private String ecnNumberKey;

	@Value("${hashmap.key.xml}")
	private String xmlKey;

	@Value("${azure.xml.payload.subribedfile.xmltag.ecnno}")
	private String xmlECNNoTag;

	@Value("${apigatewayms.name}")
	private String apigatewaymsName;

	@Value("${plmpayloadprocessms.resource}")
	private String plmpayloadprocessmsResource;

	@Value("${plmstoragems.resource}")
	private String plmstoragemsResource;

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private PLMSubscriberMSDao plmSubscriberMSDao;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private DiscoveryClient discoveryClient;

	public List<ServiceInstance> serviceInstancesByApplicationName(String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

	public ServiceBusContract azureConnectionSetup() {
		LOG.info("###### Starting PLMSubscriberMSServiceImpl.azureConnectionSetup");
		com.microsoft.windowsazure.Configuration config = ServiceBusConfiguration
				.configureWithSASAuthentication(nameSpace, sasPolicyKeyName, sasPolicyKey, serviceBusRootURI);
		ServiceBusContract service = ServiceBusService.create(config);
		LOG.info("###### Ending PLMSubscriberMSServiceImpl.azureConnectionSetup");
		return service;
	}

	public boolean azureMessagePublisher(ServiceBusContract service, String message) {
		LOG.info("###### Starting PLMSubscriberMSServiceImpl.azureMessagePublisher");
		try {
			BrokeredMessage brokeredMessage = new BrokeredMessage(message);
			service.sendQueueMessage(queueName, brokeredMessage);
		} catch (ServiceException e) {
			LOG.error(
					"ServiceException encountered in PLMSubscriberMSServiceImpl.azureMessagePublisher while sending messages to queue: ",
					e);
			return false;
		}
		LOG.info("###### Ending PLMSubscriberMSServiceImpl.azureMessagePublisher");
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean azureMessageSubscriber(ServiceBusContract service) throws ServiceException {
		LOG.info("###### Starting PLMSubscriberMSServiceImpl.azureMessageSubscriber");
		/*
		 * ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
		 * opts.setReceiveMode(ReceiveMode.PEEK_LOCK);
		 */

		// We are setting max size of the xml file as 64 KB
		List<ServiceInstance> apigatewaymsInstanceList = discoveryClient.getInstances(apigatewaymsName);
		ServiceInstance apigatewaymsInstance = apigatewaymsInstanceList.get(0);

		service.getQueue(queueName).getValue().setMaxSizeInMegabytes((long) 1);
		BrokeredMessage message = service.receiveQueueMessage(queueName).getValue();
		// ReceiveQueueMessageResult resultQM =
		// service.receiveQueueMessage(queueName);
		// BrokeredMessage message = resultQM.getValue();
		StreamSource source = null;
		ResponseEntity<String> response = null;
		LOG.info("Message: " + message);
		// LOG.info("message.getMessageId(): " + message.getMessageId());
		try {
			if (message != null) {
				source = new StreamSource(message.getBody());
				StringWriter outWriter = new StringWriter();
				StreamResult result = new StreamResult(outWriter);
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer();
				try {
					transformer.transform(source, result);
				} catch (Exception e) {
					LOG.error(
							"Exception while parsing XML from queue PLMSubscriberMSServiceImpl.azureMessageSubscriber",
							e);
					// service.unlockMessage(message);
					return false;
				}
				StringBuffer sb = outWriter.getBuffer();
				String finalstring = sb.toString();

				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource src = new InputSource();
				src.setCharacterStream(new StringReader(finalstring));

				Document doc = builder.parse(src);
				String ecnNo = doc.getElementsByTagName(xmlECNNoTag).item(0).getTextContent();

				LOG.info("XML Content: " + finalstring);
				try {
					// Sending the payload to DAO for insertion
					LOG.info("########Starting Inserting to Storage MS block########");
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put(xmlKey, finalstring.toString());
					hashMap.put(ecnNumberKey, ecnNo);
					if (plmSubscriberMSDao.insertPayloadXMLToBlob(hashMap)) {
						LOG.info("Inserted successfully");
					} else {
						LOG.info("Insertion Failed");
					}
				} catch (Exception e) {
					LOG.error(
							"Exception during posting XML to storage MS in PLMSubscriberMSServiceImpl.azureMessageSubscriber",
							e);
					// service.unlockMessage(message);
					return false;
				}
				try {
					// sending the payload to PayloadProcess MS
					LOG.info("########Starting Posting messages to PayloadProcess MS block########");
					LOG.info("We are going to Hit " + apigatewaymsInstance.getUri().toString()
							+ plmpayloadprocessmsResource);
					HttpEntity entity = new HttpEntity(finalstring, new HttpHeaders());
					response = restTemplate.exchange(
							apigatewaymsInstance.getUri().toString() + plmpayloadprocessmsResource, HttpMethod.POST,
							entity, String.class);
					LOG.info("Response: " + response);
					LOG.info("########Ending Posting messages to PayloadProcess MS block########");
				} catch (Exception e) {
					LOG.error(
							"Exception during posting JSON to PayloadProcess MS in PLMSubscriberMSServiceImpl.azureMessageSubscriber",
							e);
				}
				// }
			} else {
				LOG.info("########No messaages in queue########");
				LOG.info("########Ending PLMSubscriberMSServiceImpl.azureMessageSubscriber########");
			}
		} catch (Exception e) {
			LOG.error("Generic exception encountered in PLMSubscriberMSServiceImpl.azureMessageSubscriber: ", e);
			LOG.info("###### Ending PLMSubscriberMSServiceImpl.azureMessageSubscriber");
			return false;
		}
		/*
		 * try { service.deleteMessage(message); } catch (ServiceException e) {
		 * LOG.error(
		 * "Exception encountered in PLMSubscriberMSServiceImpl.azureMessageSubscriber while deleting message from queue: "
		 * , e); LOG.info(
		 * "###### Ending PLMSubscriberMSServiceImpl.azureMessageSubscriber"); }
		 */
		return true;
	}

	@Override
	public boolean readBlobXML(String ecnNo) {
		return plmSubscriberMSDao.readBlobXML(ecnNo);
	}
}
