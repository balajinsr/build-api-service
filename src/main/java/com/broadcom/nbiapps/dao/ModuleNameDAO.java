package com.broadcom.nbiapps.dao;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import com.broadcom.nbiapps.entities.ModuleName;

public interface ModuleNameDAO extends CrudRepository<ModuleName, BigInteger> {
    
}
