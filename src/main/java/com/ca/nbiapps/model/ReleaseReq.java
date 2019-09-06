/**
 * 
 */
package com.ca.nbiapps.model;

import java.math.BigInteger;
import java.util.List;

import com.ca.nbiapps.entities.BuildAuditReq;

/**
 * @author Balaji N
 *
 */
public class ReleaseReq extends BuildAuditReq {
	private BigInteger cycleId;
	private List<String> taskIdList;
	
	public BigInteger getCycleId() {
		return cycleId;
	}
	public void setCycleId(BigInteger cycleId) {
		this.cycleId = cycleId;
	}
	public List<String> getTaskIdList() {
		return taskIdList;
	}
	public void setTaskIdList(List<String> taskIdList) {
		this.taskIdList = taskIdList;
	}
}
