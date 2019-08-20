package com.broadcom.nbiapps.dao;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.broadcom.nbiapps.entities.BinaryAudit;

public interface BinaryAuditDAO extends CrudRepository<BinaryAudit, BigInteger> {
    
}
