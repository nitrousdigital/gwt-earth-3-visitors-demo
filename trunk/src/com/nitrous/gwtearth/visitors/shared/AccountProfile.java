package com.nitrous.gwtearth.visitors.shared;

import java.io.Serializable;

/**
 * A bean that represents a single google analytics account profile
 * @author Nick
 *
 */
public class AccountProfile implements Serializable {
	private static final long serialVersionUID = 961899497046383701L;
	private String accountName;
	private String profileName;
	private String profileId;
	private String tableId;
	public AccountProfile() {
	}
	public AccountProfile(String accountName, String profileName,
			String profileId, String tableId) {
		super();
		this.accountName = accountName;
		this.profileName = profileName;
		this.profileId = profileId;
		this.tableId = tableId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getProfileId() {
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	public String getTableId() {
		return tableId;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	
}
