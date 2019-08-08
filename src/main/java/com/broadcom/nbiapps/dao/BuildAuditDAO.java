package com.broadcom.nbiapps.dao;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.broadcom.nbiapps.entities.BuildAudit;

public interface BuildAuditDAO extends CrudRepository<BuildAudit, Long> {
    BuildAudit findByPullReqNumberAndBuildNumberAndSiloId(BigInteger pullReqNumber, BigInteger buildNumber, BigInteger siloId);
}
