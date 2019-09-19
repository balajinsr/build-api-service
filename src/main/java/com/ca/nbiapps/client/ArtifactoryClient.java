package com.ca.nbiapps.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Balaji N
 */
@Service
public class ArtifactoryClient {
	
	@Value("${spring.artifactory.username}")
	private String artifactoryUserName;
	
	@Value("${spring.artifactory.apiKey}")
	private String artifactoryAPIKey;
	
	@Value("${spring.artifactory.url}")
	private String artifactoryURL;
	
	@Value("${spring.artifactory.readtimeout}")
	private String readtimeout;
	
	@Value("${spring.artifactory.connectiontimeout}")
	private String connectionTimeOut;
	
	@Value("${spring.artifactory.mavenrepo}")
	private String mavenrepo;
	
	private Artifactory artifactory;
	

	public void createArtifactory() {
        if (StringUtils.isEmpty(artifactoryUserName) || StringUtils.isEmpty(artifactoryAPIKey) || StringUtils.isEmpty(artifactoryURL)){
            throw new IllegalArgumentException("Arguments passed to createArtifactory are not valid");
        }
         this.artifactory = ArtifactoryClientBuilder.create()
                .setUrl(artifactoryURL)
                .setUsername(artifactoryUserName)
                .setAccessToken(artifactoryAPIKey)
                .setSocketTimeout(Integer.valueOf(readtimeout))
                .setConnectionTimeout(Integer.valueOf(connectionTimeOut))
                .build();
    }
	
	public void uploadFile(String uploadFilePath, String uploadFileName) {
		File file = new File(uploadFileName);
		org.jfrog.artifactory.client.model.File result = artifactory.repository(mavenrepo).upload(uploadFilePath, file).doUpload();
	}
	
	
	public java.io.File downloadFile(String filePath, String downloadLocation) throws IOException {
		if (artifactory == null || StringUtils.isEmpty(mavenrepo) || StringUtils.isEmpty(filePath)) {
			throw new IllegalArgumentException("Arguments passed to downloadFile are not valid");
		}
		java.io.File targetFile = null;
		try (InputStream inputStream = artifactory.repository(mavenrepo).download(filePath).doDownload()) {
			targetFile = new java.io.File(downloadLocation);
			FileUtils.copyInputStreamToFile(inputStream, targetFile);
		}
		return targetFile;
	}
	
	public void printArtifactoryInstance() {
		System.out.println(artifactory);
	}
}
