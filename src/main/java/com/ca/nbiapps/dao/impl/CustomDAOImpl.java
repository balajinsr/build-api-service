package com.ca.nbiapps.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.ca.nbiapps.dao.ICustomDAO;
import com.ca.nbiapps.entities.SiloName;

@Repository
public class CustomDAOImpl implements ICustomDAO {
	  @PersistenceContext
	  private EntityManager em;
	   
	  
	   @Transactional
	   @Override
	   public void save(SiloName siloName) {
	      em.persist(siloName);
	      em.flush();
	   }
	   
	   
	   @Transactional
	   @Override
	   public void merge(SiloName siloName) {
	      em.merge(siloName);
	      em.flush();
	   }
}
