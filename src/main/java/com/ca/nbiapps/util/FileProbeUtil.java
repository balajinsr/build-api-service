package com.ca.nbiapps.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.util.StringUtils;

import com.ca.nbiapps.business.layer.PomContainer;
import com.ca.nbiapps.model.ResponseBuilder;

public class FileProbeUtil {
	
	private String basePath;
	
	public FileProbeUtil(String basePath) {
		this.basePath=basePath;
	}
	
	/**
	 * Checks whether the extensions are in list. 
	 * @param pullContentFiles : list of files with absolute file paths
	 * @return
	 * @throws IOException
	 */
	public ResponseBuilder areFileExtensionsCorrect(String extensionsAllowed, List<String> relativeFilePaths) throws IOException{
		if (relativeFilePaths == null || relativeFilePaths.isEmpty()){
			return sendResult(true,"No files present");
		} else{
			for (String relativeFilePath : relativeFilePaths){
				// skip if its a directory
				String absFilePath = basePath+File.separator+relativeFilePath;
				if (!(new File(absFilePath)).isDirectory()) {
					if(StringUtils.containsWhitespace(absFilePath) ) {
						return sendResult(false,"File Name should not contains space. [ "+absFilePath+" ]. NOTE: Not in use files should be removed.");
					} else if(!extensionsAllowed.contains(getExtension(absFilePath))){
						return sendResult(false,"Extension used in [ "+absFilePath+" ] is not in Allowed List { "+extensionsAllowed+" }");
					}
				}
			}
		}
		return sendResult(true);
	}
	
	/**
	 * Checks whether the extensions are in list. 
	 * @param pullContentFiles : list of files with absolute file paths
	 * @return
	 * @throws IOException
	 */
	public ResponseBuilder areFileExtensionsCorrect(String extensionsAllowed, String relativeFilePath) throws IOException{
		if (relativeFilePath == null){
			return sendResult(true,"No files present");
		} else{
			
			// skip if its a directory
			String absFilePath = basePath+File.separator+relativeFilePath;
			if (!(new File(absFilePath)).isDirectory()) {
				if(StringUtils.containsWhitespace(absFilePath) ) {
					return sendResult(false,"File Name should not contains space. [ "+absFilePath+" ]. NOTE: Not in use files should be removed.");
				} else if(!extensionsAllowed.contains(getExtension(absFilePath))){
					return sendResult(false,"Extension used in [ "+absFilePath+" ] is not in Allowed List { "+extensionsAllowed+" }");
				}
			}
			
		}
		return sendResult(true);
	}
	

	
	/**
	 * Checks the correctness of a POM
	 * @param fileName : absolute file path
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public ResponseBuilder isPomCorrect(String relativeFilePath) throws FileNotFoundException, IOException {
		String absfileName = basePath+File.separator+relativeFilePath;
		try {
			PomContainer pomContainer = new PomContainer(absfileName);
			ResponseBuilder responseBuilder = pomContainer.checkFinalName();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isPomVersionCorrect();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isArtifactIdNomenclatureCorrect();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isReleaseVersion();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isDependencyNomenclatureCorrect();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isDependencyVersioningNomenclatureCorrect();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isDependencyScopeNomenclatureCorrect();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isDuplicateDependencyPresent();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isPluginNomenclatureCorrect();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isRepositoriesNomenclatureCorrect();
			if (!responseBuilder.isResult())
				return responseBuilder;
			responseBuilder = pomContainer.isDependencyPluginPresent();
			if (!responseBuilder.isResult())
				return responseBuilder;
			return responseBuilder;
		} catch(XmlPullParserException xpp) {
			return new ResponseBuilder(false, "{File Path = "+absfileName+"}, Syntax Error: "+xpp.getMessage());
		}
	}
	
	/**
	 * Handling for Java files only. Will look at properties as we go ahead
	 * @param fileName : Name of the file without the path
	 * @param lookUpPaths
	 * @return
	 * @throws IOException
	 */
	public ResponseBuilder isFileUnique(String fileName, String fileType, String... lookUpPaths) throws IOException{
		List<String> matchedFiles = new ArrayList<>();
		Set<String> uniqueFileSet = new HashSet<>();
		for (String lookUpPath:lookUpPaths){
			if (fileName.endsWith(fileType)){
				matchedFiles.addAll((new FileFinder(lookUpPath, fileName, ".git,target,test,sql,SILO")).getResults(true));
			 //} else if (fileName.endsWith("properties")){
			 //	((new FileFinder(lookUpPath, fileName, ".git,target,test,java,acspage"))).getResults(false).addAll(matchedFiles);
			}else{
				// @TODO - Sandip - handle this
			}
		}
		
		if (matchedFiles.size() > 1){
			for(String absFilePath : matchedFiles) {
				int index = -1;
				if(fileName.endsWith(".jsp")) {
					index = absFilePath.indexOf("webapp");
				} else if(fileName.endsWith(".java")) {
					if(!absFilePath.contains("src/main/java")) {
						return sendResult(false,"Java source code should be under src/main/java structure.");
					}
					index = absFilePath.indexOf("java")+5;
				}
				String patternPath = absFilePath.substring(index);
				String basePath = absFilePath.substring(0, index);
				if (!uniqueFileSet.add(patternPath)){
					return sendResult(false,"[ "+fileName+" ] is not unique. Found in the following locations { "+basePath+patternPath+" }");
				}
			}
		}
		return sendResult(true);
	}

	/**
	 * Done in a crude manner. Checks for the string after the last '.'
	 * @param strFile
	 * @return
	 */
	private String getExtension(String strFile){
		if (strFile !=null)
			return strFile.substring(strFile.lastIndexOf(".")+1);
		return null;
	}	
	
	private ResponseBuilder sendResult(boolean result){
		return sendResult(result, "");
	}
	
	private ResponseBuilder sendResult(boolean result, String resultDesc){
		return new ResponseBuilder(result, resultDesc);
	}
}
