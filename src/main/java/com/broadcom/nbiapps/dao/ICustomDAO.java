/**
 * 
 */
package com.broadcom.nbiapps.dao;

import com.broadcom.nbiapps.entities.SiloName;

/**
 * @author Balaji N
 *
 */

public interface ICustomDAO {
	void save(SiloName siloName);

	public void merge(SiloName siloName);
}
