package com.adp.restservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// Request model class
public class ChangeRequest {
	@JsonProperty("billAmount")
	private String billAmount;
	@JsonProperty("paidAmount")
	private String paidAmount;
	@JsonProperty("maximizeCoins")
	private boolean maximizeCoins = Boolean.FALSE;

	public ChangeRequest(String billAmount, String paidAmount, boolean maximizeCoins) {
		this.billAmount = billAmount;
		this.paidAmount = paidAmount;
		this.maximizeCoins = maximizeCoins;
	}

	public ChangeRequest() {

	}

	public String getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(String billAmount) {
		this.billAmount = billAmount;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public boolean isMaximizeCoins() {
		return maximizeCoins;
	}

	public void setMaximizeCoins(boolean maximizeCoins) {
		this.maximizeCoins = maximizeCoins;
	}

}
