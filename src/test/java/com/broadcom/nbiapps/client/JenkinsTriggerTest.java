/**
 * 
 */
package com.broadcom.nbiapps.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Balaji N
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class JenkinsTriggerTest {

	private static final Logger logger = LoggerFactory.getLogger(JenkinsTriggerTest.class);

	@Autowired
	TriggerBuild triggerBuild;

	@Test
	public void triggerBuild() throws Exception {
		ResponseEntity<String> response = triggerBuild.callJenkin();
		if (response.getStatusCode().equals(HttpStatus.CREATED)) {
			logger.info("Jenkins triggerred Successfully");
		} else {
			logger.error("Jenkins triggerred failed - statusCode: " + response.getStatusCode() + " , responseBody: " + response.getBody());
		}
		Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

	}
}
