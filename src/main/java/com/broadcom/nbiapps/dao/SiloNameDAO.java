package com.broadcom.nbiapps.dao;

import org.springframework.data.repository.CrudRepository;

import com.broadcom.nbiapps.entities.SiloName;

import java.util.List;

public interface SiloNameDAO extends CrudRepository<SiloName, Long> {
    List<SiloName> findByName(String name);
}
