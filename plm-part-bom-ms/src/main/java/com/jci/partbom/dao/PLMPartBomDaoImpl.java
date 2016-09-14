package com.jci.partbom.dao;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.jci.partbom.domain.MiscTableEntity;
import com.jci.partbom.domain.PLMPayloadTableEntity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableEntity;
import com.microsoft.azure.storage.table.TableOperation;

@Service
@Configuration
public class PLMPartBomDaoImpl implements PLMPartBomDao {

	private static final Logger LOG = LoggerFactory.getLogger(PLMPartBomDaoImpl.class);

	@Value("${azure.storage.connectionstring}")
	private String connectionString;

	@Value("${azure.storage.miscdatatablename}")
	private String miscdataTableName;

	@Value("${azure.storage.plmpayloadtablename}")
	private String plmPayloadTableName;

	@Value("${azure.storage.partionkey.plmpayload}")
	private String plmPayloadPartitionKey;

/*	@Value("${azure.storage.partionkey.miscdata}")
	private String miscdataPartitionKey;*/

	@Value("${azure.table.hashmap.ecnno}")
	private String ecnNumberKey;

	@Value("${azure.table.hashmap.erp}")
	private String erpKey;

	@Value("${azure.table.hashmap.region}")
	private String regionKey;

	@Value("${azure.table.hashmap.plant}")
	private String plantKey;

	@Value("${azure.table.hashmap.transactionid}")
	private String transactionIdKey;

	@Value("${azure.table.hashmap.isprocessed}")
	private String isprocessedKey;

	@Value("${azure.table.hashmap.iserrored}")
	private String iserroredKey;

	@Value("${azure.table.hashmap.message}")
	private String messageKey;

	@Value("${azure.table.hashmap.code}")
	private String codeKey;

	@Value("${azure.table.hashmap.status}")
	private String statusKey;

	@Value("${azure.table.hashmap.processeddate}")
	private String processedDateKey;

	@Value("${azure.table.hashmap.createddate}")
	private String createdDateKey;

	@Value("${azure.table.hashmap.processby}")
	private String processedByKey;

	@Value("${azure.table.hashmap.uiprocessed}")
	private String uiProcessedKey;

	@Value("${azure.table.hashmap.isacknowledged}")
	private String isAcknowledgedKey;

	@Value("${azure.table.hashmap.description}")
	private String descriptionKey;

	@Value("${azure.table.hashmap.type}")
	private String typeKey;

	@Value("${azure.table.hashmap.createdby}")
	private String createdbyKey;

