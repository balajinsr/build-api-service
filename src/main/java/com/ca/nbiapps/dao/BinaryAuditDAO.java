package com.ca.nbiapps.dao;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.ca.nbiapps.entities.BinaryAudit;

public interface BinaryAuditDAO extends CrudRepository<BinaryAudit, BigInteger> {
    
}
