package com.jci.subscriber.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;

import com.microsoft.azure.keyvault.extensions.RsaKey;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobEncryptionPolicy;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

@Service
@Configuration
public class PLMSubscriberMSDaoImpl implements PLMSubscriberMSDao {

	private static final Logger LOG = LoggerFactory.getLogger(PLMSubscriberMSDaoImpl.class);

	@Value("${azure.storage.connectionstring}")
	private String connectionString;

	@Value("${azure.storage.blobname}")
	private String blobName;

	@Value("${hashmap.key.ecnnumber}")
	private String ecnNumberKey;

	@Value("${hashmap.key.xml}")
	private String xmlKey;

	@Value("${azure.storage.blob.encryption.type}")
	private String encryptionType;

	@Value("${azure.storage.blob.encryption.key}")
	private String encryptionKey;

	@Value("${azure.storage.blob.download.url.protocol}")
	private String blobProtocol;

	@Value("${azure.storage.blob.download.url.constructor}")
	private String blobURLConstructor;

	@Value("${azure.storage.accountname}")
	private String storageAccountName;

	@Value("${azure.storage.blob.download.url.protocolseperator}")
	private String blobProtocolSeperator;

	@Value("${azure.storage.blob.download.url.hashseperator}")
	private String blobURLHashSeperator;

	@Value("${azure.storage.blob.download.url.dotseperator}")
	private String blobURLDotSeperator;

	@Value("${azure.storage.blob.download.baseurl}")
	private String blobBaseURL;

	@SuppressWarnings("null")
	@Override
	public boolean insertPayloadXMLToBlob(HashMap<String, Object> xml) {
		LOG.info("#####Staring PLMSubscriberMSDaoImpl.insertPayloadXMLToBlob#####");
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			CloudBlobContainer blobContainer = blobClient.getContainerReference(blobName);
			// blobContainer.createIfNotExists();
			boolean tableExistsOrNOt = true;
			if (blobContainer == null) {
				tableExistsOrNOt = blobContainer.createIfNotExists();
			}
			if (tableExistsOrNOt) {
				CloudBlockBlob blob = blobContainer.getBlockBlobReference(xml.get(ecnNumberKey).toString());
				// Blob Encryption Logic
				// final KeyPairGenerator keyGen =
				// KeyPairGenerator.getInstance(encryptionType);
				// keyGen.initialize(1024);
				// final KeyPair wrapKey = keyGen.generateKeyPair();
				// RsaKey rsaKey = new RsaKey(encryptionKey, wrapKey);
				// RsaKey rsaKey = new RsaKey(encryptionKey, null);
				// BlobEncryptionPolicy policy = new
				// BlobEncryptionPolicy(rsaKey, null);
				// BlobRequestOptions options = new BlobRequestOptions();
				// options.setEncryptionPolicy(policy);

				try {
					InputStream inputStream = new ByteArrayInputStream(
							xml.get(xmlKey).toString().getBytes(StandardCharsets.UTF_8));
					blob.upload(inputStream, inputStream.available());
					// blob.upload(inputStream, inputStream.available(), null,
					// options, null);
				} catch (Exception e) {
					LOG.error("Exception while inserting xml to blob in PLMSubscriberMSDaoImpl.insertPayloadXMLToBlob",
							e);
					LOG.info("#####Ending PLMSubscriberMSDaoImpl.insertPayloadXMLToBlob#####");
					return false;
				}
			}
		} catch (Exception e) {
			LOG.error("Exception while writing xml to blob in PLMSubscriberMSDaoImpl.insertPayloadXMLToBlob", e);
			LOG.info("#####Ending PLMSubscriberMSDaoImpl.insertPayloadXMLToBlob#####");
			return false;
		}
		LOG.info("#####Ending PLMSubscriberMSDaoImpl.insertPayloadXMLToBlob#####");
		return true;
	}

	@SuppressWarnings("null")
	@Override
	public boolean readBlobXML(String ecnNo) {
		LOG.info("#####Staring PLMSubscriberMSDaoImpl.readBlobXML#####");
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
				// final KeyPairGenerator keyGen =
				// KeyPairGenerator.getInstance("RSA");
				// RSA/ECB/OAEPWithSHA1AndMGF1Padding
				// keyGen.initialize(1024);
				// final KeyPair wrapKey = keyGen.generateKeyPair();
				// RsaKey rsaKey = new RsaKey(encryptionKey, wrapKey);

				// RsaKey rsaKey = new RsaKey(encryptionKey, null);
				// BlobEncryptionPolicy policy = new
				// BlobEncryptionPolicy(rsaKey, null);
				// BlobRequestOptions options = new BlobRequestOptions();
				// options.setEncryptionPolicy(policy);

				try {
					// Download and decrypt the encrypted contents from the
					// ByteArrayOutputStream outputStream = new
					// ByteArrayOutputStream();
					// blob.download(outputStream);
					// LOG.info("blob string: "
					// + blob.downloadText(StandardCharsets.UTF_8.toString(),
					// null, options, null));
					// blob.download(outputStream, null, options, null);
					blob.downloadToFile("output.xml");
					LOG.info("Downloading done!");
				} catch (Exception e) {
					LOG.error("Exception while downloading xml from blob in PLMSubscriberMSDaoImpl.readBlobXML", e);
					LOG.info("#####Ending PLMSubscriberMSDaoImpl.readBlobXML#####");
					return false;
				}
			}
		} catch (Exception e) {
			LOG.error("Exception while writing xml to blob in PLMSubscriberMSDaoImpl.insertPayloadXMLToBlob", e);
			LOG.info("#####Ending PLMSubscriberMSDaoImpl.insertPayloadXMLToBlob#####");
			return false;
		}
		LOG.info("#####Ending PLMSubscriberMSDaoImpl.insertPayloadXMLToBlob#####");
		return true;
	}

	public static String base64Encode(String token) {
		byte[] encodedBytes = Base64.encode(token.getBytes());
		return new String(encodedBytes, Charset.forName("UTF-8"));
	}

	public static String base64Decode(String token) {
		byte[] decodedBytes = Base64.decode(token.getBytes());
		return new String(decodedBytes, Charset.forName("UTF-8"));
	}

}
