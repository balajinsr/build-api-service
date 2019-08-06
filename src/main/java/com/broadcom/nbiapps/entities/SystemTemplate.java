package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the system_templates database table.
 * 
 */
@Entity
@Table(name="system_templates")
@NamedQuery(name="SystemTemplate.findAll", query="SELECT s FROM SystemTemplate s")
public class SystemTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="template_id")
	private Long templateId;

	@Lob
	private String body;

	private String enable;

	private String name;

	private String subject;

	public SystemTemplate() {
	}

	public Long getTemplateId() {
		return this.templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getEnable() {
		return this.enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}