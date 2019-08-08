/**
 * 
 */
package com.broadcom.nbiapps.client;


import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
/**
 * @author Balaji N
 *
 */
@Component
public class TriggerBuild {
	private static final Logger logger = LoggerFactory.getLogger(TriggerBuild.class);
	
	@Value("${spring.jenkins.url}")
	public String jenkinsURL;
	
	@Value("${spring.jenkins.username}")
	public String jenkinsAuthenticationUserName;
	
	@Value("${spring.jenkins.token}")
	public String jenkinsAuthenticationPassword;

	public ResponseEntity<String> jenkinsRemoteAPIBuildWithParameters(String jobName, MultiValueMap<String, String> paramsMap) {	
		ResponseEntity<String> response = null;
		try {
			String jenkinWithParamURL = jenkinsURL.concat("/jenkins/job/").concat(jobName).concat("/buildWithParameters");
			URL url = new URL(jenkinWithParamURL);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setBasicAuth(jenkinsAuthenticationUserName, jenkinsAuthenticationPassword);
			RestTemplate restTemplate = null;
			logger.info("Jenkins URL: "+url.toString());
			logger.info("PayLoad: "+paramsMap.toString());
			
			if("https".equals(url.getProtocol())) {
				restTemplate = new RestTemplate(getClientHttpRequestFactory());
			} else {
				restTemplate = new RestTemplate();
			}
	
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(paramsMap, headers);
			response = restTemplate.postForEntity(url.toString(), request , String.class);
			logger.info("Response status : "+response.getStatusCode());
		} catch(IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException ex) {
			logger.error("Error: "+ex, ex);
			throw new RuntimeException("Error while calling RemoteAPIBuildWithParameters", ex);
		}
		return response;
	}
	
	public HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException  {
		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
	        @Override
	        public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
	            return true;
	        }
	    };
	    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
	    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
		
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return requestFactory;
	}
	
	public ResponseEntity<String> callJenkin() throws Exception {
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("SILO_NAME", "first.last@example.com");
		jenkinsURL="https://lod-paysec01.lvn.broadcom.net";
		jenkinsAuthenticationUserName = "nbi-app-build";
		jenkinsAuthenticationPassword = "71b366a61be94c74f381a6179c306811";//114b3c900029eb98bc3a80b5628340ec4f";
		return jenkinsRemoteAPIBuildWithParameters("Test", map);
	}

	public static void main(String[] args) throws Exception {
		new TriggerBuild().callJenkin();
	}
}
