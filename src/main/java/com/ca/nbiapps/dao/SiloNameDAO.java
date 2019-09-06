package com.ca.nbiapps.dao;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.ca.nbiapps.entities.SiloName;

public interface SiloNameDAO extends CrudRepository<SiloName, BigInteger> {
    SiloName findBySiloName(String siloName);
}
