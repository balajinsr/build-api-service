package com.ca.nbiapps.model;

public class Repository {
	private String html_url;
	private String ssh_url;
	private String name;
	private String url;
	
	public String getHtml_url() {
		return html_url;
	}
	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSsh_url() {
		return ssh_url;
	}
	public void setSsh_url(String ssh_url) {
		this.ssh_url = ssh_url;
	}
}
