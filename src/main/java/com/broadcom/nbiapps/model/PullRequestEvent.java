package com.broadcom.nbiapps.model;

import java.math.BigInteger;

public class PullRequestEvent {
	private String action;
	private BigInteger number;
	private PullRequest pull_request;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public PullRequest getPull_request() {
		return pull_request;
	}

	public void setPull_request(PullRequest pull_request) {
		this.pull_request = pull_request;
	}

	public BigInteger getNumber() {
		return number;
	}

	public void setNumber(BigInteger number) {
		this.number = number;
	}
}