	@SuppressWarnings("unused")
	@Override
	public boolean insertPayloadEntity(HashMap<String, Object> map)
			throws InvalidKeyException, URISyntaxException, StorageException {
		LOG.info("#####Staring PLMPartBomDaoImpl.insertPayloadEntity#####");
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
		CloudTableClient tableClient = storageAccount.createCloudTableClient();
		CloudTable cloudTable = tableClient.getTableReference(plmPayloadTableName);
		CloudTable cloudMiscDataTable = tableClient.getTableReference("controlsmiscdata");//hard coded Table Name
		cloudTable.createIfNotExists();
		cloudMiscDataTable.createIfNotExists();
		PLMPayloadTableEntity plmPayloadTableEntity = new PLMPayloadTableEntity(
				plmPayloadPartitionKey + "_" + map.get(erpKey), map.get(ecnNumberKey).toString());
		plmPayloadTableEntity.setECNNumber(map.get(ecnNumberKey).toString());
		plmPayloadTableEntity.setTransactionID(map.get(transactionIdKey).toString());
		plmPayloadTableEntity.setErp(map.get(erpKey).toString());
		plmPayloadTableEntity.setRegion(map.get(regionKey).toString());
		plmPayloadTableEntity.setPlant(map.get(plantKey).toString());
		plmPayloadTableEntity.setIsProcessed(Integer.parseInt(map.get(isprocessedKey).toString()));
		plmPayloadTableEntity.setIsErrored(Integer.parseInt(map.get(iserroredKey).toString()));
		plmPayloadTableEntity.setMessage(map.get(messageKey).toString());
		plmPayloadTableEntity.setCode(Integer.parseInt(map.get(codeKey).toString()));
		plmPayloadTableEntity.setStatus(map.get(statusKey).toString());
		plmPayloadTableEntity.setProcessedDate(map.get(processedDateKey).toString());
		plmPayloadTableEntity.setCreatedDate(map.get(createdDateKey).toString());
		plmPayloadTableEntity.setProcessedBy(map.get(processedByKey).toString());
		plmPayloadTableEntity.setUIProcessed(Integer.parseInt(map.get(uiProcessedKey).toString()));
		plmPayloadTableEntity.setIsAcknowledged(Integer.parseInt(map.get(isAcknowledgedKey).toString()));
		plmPayloadTableEntity.setECNDescription(map.get(descriptionKey).toString());
		plmPayloadTableEntity.setECNType(map.get(typeKey).toString());
		plmPayloadTableEntity.setCreatedBy(map.get(createdbyKey).toString());
		TableOperation insert = TableOperation.insertOrReplace((TableEntity) plmPayloadTableEntity);
		try {
			cloudTable.execute(insert);
		} catch (Exception e) {
			LOG.error(
					"Exception while inserting payload json into azure storage tables in PLMPartBomDaoImpl.insertPayloadEntity");
			LOG.info("#####Ending PLMPartBomDaoImpl.insertPayloadEntity#####");
			return false;
		}
		MiscTableEntity miscTableEntity = new MiscTableEntity("TOTAL_COUNT",
				map.get(erpKey).toString().toUpperCase());
		//LOG.info("#####miscdataPartitionKey#####" + miscdataPartitionKey);
		LOG.info("######miscdataTableName####  " + miscdataTableName);
		
		//Added By ANand For Testing
		
		miscTableEntity.setErrorCount(1); //miscTableEntity.getErrorCount() is not catching the data hence we are setting manually
		miscTableEntity.setProcessedCount(1); //miscTableEntity.getProcessedCount() is not catching the data hence we are setting manually
		miscTableEntity.getErrorCount();
		miscTableEntity.getProcessedCount();
		
		//Added By ANand For Testing
		
		LOG.info("map.get(iserroredKey).toString() " +map.get(iserroredKey).toString());

		if (Integer.parseInt(map.get(iserroredKey).toString()) == 1) {
			LOG.info("In block 1.1");
			if (miscTableEntity.getErrorCount()<0) {
				LOG.info(" ErrorCount One########");
				miscTableEntity.setErrorCount(1);
			} else {
				LOG.info(" ErrorCount Two########");
				miscTableEntity.setErrorCount(miscTableEntity.getErrorCount() + 1);
			}
		} else  {
			LOG.info("In block 1.2");
			System.out.println("Get Processed Count  "+miscTableEntity.getProcessedCount());
			if (miscTableEntity.getProcessedCount()<0) {
			LOG.info(" ProcessedCount One########");
				miscTableEntity.setProcessedCount(1);
			} else {
				LOG.info(" ProcessedCount Two#######");
				miscTableEntity.setProcessedCount(miscTableEntity.getProcessedCount() + 1);
			}
		}
		insert = TableOperation.insertOrReplace((TableEntity) miscTableEntity);
		try {
			cloudMiscDataTable.execute(insert);
		} catch (Exception e) {
			LOG.error(
					"Exception while inserting payload json into azure storage tables in PLMPartBomDaoImpl.insertPayloadEntity");
			LOG.info("#####Ending PLMPartBomDaoImpl.insertPayloadEntity#####");
			return false;
		}
		LOG.info("#####Ending PLMPartBomDaoImpl.insertPayloadEntity#####");
		return true;
	}
}
