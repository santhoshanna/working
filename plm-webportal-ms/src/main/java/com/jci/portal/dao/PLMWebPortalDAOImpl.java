package com.jci.portal.dao;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import com.jci.portal.domain.MiscTableEntity;
import com.jci.portal.domain.PLMDetailsResponse;
import com.jci.portal.domain.PLMFailureData;
import com.jci.portal.domain.PLMGraphData;
import com.jci.portal.domain.PLMPayloadTableEntity;
import com.jci.portal.domain.PLMRequestParam;
import com.jci.portal.domain.PLMSucessData;
import com.jci.portal.domain.PLMUserTableEntity;
import com.jci.portal.domain.Pagination;
import com.jci.portal.domain.SymixData;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobEncryptionPolicy;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableQuery.Operators;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;

@Configuration
@Repository
public class PLMWebPortalDAOImpl implements PLMWebPortalDAO{
	
	@Value("${azure.storage.connectionstring}")
	private String connectionString;
	
	@Value("${azure.miscdata.table}")
	private String miscTable;
	
	@Value("${azure.miscdata.partitionkey}")
	private String miscPartitionKey;
	
	@Value("${azure.miscdata.rowkey}")
	private String miscRowKey;
	
	@Value("${azure.column.rowkey}")
	private String rowKeyColumnName;
	
	@Value("${azure.column.partitionkey}")
	private String partitionKeyColumnName;
	
	@Value("${azure.controlsplmpayloadtabledata.table}")
	private String plmdataTable;
	
	@Value("${azure.controlsplmpayloadtabledata.partitionkey}")
	private String plmdataPartitionKey;
	
	@Value("${azure.userrolemaptable.table}")
	private String userTable;
	
	@Value("${azure.userrolemaptable.partitionkey}")
	private String userPartitionKey;
	
	@Value("${azure.userrolemaptable.rowkey}")
	private String userRowKey;
	
	@Value("${azure.storage.blobname}")
	private String blobName;
	
	

	@Override
	public MiscTableEntity getMiscData() {
		MiscTableEntity miscTableEntity = null;
		try
		{
		    CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
		    CloudTableClient tableClient = storageAccount.createCloudTableClient();
		    CloudTable cloudTable = tableClient.getTableReference(miscTable);
		    TableOperation retrieveMiscData = TableOperation.retrieve(miscPartitionKey, miscRowKey, MiscTableEntity.class);
		    miscTableEntity = cloudTable.execute(retrieveMiscData).getResultAsType();
		}catch (Exception e){
		    e.printStackTrace();
		}
		return miscTableEntity;
	}
	
	@Override
	public PLMUserTableEntity getUserData() {
		PLMUserTableEntity userTableEntity = null;
		try
		{
		    CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
		    CloudTableClient tableClient = storageAccount.createCloudTableClient();
		    CloudTable cloudTable = tableClient.getTableReference(userTable);
		    TableOperation retrieveUserData = TableOperation.retrieve(userPartitionKey, userRowKey, PLMUserTableEntity.class);
		    userTableEntity = cloudTable.execute(retrieveUserData).getResultAsType();
		}catch (Exception e){
		    e.printStackTrace();
		}
		return userTableEntity;
	}
	
	@Override
	public boolean updatePayloadProcess(PLMPayloadTableEntity payloadTableEntity){
		boolean isPLMPayloadUpdated = false;
		try
		{
		    CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
		    CloudTableClient tableClient = storageAccount.createCloudTableClient();
		    CloudTable cloudTable = tableClient.getTableReference(plmdataTable);
		    TableOperation retrievePLMPayloadTable = TableOperation.retrieve(payloadTableEntity.getPartitionKey(), payloadTableEntity.getRowKey(), PLMPayloadTableEntity.class);
		    TableOperation replaceEntity = TableOperation.replace(payloadTableEntity);
		    cloudTable.execute(replaceEntity);
		    isPLMPayloadUpdated = true;
		}catch (Exception e){
		    e.printStackTrace();
		}
		return isPLMPayloadUpdated;
	}
	
	public PLMFailureData getReprocessingData(PLMRequestParam reqParam){
		
		PLMFailureData failureData = new PLMFailureData();
		Pagination pagination = new Pagination();
		List<PLMPayloadTableEntity> failurePayload = new ArrayList<PLMPayloadTableEntity>();
		
		try{
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
			CloudTableClient tableClient = storageAccount.createCloudTableClient();
			CloudTable cloudTable = tableClient.getTableReference(plmdataTable);
			String partitionFilter = TableQuery.generateFilterCondition(partitionKeyColumnName,QueryComparisons.EQUAL,plmdataPartitionKey);
			String isErroredFilter = TableQuery.generateFilterCondition("IsErrored", QueryComparisons.EQUAL, 1);
			String combinedFilter = TableQuery.combineFilters(partitionFilter, Operators.AND, isErroredFilter);
			TableQuery<PLMPayloadTableEntity> partitionQuery = TableQuery.from(PLMPayloadTableEntity.class).where(combinedFilter).take(reqParam.getSize());
			for(PLMPayloadTableEntity entity : cloudTable.execute(partitionQuery)){
				if((entity.getIsErrored() == 1) && (entity.getIsProcessed() == 1)){
					failurePayload.add(entity);
				}
			}
			
			SymixData symixData = new SymixData();
			symixData.setSeries(failurePayload);
			symixData.setPagination(pagination);
			failureData.setSymix(symixData);
		}catch(Exception e){
			e.printStackTrace();
		}
		return failureData;
	}
	
