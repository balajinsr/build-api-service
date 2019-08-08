package com.broadcom.nbiapps.dao;

import org.springframework.data.repository.CrudRepository;

import com.broadcom.nbiapps.entities.SiloName;

public interface SiloNameDAO extends CrudRepository<SiloName, Long> {
    SiloName findBySiloName(String siloName);
}
