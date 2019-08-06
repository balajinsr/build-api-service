package com.broadcom.nbiapps.model;

public class ResponseBuilder {
	private boolean result;
	// idea is to have this field populated when more information with regards to a the boolean result is required 
	private String resultDesc;
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getResultDesc() {
		return resultDesc;
	}
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}
	public ResponseBuilder(boolean result, String resultDesc) {
		super();
		this.result = result;
		this.resultDesc = resultDesc;
	}
	@Override
	public String toString() {
		return "ResponseBuilder [result=" + result + ", resultDesc=" + resultDesc + "]";
	}
}