	@Override
	public PLMDetailsResponse getPayloadDetails(PLMRequestParam reqParam){
		PLMDetailsResponse plmDetailsResponse = new PLMDetailsResponse();
		List<PLMPayloadTableEntity> successPayload = new ArrayList<PLMPayloadTableEntity>();
		List<PLMPayloadTableEntity> failurePayload = new ArrayList<PLMPayloadTableEntity>();
		Pagination pagination = new Pagination();
		//user data
		PLMUserTableEntity userData = getUserData();
		plmDetailsResponse.setUserData(userData);
		try{
			plmDetailsResponse.setMessage("ok");
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
			CloudTableClient tableClient = storageAccount.createCloudTableClient();
			CloudTable cloudTable = tableClient.getTableReference(plmdataTable);
			String partitionFilter = TableQuery.generateFilterCondition(partitionKeyColumnName,QueryComparisons.EQUAL,plmdataPartitionKey);
			//String isErroredFilter = TableQuery.generateFilterCondition("RowKey", QueryComparisons.GREATER_THAN, reqParam.getPaginationParam().getNextRow());
			//String combinedFilter = TableQuery.combineFilters(partitionFilter, Operators.AND, isErroredFilter);
			TableQuery<PLMPayloadTableEntity> partitionQuery = TableQuery.from(PLMPayloadTableEntity.class).where(partitionFilter);
			for(PLMPayloadTableEntity entity : cloudTable.execute(partitionQuery)){
				if((entity.getIsErrored() == 1) && (entity.getIsProcessed() == 1)){
					failurePayload.add(entity);
				}
				successPayload.add(entity);
			}
			//success data
			PLMSucessData plmSucessData = new PLMSucessData();
			SymixData symixData = new SymixData();
			symixData.setSeries(successPayload);
			symixData.setPagination(pagination);
			plmSucessData.setSymix(symixData);
			plmDetailsResponse.setResultSet(plmSucessData);
			
			//failed data
			PLMFailureData plmFailureData = new PLMFailureData();
			SymixData symixData2 = new SymixData();
			symixData2.setSeries(failurePayload);
			symixData2.setPagination(pagination);
			plmFailureData.setSymix(symixData2);
			plmDetailsResponse.setErrorData(plmFailureData);
			
			plmDetailsResponse.getErrorData().getSymix().setSeries(failurePayload);
			plmDetailsResponse.setError(false);
			MiscTableEntity miscTableEntity = getMiscData();
			int processedCount = miscTableEntity.getProcessedCount();
			int errorCount = miscTableEntity.getErrorCount();
			
			//graph data
			PLMGraphData plmGraphData = new PLMGraphData();
			List<Integer> graphErpData = new ArrayList<Integer>();
			graphErpData.add(processedCount);
			graphErpData.add(errorCount);
			plmGraphData.setSymix(graphErpData);
			plmDetailsResponse.setGraphData(plmGraphData);
		}catch(Exception e){
			plmDetailsResponse.setMessage("error while fetching");
			plmDetailsResponse.setError(true);
			e.printStackTrace();
		}
		return plmDetailsResponse;
	}

	@SuppressWarnings("null")
	@Override
	public String readBlobXML(String ecnNo) {
		String xmlOutput = "";
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			// new CloudBlobClient(baseURL,
			// new StorageCredentialsAccountAndKey(myAccount, myKey));
			CloudBlobContainer blobContainer = blobClient.getContainerReference(blobName);
			// blobContainer.createIfNotExists();
			boolean tableExistsOrNOt = true;
			if (blobContainer == null) {
				tableExistsOrNOt = blobContainer.createIfNotExists();
			}
			if (tableExistsOrNOt) {
				// String ecnURL = blobProtocol + blobProtocolSeperator +
				// storageAccountName + blobURLDotSeperator
				// + blobURLConstructor + blobURLHashSeperator + blobName +
				// blobURLHashSeperator + ecnNo;
				//String ecnURL = blobBaseURL + ecnNo;

				//LOG.info("URL for downloading blob: " + ecnURL);
				// CloudBlockBlob blob =
				// blobContainer.getBlockBlobReference(ecnURL.trim());
				CloudBlockBlob blob = blobContainer.getBlockBlobReference(ecnNo);

				// Blob Encryption Logic
				 //final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
				// RSA/ECB/OAEPWithSHA1AndMGF1Padding
				 //keyGen.initialize(1024);
				 //final KeyPair wrapKey = keyGen.generateKeyPair();
				 //RsaKey rsaKey = new RsaKey(encryptionKey, wrapKey);

				 //RsaKey rsaKey = new RsaKey(encryptionKey, null);
				 //BlobEncryptionPolicy policy = new
				 //BlobEncryptionPolicy(rsaKey, null);
				 //BlobRequestOptions options = new BlobRequestOptions();
				 //options.setEncryptionPolicy(policy);

				try {
					// Download and decrypt the encrypted contents from the
					 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					 blob.download(outputStream);
					 //System.out.println("blob string: "+blob.downloadText(StandardCharsets.UTF_8.toString(), null, options, null));
					 xmlOutput = blob.downloadText();
					// blob.download(outputStream, null, options, null);
					blob.downloadToFile("output.xml");
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlOutput;
	}

	public static String base64Decode(String token) {
		byte[] decodedBytes = Base64.decode(token.getBytes());
		return new String(decodedBytes, Charset.forName("UTF-8"));
	}
	
}
