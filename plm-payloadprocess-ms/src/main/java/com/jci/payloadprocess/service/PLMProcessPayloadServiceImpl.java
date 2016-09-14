package com.jci.payloadprocess.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.jci.payloadprocess.domain.ERPMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
@Configuration
public class PLMProcessPayloadServiceImpl implements PLMProcessPayloadService {

	private static final Logger LOG = LoggerFactory.getLogger(PLMProcessPayloadServiceImpl.class);

	@Value("${xml.payload.filename}")
	private String xmlPayloadFileName;

	@Value("${xml.output.filename}")
	private String xmlOutputFileName;

	@Value("${xml.output.xmltags.collection}")
	private String xmltagsCollection;

	@Value("${xml.output.xmltags.bomcomponents}")
	private String xmltagsBOMComponents;

	@Value("${xml.output.xmltags.partcomponents}")
	private String xmltagsPartComponents;

	@Value("${xsl.input.filename}")
	private String xslInputFileName;

	@Value("${json.input.filename}")
	private String jsonInputFileName;

	@Value("${json.input.jsonpath.erp}")
	private String jsonpathERP;

	@Value("${json.input.jsonpath.region}")
	private String jsonpathRegion;

	@Value("${partbomms.url.parameter.bom}")
	private String urlparamBOM;

	@Value("${partbomms.url.parameter.part}")
	private String urlparamPart;

	@Value("${partbomms.url.parameter.erp}")
	private String urlparamERP;

	@Value("${partbomms.url.parameter.plant}")
	private String urlparamPlant;

	@Value("${partbomms.url.parameter.region}")
	private String urlparamRegion;

	@Value("${partbomms.url.parameter.ecnno}")
	private String urlparamECNNo;

	@Value("${partbomms.url.parameter.transactionid}")
	private String urlparamTransactionID;

	@Value("${partbomms.url.parameter.type}")
	private String urlparamType;

	@Value("${partbomms.url.parameter.description}")
	private String urlparamDescription;

	@Value("${partbomms.url.parameter.createdby}")
	private String urlparamCreatedBy;

	@Value("${apigatewayms.name}")
	private String apigatewaymsName;

	@Value("${plmpartbomms.resource}")
	private String plmpartbommsResource;

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	RestTemplate restTemplate;

	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
		LOG.info("#####Starting PLMProcessPayloadServiceImpl.serviceInstancesByApplicationName#####");
		LOG.info("#####Ending PLMProcessPayloadServiceImpl.serviceInstancesByApplicationName#####");
		return this.discoveryClient.getInstances(applicationName);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean processPayload(String completeXml, String ecnNo, String transactionId, String plant,
			String description, String type, String createdBy) {
		LOG.info("#####Starting PLMProcessPayloadServiceImpl.processPayload#####");
		try {
			List<ServiceInstance> apigatewaymsInstanceList = discoveryClient.getInstances(apigatewaymsName);
			ServiceInstance apigatewaymsInstance = apigatewaymsInstanceList.get(0);

			ResponseEntity<String> response = null;
			LOG.info("processpayload() is executed . . . . . . .");
			LOG.info("value of ecnNo is    " + ecnNo);
			LOG.info("value of transactionId is    " + transactionId);
			LOG.info("value of plant is    " + plant);
			LOG.info("value of description is    " + description);
			LOG.info("value of type is    " + type);
			LOG.info("value of createdBy is    " + createdBy);

			// JSON parsing
			JSONParser jsonParser = new JSONParser();
			String erp = "";
			String region = "";

			Object obj = jsonParser.parse(new FileReader(jsonInputFileName));
			String jsonStr = obj.toString();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ERPMapper erpMapper = mapper.readValue(jsonStr, ERPMapper.class);

			for (int i = 0; i < erpMapper.getMapping().size(); i++) {
				if (plant.equals(erpMapper.getMapping().get(i).getPlant())) {
					erp = erpMapper.getMapping().get(i).getErp();
					region = erpMapper.getMapping().get(i).getRegion();
				}
			}

			// XSLT transformer
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory
					.newTransformer(new javax.xml.transform.stream.StreamSource(xslInputFileName));
			transformer.transform(new StreamSource(new StringReader(completeXml)),
					new javax.xml.transform.stream.StreamResult(new FileOutputStream(xmlOutputFileName)));

			// converting XSLT Xml and Payload xml in string
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			InputStream outputXMLAfterXSLTInputStream = new FileInputStream(new File(xmlOutputFileName));
			StringWriter xmlStringWriter = new StringWriter();
			// StringWriter payloadXML = new StringWriter();
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			serializer.transform(
					new DOMSource(documentBuilderFactory.newDocumentBuilder().parse(outputXMLAfterXSLTInputStream)),
					new StreamResult(xmlStringWriter));

			JSONObject trsansformedPayloadJSON = XML.toJSONObject(xmlStringWriter.toString());
			JSONObject collectionPayload = (JSONObject) trsansformedPayloadJSON.get(xmltagsCollection);
		
			// sending to part-bom ms
			HashMap<String, Object> mvm = new HashMap<String, Object>();
			mvm.put(urlparamBOM, collectionPayload.get(xmltagsBOMComponents).toString());
			mvm.put(urlparamPart, collectionPayload.get(xmltagsPartComponents).toString());
			mvm.put(urlparamERP, erp);
			mvm.put(urlparamPlant, plant);
			mvm.put(urlparamRegion, region);
			mvm.put(urlparamECNNo, ecnNo);
			mvm.put(urlparamTransactionID, transactionId);
			mvm.put(urlparamDescription, description);
			mvm.put(urlparamType, type);
			mvm.put(urlparamCreatedBy, createdBy);

			HttpEntity entity = new HttpEntity(mvm, new HttpHeaders());
			LOG.info("url 1: " + apigatewaymsInstance.getUri().toString() + plmpartbommsResource);
			LOG.info("url 3: " + entity);

			response = restTemplate.exchange("http://plm-part-bom-ms:8002/processJSON", HttpMethod.POST, entity,
					String.class);

			LOG.info("Response from payload part-bom-ms" + response);
		} catch (Exception e) {
			LOG.error("Exception in PLMProcessPayloadServiceImpl.processPayload", e);
			return false;
		}
		LOG.info("#####Ending PLMProcessPayloadServiceImpl.processPayload#####");
		return true;
	}

	@Override
	@HystrixCommand(fallbackMethod = "error")
	public boolean hystrixCircuitBreaker() {
		LOG.info("#####Starting PLMProcessPayloadServiceImpl.hystrixCircuitBreaker#####");
		LOG.info("#####Ending PLMProcessPayloadServiceImpl.hystrixCircuitBreaker#####");
		return true;
	}

	public boolean error() {
		LOG.info("#####Starting PLMProcessPayloadServiceImpl.error#####");
		LOG.info("#####Ending PLMProcessPayloadServiceImpl.error#####");
		return true;
	}

}
