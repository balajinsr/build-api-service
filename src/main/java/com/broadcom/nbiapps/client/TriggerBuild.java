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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
/**
 * @author Balaji N
 *
 */

public class TriggerBuild {
	private static final Logger logger = LoggerFactory.getLogger(TriggerBuild.class);
	
	@Value("${jenkins.url}")
	private String jenkinsURL;
	
	@Value("${jenkins.userName}")
	private String jenkinsAuthenticationUserName;
	
	@Value("${jenkins.password}")
	private String jenkinsAuthenticationPassword;

	public void jenkinsRemoteAPIBuildWithParameters(String jobName, MultiValueMap<String, String> paramsMap) throws  IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {	
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
		ResponseEntity<String> response = restTemplate.postForEntity(url.toString(), request , String.class);
		if(response.getStatusCode().equals(HttpStatus.CREATED)) {
			logger.info("Jenkins triggerred Successfully");
		} else {
			logger.error("Jenkins triggerred failed - statusCode: "+response.getStatusCode()+" , responseBody: "+response.getBody());
		}
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
	
	public void callJenkin() throws Exception {
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("Test", "first.last@example.com");
		jenkinsURL="https://datso03-l26497.lvn.broadcom.net";
		jenkinsAuthenticationUserName = "causer";
		jenkinsAuthenticationPassword = "114b3c900029eb98bc3a80b5628340ec4f";
		jenkinsRemoteAPIBuildWithParameters("test", map);
	}

	public static void main(String[] args) throws Exception {
		new TriggerBuild().callJenkin();
	}
}
