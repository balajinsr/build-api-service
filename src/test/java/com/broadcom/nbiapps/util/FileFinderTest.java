/**
 * 
 */
package com.broadcom.nbiapps.util;

import java.io.IOException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author Balaji N
 *
 */

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FileFinderTest {

	private static final Logger logger = LoggerFactory.getLogger(FileFinderTest.class);
	private static final String path="..";
	@Test
	public void duplicationFileFinderTest() {
		FileFinder finder = new FileFinder(path,"duplication.txt");
		try {
			List<String> duplicateFileList = finder.getResults(true);
			logger.info("Duplication File List: "+duplicateFileList.toString());
			Assertions.assertThat(duplicateFileList.size() > 0);
		} catch (IOException e) {
			logger.error("Error: "+e);
		}
	}
	
	@Test
	public void ignoreFilesFinderTest() {
		FileFinder finder = new FileFinder(path,"duplication.txt","target");
		try {
			List<String> duplicateFileList = finder.getResults(true);
			logger.info("Duplication File List with ignored dir: target: "+duplicateFileList.toString());
			Assertions.assertThat(duplicateFileList.size() > 0);
		} catch (IOException e) {
			logger.error("Error: "+e);
		}
	}
	
	
}
