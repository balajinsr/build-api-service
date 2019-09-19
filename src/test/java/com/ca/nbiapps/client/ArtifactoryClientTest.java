package com.ca.nbiapps.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


/**
 * @author Balaji N
 */
		
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ArtifactoryClientTest {
	
	@Autowired
	ArtifactoryClient artifactoryClient;
	
	@Test
	public void connectArtifactoryTest() {
		artifactoryClient.createArtifactory();
		artifactoryClient.printArtifactoryInstance();
		artifactoryClient.uploadFile("C:\\work-ca\\spring-boot-rest-example-master.zip","test/spring-boot-rest-example-master.zip");
	}
}
