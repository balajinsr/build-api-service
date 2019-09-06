package com.ca.nbiapps.dao;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.ca.nbiapps.entities.BuildAudit;

public interface BuildAuditDAO extends CrudRepository<BuildAudit, Long> {
    BuildAudit findByPullReqNumberAndBuildNumberAndSiloId(BigInteger pullReqNumber, BigInteger buildNumber, BigInteger siloId);
}
