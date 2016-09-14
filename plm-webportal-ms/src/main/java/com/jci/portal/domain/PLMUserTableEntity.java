package com.jci.portal.domain;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class PLMUserTableEntity extends TableServiceEntity {

	private String EmailID;
	private Integer PLMAccess;
	private String Role;
	private Integer SCAccess;
	
	public PLMUserTableEntity(){
	}
	
	public PLMUserTableEntity(String partitionKey, String rowKey) {
		this.partitionKey = partitionKey;
		this.rowKey = rowKey;
	}
	
	public String getEmailID() {
		return EmailID;
	}
	public void setEmailID(String emailID) {
		EmailID = emailID;
	}
	public String getRole() {
		return Role;
	}
	public void setRole(String role) {
		Role = role;
	}
	public Integer getPLMAccess() {
		return PLMAccess;
	}
	public void setPLMAccess(Integer pLMAccess) {
		PLMAccess = pLMAccess;
	}
	public Integer getSCAccess() {
		return SCAccess;
	}
	public void setSCAccess(Integer sCAccess) {
		SCAccess = sCAccess;
	}
}
