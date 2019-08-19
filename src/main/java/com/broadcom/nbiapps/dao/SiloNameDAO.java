package com.broadcom.nbiapps.dao;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.broadcom.nbiapps.entities.SiloName;

public interface SiloNameDAO extends CrudRepository<SiloName, BigInteger> {
    SiloName findBySiloName(String siloName);
}
