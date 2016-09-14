package com.jci.partbom.dao;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import com.microsoft.azure.storage.StorageException;

public interface PLMPartBomDao {

	boolean insertPayloadEntity(HashMap<String, Object> map)
			throws InvalidKeyException, URISyntaxException, StorageException;

}
