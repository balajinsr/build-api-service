package com.ca.nbiapps.business.layer;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
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
		try(GitOpertions gitOpertions = new GitOpertions(repoPath)) {
			Set<String> addedFiles = gitOpertions.getAddedFiles();
			System.out.println(addedFiles.toString());
			Assertions.assertTrue(addedFiles != null && !addedFiles.isEmpty());
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
			Assertions.assertTrue(false);
		} catch (Exception e1) {		
			e1.printStackTrace();
		}
	}

	@Test
	public void modifiedFilesTest() {
		String repoPath = System.getenv("GIT_REPO_LOCATION");
		try (GitOpertions gitOpertions = new GitOpertions(repoPath)) {
			Set<String> modifiedFiles = gitOpertions.getModifiedFiles();
			System.out.println(modifiedFiles.toString());
			Assertions.assertTrue(modifiedFiles != null && !modifiedFiles.isEmpty());
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
			Assertions.assertTrue(false);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void deletedFilesTest() {
		String repoPath = System.getenv("GIT_REPO_LOCATION");
		try (GitOpertions gitOpertions = new GitOpertions(repoPath)) {
			Set<String> deletedFiles = gitOpertions.getDeletedFiles();
			System.out.println(deletedFiles.toString());
			Assertions.assertTrue(deletedFiles != null && !deletedFiles.isEmpty());
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
			Assertions.assertTrue(false);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void gitDiffTest() {
		String repoPath = System.getenv("GIT_REPO_LOCATION");
		try (GitOpertions gitOpertions = new GitOpertions(repoPath)) {
			List<DiffEntry> diffEntry = gitOpertions.getDiff("refs/heads/master", "refs/heads/DT-123123123");
			for (DiffEntry entry : diffEntry) {
				System.out.println(entry);
				if (entry.getChangeType().equals(DiffEntry.ChangeType.ADD)) {
					System.out.println("Added file: " + entry.getNewPath());
				} else if (entry.getChangeType().equals(DiffEntry.ChangeType.MODIFY)) {
					System.out.println("Modified file: " + entry.getNewPath());
				} else if (entry.getChangeType().equals(DiffEntry.ChangeType.DELETE)) {
					System.out.println("Deleted file: " + entry.getOldPath());
				} else {
					System.out.println("Else case..");
				}
			}
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
			Assertions.assertTrue(false);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
