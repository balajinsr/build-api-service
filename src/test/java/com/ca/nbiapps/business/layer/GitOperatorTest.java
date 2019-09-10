package com.ca.nbiapps.business.layer;

import java.io.IOException;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 
 * @author Balaji N
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class GitOperatorTest {
	
	@Test
	public void addedFilesTest() {
		String repoPath = System.getenv("GIT_REPO_LOCATION");
		try {
			GitOpertions gitOpertions = new GitOpertions(repoPath);
			Set<String> addedFiles = gitOpertions.getAddedFiles();
			System.out.println(addedFiles.toString());
			Assertions.assertTrue(addedFiles != null && !addedFiles.isEmpty());
		} catch(IOException | GitAPIException e) {
			e.printStackTrace();
			Assertions.assertTrue(false);
		}
	}
}
