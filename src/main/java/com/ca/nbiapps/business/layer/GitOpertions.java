package com.ca.nbiapps.business.layer;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Balaji
 *
 */
public class GitOpertions {
	private static final Logger logger = LoggerFactory.getLogger(GitOpertions.class);
	private String localGitRepo;

	public GitOpertions(String localGitRepo) throws IOException, GitAPIException {
		this.localGitRepo = localGitRepo;
		isValidLocalRepository();
	}

	public RevCommit commit(String commentMessage)
			throws IOException, NoHeadException, NoMessageException, UnmergedPathsException,
			ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException {
		logger.info("Going to commit " + commentMessage);
		try (Git git = Git.open(new File(localGitRepo))) {
			CommitCommand commitCommand = git.commit().setMessage(commentMessage);
			return commitCommand.call();
		}
	}

	public void addFileTOGit(String filePattern) throws NoFilepatternException, GitAPIException, IOException {
		try (Git git = Git.open(new File(localGitRepo))) {
			if (filePattern == null) {
				filePattern = ".";
			}
			AddCommand addCommand = git.add().addFilepattern(filePattern);
			addCommand.call();
		}
	}

	public void checkoutDir(String branchName) throws IOException, RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException, GitAPIException {
		try (Git git = Git.open(new File(localGitRepo))) {
			git.checkout().call();
		}
	}

	public Set<String> getAddedFiles() throws IOException, NoWorkTreeException, GitAPIException {
		try (Git git = Git.open(new File(localGitRepo))) {
			Status status = git.status().call();
			return status.getAdded();
		}
	}

	public Set<String> getModifiedFiles() throws IOException, NoWorkTreeException, GitAPIException {
		try (Git git = Git.open(new File(localGitRepo))) {
			Status status = git.status().call();
			return status.getModified();
		}
	}

	public Set<String> getDeletedFiles() throws IOException, NoWorkTreeException, GitAPIException {
		try (Git git = Git.open(new File(localGitRepo))) {
			Status status = git.status().call();
			return status.getRemoved();
		}
	}

	/**
	 * The path must either name a file or a directory exactly. All paths are always
	 * relative to the repository root. If a directory is specified all files
	 * recursively underneath that directory are matched. OR
	 * 
	 * @param path
	 * @throws Exception
	 */
	public void reset(String path) throws Exception {
		if (localGitRepo != null) {
			try (Git git = Git.open(new File(localGitRepo))) {
				Set<String> files = new TreeSet<>();
				Status status = git.status().addPath(path + "/").call();

				files.addAll(status.getUntrackedFolders());
				files.addAll(status.getUntracked());
				files.addAll(status.getModified());
				files.addAll(status.getRemoved());
				files.addAll(status.getUncommittedChanges());

				logger.info("Git Status [git status " + path + "]: " + files);
				Set<String> clearedList = git.clean().setCleanDirectories(true).setPaths(files).setForce(true).call();

				for (String item : clearedList) {
					logger.info("Following files reset from local repo:" + item);
				}
			}
		}
	}

	public byte[] getMasterBranchRootPomContent(String path) throws IOException {
		try (Git git = Git.open(new File(localGitRepo))) {
			ObjectId masterTreeId = git.getRepository().resolve("refs/heads/master^{tree}");
			try (TreeWalk treeWalk = TreeWalk.forPath(git.getRepository(), path, masterTreeId)) {
				ObjectId blobId = treeWalk.getObjectId(0);
				ObjectLoader objectLoader = loadObject(git, blobId);
				return objectLoader.getBytes();
			}
		}
	}

	private ObjectLoader loadObject(Git git, ObjectId objectId) throws IOException {
		try (ObjectReader objectReader = git.getRepository().newObjectReader()) {
			return objectReader.open(objectId);
		}
	}

	private boolean isValidLocalRepository() throws IOException {
		try (Git git = Git.open(new File(localGitRepo))) {
			return true;
		} catch(IOException e) {
			logger.error("Error: "+e, e);
			throw e;
		}

	}

}
