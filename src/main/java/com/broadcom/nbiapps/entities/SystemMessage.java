package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;


/**
 * The persistent class for the system_messages database table.
 * 
 */
@Entity
@Table(name="system_messages")
@NamedQuery(name="SystemMessage.findAll", query="SELECT s FROM SystemMessage s")
public class SystemMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="code_id")
	private BigInteger codeId;

	private Long code;

	private String description;

	private String type;

	public SystemMessage() {
	}

	public BigInteger getCodeId() {
		return this.codeId;
	}

	public void setCodeId(BigInteger codeId) {
		this.codeId = codeId;
	}

	public Long getCode() {
		return this.code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}