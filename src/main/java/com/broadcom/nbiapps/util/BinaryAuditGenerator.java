/**
 * 
 */
package com.broadcom.nbiapps.util;

import static java.util.stream.Collectors.toList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.broadcom.nbiapps.model.BuildArtifacts;
import com.broadcom.nbiapps.model.FileChanges;


/**
 * @author Balaji N
 *
 */
public class BinaryAuditGenerator extends ModulesValidator {
	private static final Logger logger = LoggerFactory.getLogger(ModulesValidator.class);
	private BuildArtifacts buildArtifacts = new BuildArtifacts();
	

	/**
	 * This model is used filter files for audit.
	 * @param basePath
	 * @param operation
	 * @param fileChangeList
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	BinaryAuditGenerator(String basePath, String taskId, List<FileChanges> fileChanges) throws FileNotFoundException, IOException, XmlPullParserException {		
		super(basePath,taskId, fileChanges);
		
		List<String> addedPoms = 
		for(fileChanges) {
			
		}
		List<String> addedPomFiles = fileChanges.stream().filter(s-> (s.getOperation().equals("A")) && s.getChangeList().contains("pom.xml")).collect(toList());
		
		
	}
	

}
