package com.broadcom.nbiapps.dao;

import org.springframework.data.repository.CrudRepository;
import com.broadcom.nbiapps.entities.BuildAudit;

public interface BuildAuditDAO extends CrudRepository<BuildAudit, Long> {
    
}
