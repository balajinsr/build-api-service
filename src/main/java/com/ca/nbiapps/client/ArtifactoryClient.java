/**
 * 
 */
package com.ca.nbiapps.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Balaji N
 *
 */
public class ArtifactoryClient {
	
	private static final Logger logger = LoggerFactory.getLogger(ArtifactoryClient.class);
	
	private String artifactoryURL;
	private String userName;
	private String password;
	
	
	
	public String getArtifactoryURL() {
		return artifactoryURL;
	}
	public void setArtifactoryURL(String artifactoryURL) {
		this.artifactoryURL = artifactoryURL;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
