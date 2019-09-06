/**
 * 
 */
package com.ca.nbiapps.dao;

import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ca.nbiapps.dao.PullRequestDataDAO;
import com.ca.nbiapps.entities.PullRequestData;
import com.ca.nbiapps.model.PullRequest;

/**
 * @author Balaji N
 *
 */

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class PullRequestDataDAOTest {

	private static final Logger logger = LoggerFactory.getLogger(PullRequestDataDAOTest.class);

	@Autowired
	PullRequestDataDAO pullRequestDataDAO;

	@Test
	public void findByPullReqNumberAndSiloIdTest() {
		PullRequestData pullReqData = pullRequestDataDAO.findByPullReqNumberAndSiloId(BigInteger.valueOf(1), BigInteger.valueOf(1));
		logger.info("findByPullReqNumberAndSiloIdTest::"+pullReqData.toString());
		Assertions.assertTrue(pullReqData != null && pullReqData.getPullReqNumber().longValue() == 1);
	}
	
	@Test
	public void saveTest() {
		PullRequestData pullRequestData = new PullRequestData();
		pullRequestData.setAction("opened");
		pullRequestData.setPullReqNumber(BigInteger.valueOf(2));
		pullRequestData.setSiloId(BigInteger.valueOf(1));
		
		PullRequest pullRequest = new PullRequest();
		pullRequest.setTitle("234234234");
		pullRequest.setPatch_url("heel");
		pullRequestData.setPullRequest(pullRequest);
		
		PullRequestData pullReqData = pullRequestDataDAO.save(pullRequestData);
		logger.info("saveTest::"+pullReqData.toString());
		Assertions.assertTrue(pullReqData != null && pullReqData.getPullReqNumber().longValue() == 2);
	}
	
}
