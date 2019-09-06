package com.ca.nbiapps.dao;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.ca.nbiapps.entities.PullRequestData;

public interface PullRequestDataDAO extends CrudRepository<PullRequestData, Long> {
	
    PullRequestData findByPullReqNumberAndSiloId(BigInteger pullReqNumber, BigInteger siloId);
    
}
