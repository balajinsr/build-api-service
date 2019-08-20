/**
 * 
 */
package com.broadcom.nbiapps.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.broadcom.nbiapps.model.BuildArtifacts;
import com.broadcom.nbiapps.model.FileChanges;
import com.broadcom.nbiapps.model.ModuleData;


/**
 * @author Balaji N
 *
 */
public class BinaryAuditGenerator extends ModulesValidator {
	private static final Logger logger = LoggerFactory.getLogger(BinaryAuditGenerator.class);
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
		
		
		//TODO: populate buildArtifactos for audit.
		if(isRootPomAdded()) {
			
		} else if(isRootPomModified()) {
			
		}
		
		Set<String> moduleNameKeys = getUniqueAddedOrModifiedModulesMap().keySet();
		// changes on module level
		for(String moduleNameDir : moduleNameKeys) {
			ModuleData moduleData = getUniqueAddedOrModifiedModulesMap().get(moduleNameDir);
			if(DELETED.equals(moduleData.getModuleAction())) {
				buildArtifacts.getModuleData().add(moduleData);
			}

			if(ADDED.equals(moduleData.getModuleAction())) {
				
			} else if(MODIFIED.equals(moduleData.getModuleAction())) {
				if(ADDED.equals(moduleData.getPomAction())) {
					
				} else if(MODIFIED.equals(moduleData.getPomAction())) {
					
				}
				if(moduleData.isSourceChanged()) {
					
				}
			}
		}
		
		// impacted modules because of root pom changed. loop through the impacted module for prepare dependency jars list{added or deleted} with md5 value
		
	}
	

}
