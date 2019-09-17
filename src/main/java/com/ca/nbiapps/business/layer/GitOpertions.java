package com.ca.nbiapps.business.layer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidMergeHeadsException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.HttpTransport;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 
 * @author Balaji
 *
 */
public class GitOpertions implements AutoCloseable {
	private static final Logger logger = LoggerFactory.getLogger(GitOpertions.class);
	private Git git;
	private String localGitRepo;

	public GitOpertions(String localGitRepo) throws IOException, GitAPIException {
		this.git = Git.open(new File(localGitRepo));
	}

	public SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
		@Override
		protected void configure(Host hc, Session session) {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
		}

		public String getOsName() {
			return System.getProperty("os.name");
		}

		public String getSSHFileLocation() {
			if (getOsName().startsWith("Windows")) {
				String sshPath = System.getenv("systemdrive") + File.separator + System.getenv("homepath");
				return sshPath + File.separator + ".ssh" + File.separator + "rsa";
			} else {
				return "~" + File.separator + ".ssh" + File.separator + "rsa";
			}
		}

		@Override
		protected JSch createDefaultJSch(FS fs) throws JSchException {
			JSch defaultJSch = super.createDefaultJSch(fs);
			defaultJSch.addIdentity(getSSHFileLocation());
			return defaultJSch;
		}
	};

	public RevCommit commit(String commentMessage) throws IOException, NoHeadException, NoMessageException, UnmergedPathsException,
			ConcurrentRefUpdateException, WrongRepositoryStateException, AbortedByHookException, GitAPIException {
		logger.info("Going to commit " + commentMessage);
		CommitCommand commitCommand = git.commit().setMessage(commentMessage);
		return commitCommand.call();
	}

	public void addFileTOGit(String filePattern) throws NoFilepatternException, GitAPIException, IOException {
		if (filePattern == null) {
			filePattern = ".";
		}
		AddCommand addCommand = git.add().addFilepattern(filePattern);
		addCommand.call();
	}

	public void gitClone(String sshURL) throws GitAPIException, IOException {
		File cloneRepo = new File(localGitRepo);
		if (!cloneRepo.exists()) {
			CloneCommand cloneCommand = Git.cloneRepository();
			cloneCommand.setURI("");
			cloneCommand.setDirectory(cloneRepo);
			cloneCommand.setTransportConfigCallback(new TransportConfigCallback() {
				@Override
				public void configure(Transport transport) {
					SshTransport sshTransport = (SshTransport) transport;
					sshTransport.setSshSessionFactory(sshSessionFactory);
				}
			});
			cloneCommand.call();
		}
	}

	public void checkoutDir(String branchName)
			throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		git.checkout().call();
	}

	
	public String fetch(String taskBranchName) throws InvalidRemoteException, TransportException, GitAPIException  {    
	    RefSpec spec = new RefSpec("refs/remotes/origin/"+taskBranchName);
	    FetchCommand command = git.fetch();
	    command.setRefSpecs(spec);
	    FetchResult result = command.call();
	    return result.getMessages();
	}
	
	/**
	 * 
	 * @param isForce
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	public void gitPush(boolean isForce) throws InvalidRemoteException, TransportException, GitAPIException {
		PushCommand pushCommand = git.push();
		if (isForce) {
			pushCommand.setForce(isForce);
		}
		pushCommand.setTransportConfigCallback(new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
				if (transport instanceof SshTransport) {
					SshTransport sshTransport = (SshTransport) transport;
					sshTransport.setSshSessionFactory(sshSessionFactory);
				} else if (transport instanceof HttpTransport) {
					// configure HTTP protocol specifics
				}
			}
		});
		pushCommand.call();

	}
	/**
	 * 
	 * @param remoteName - it will be "origin"/"upstream"
	 * @param branchName - branch to pull from remote.
	 * @throws WrongRepositoryStateException
	 * @throws InvalidConfigurationException
	 * @throws InvalidRemoteException
	 * @throws CanceledException
	 * @throws RefNotFoundException
	 * @throws RefNotAdvertisedException
	 * @throws NoHeadException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	public void gitPull(String remoteName, String branchName) throws WrongRepositoryStateException, InvalidConfigurationException, InvalidRemoteException, CanceledException, RefNotFoundException,
			RefNotAdvertisedException, NoHeadException, TransportException, GitAPIException {
		PullCommand pullCommand = git.pull();
		pullCommand.setTransportConfigCallback(new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
				SshTransport sshTransport = (SshTransport) transport;
				sshTransport.setSshSessionFactory(sshSessionFactory);
			}
		});
		
		pullCommand.setRemote(remoteName);
		pullCommand.setRemoteBranchName(branchName);
	 
		PullResult pullResult = pullCommand.call();
		logger.info("git pull is " + (pullResult.isSuccessful() ? "Success" : "Failed"));
		logger.info("MergeStatus: " + pullResult.getMergeResult().getMergeStatus().toString());
		logger.info("FetchResult: " + pullResult.getFetchResult().getMessages());
	}
	
	
	public MergeResult merge() throws NoHeadException, ConcurrentRefUpdateException, CheckoutConflictException, InvalidMergeHeadsException, WrongRepositoryStateException, NoMessageException, GitAPIException {
		MergeCommand mergeCommand = git.merge();
		mergeCommand.setFastForward(MergeCommand.FastForwardMode.NO_FF);
		mergeCommand.setCommit(false);
		return mergeCommand.call();
	}

	public List<DiffEntry> gitDiff(String branchOneRef, String branchTwoRef) throws IOException, GitAPIException {
		Repository repo = git.getRepository();
		AbstractTreeIterator oldTreeParser = prepareTreeParser(repo, branchOneRef);
		AbstractTreeIterator newTreeParser = prepareTreeParser(repo, branchTwoRef);
		List<DiffEntry> diff = git.diff().setShowNameAndStatusOnly(true).setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
		return diff;
	}

	private AbstractTreeIterator prepareTreeParser(Repository repo, String ref) throws IOException {
		Ref head = repo.exactRef(ref);
		try (RevWalk walk = new RevWalk(repo)) {
			RevCommit commit = walk.parseCommit(head.getObjectId());
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repo.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			} finally {
				walk.dispose();
			}
			return treeParser;
		}
	}

	/**
	 * The path must either name a file or a directory exactly. All paths are always
	 * relative to the repository root. If a directory is specified all files
	 * recursively underneath that directory are matched.
	 * 
	 * @throws GitAPIException
	 * @throws NoWorkTreeException
	 */
	public void reset() throws NoWorkTreeException, GitAPIException {
		Set<String> clearedList = git.clean().setCleanDirectories(true).setForce(true).call();
		for (String item : clearedList) {
			logger.info("Following files reset from local repo:" + item);
		}
	}

	public byte[] getMasterBranchRootPomContent(String path) throws IOException {
		ObjectId masterTreeId = git.getRepository().resolve("refs/heads/master^{tree}");
		try (TreeWalk treeWalk = TreeWalk.forPath(git.getRepository(), path, masterTreeId)) {
			ObjectId blobId = treeWalk.getObjectId(0);
			ObjectLoader objectLoader = loadObject(blobId);
			return objectLoader.getBytes();
		}
	}

	private ObjectLoader loadObject(ObjectId objectId) throws IOException {
		try (ObjectReader objectReader = git.getRepository().newObjectReader()) {
			return objectReader.open(objectId);
		}
	}

	@Override
	public void close() {
		if (git != null) {
			git.close();
		}
	}
}
