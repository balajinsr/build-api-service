package com.ca.nbiapps.dao;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.ca.nbiapps.entities.ModuleName;

public interface ModuleNameDAO extends CrudRepository<ModuleName, BigInteger> {
    
}
