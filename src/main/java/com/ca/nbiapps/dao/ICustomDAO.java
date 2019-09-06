/**
 * 
 */
package com.ca.nbiapps.dao;

import com.ca.nbiapps.entities.SiloName;

/**
 * @author Balaji N
 *
 */

public interface ICustomDAO {
	void save(SiloName siloName);

	public void merge(SiloName siloName);
}
