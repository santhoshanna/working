package com.jci.plmacknowledgement.dao;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.jci.plmacknowledgement.domain.PLMPayloadTableEntity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;

@Repository
@Configuration
public class PLMACKMSDaoImpl implements PLMACKMSDao {

	private static final Logger LOG = LoggerFactory.getLogger(PLMACKMSDaoImpl.class);

	@Value("${azure.storage.connectionstring}")
	private String connectionString;

	@Value("${azure.storage.plmpayloadtablename}")
	private String plmPayloadTableName;

	@Value("${azure.storage.partionkey.plmpayload.isacknowledged}")
	private String plmPayloadIsAcknowledged;

	@Value("${ptc.acknowledgement.url}")
	private String ptcURL;

	@Value("${ptc.acknowledgement.url.username}")
	private String ptcUN;

	@Value("${ptc.acknowledgement.url.password}")
	private String ptcPW;

	@Value("${ptc.acknowledgement.url.parameter1}")
	private String ptcParameter1;

	@Value("${ptc.acknowledgement.url.parameter2}")
	private String ptcParameter2;

	@Autowired
	RestTemplate resttemplate;

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	RestTemplate restTemplate;

	@Override
	public void readPayloadTableEntityList() {
		LOG.debug("#### Starting PLMACKMSDaoImpl.readPayloadTableEntityList ###");
		try {
			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
			CloudTableClient tableClient = storageAccount.createCloudTableClient();
			CloudTable cloudTable = tableClient.getTableReference(plmPayloadTableName);
			cloudTable.createIfNotExists();
			String acknowledgementFilter = TableQuery.generateFilterCondition(plmPayloadIsAcknowledged,
					QueryComparisons.EQUAL, 0);
			TableQuery<PLMPayloadTableEntity> query = TableQuery.from(PLMPayloadTableEntity.class)
					.where(acknowledgementFilter);
			Iterable<PLMPayloadTableEntity> iterator = cloudTable.execute(query);
			for (PLMPayloadTableEntity entity : iterator) {
				MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
				params.add(ptcParameter1, entity.getTransactionID());
				params.add(ptcParameter2, entity.getStatus());
				UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ptcURL).queryParams(params).build();
				URL ptcACKPostURL = new URL(uriComponents.toUriString());
				ResponseEntity<String> repsonse = restTemplate.exchange(ptcACKPostURL.toString(), HttpMethod.POST, null,
						String.class);
				LOG.info("Response from PTC: " + repsonse);
				// Validate below things as when we get response from PTC
				// entity.setAcknowledgementCode(repsonse.getStatusCode().value());
				// entity.setAcknowledgementDate(format.format(date));
				// entity.setAcknowledgementStatus(repsonse.getStatusCode().toString());
				// entity.setAcknowledgementMessage("");
				TableOperation insert = TableOperation.insertOrReplace(entity);
				try {
					cloudTable.execute(insert);
				} catch (Exception e) {
					LOG.error("#### Failed to update PLM Payload Table with acknowledgement status ###", e);
				}
			}

		} catch (Exception e) {
			LOG.info("#### Generic exception encountered in PLMACKMSDaoImpl.readPayloadTableEntityList ###", e);
			LOG.info("#### Ending PLMACKMSDaoImpl.readPayloadTableEntityList ###", e);
		}

	}

}
