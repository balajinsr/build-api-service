package com.ca.nbiapps.business.layer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
/**
 * 
 * @author Balaji N
 *
 */
public class TarGzGenerator {
	/**
	 * 
	 * @param sourceFilePath - sourceFilePath will be file path or directory
	 * @param tarFileName - tar file path. 
	 * @throws IOException
	 */
	public void createTarFile(File sourceFilePath, File tarFilePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(tarFilePath.getAbsolutePath().concat(".tar.gz"));
		GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));	
		try (TarArchiveOutputStream tarOs = new TarArchiveOutputStream(gos)) {
			tarOs.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			addFilesToTarGZ(sourceFilePath, "", tarOs);
		} 
	}

	private void addFilesToTarGZ(File sourceDir, String parent, TarArchiveOutputStream tarArchive) throws IOException {
		String entryName = parent + sourceDir.getName();
		// add tar ArchiveEntry
		tarArchive.putArchiveEntry(new TarArchiveEntry(sourceDir, entryName));
		if (sourceDir.isFile()) {
			FileInputStream fis = new FileInputStream(sourceDir);
			try (BufferedInputStream bis = new BufferedInputStream(fis)) {
				// Write file content to archive
				IOUtils.copy(bis, tarArchive);
				tarArchive.closeArchiveEntry();
			}
		} else if (sourceDir.isDirectory()) {
			// no need to copy any content since it is
			// a directory, just close the outputstream
			tarArchive.closeArchiveEntry();
			// for files in the directories
			for (File f : sourceDir.listFiles()) {
				// recursively call the method for all the subdirectories
				addFilesToTarGZ(f, entryName + File.separator, tarArchive);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		new TarGzGenerator().createTarFile(new File("C:\\work-ca\\ca-paysec-properties-migration-app-1.0\\properties-migration"), new File("C:\\\\work-ca\\\\ca-paysec-properties-migration-app-1.0\\nbi-tomee-plugins"));
	}
}
