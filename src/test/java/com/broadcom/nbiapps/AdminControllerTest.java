/**
 * 
 */
package com.broadcom.nbiapps;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.broadcom.nbiapps.dao.SiloNameDAO;
import com.broadcom.nbiapps.entities.SiloName;
import com.broadcom.nbiapps.entities.SiloNameReq;

/**
 * @author Balaji N
 *
 */
@SpringBootTest
public class AdminControllerTest {

	@Autowired
	SiloNameDAO siloNameDAO;

	@Test
	void saveSiloNameTest() {
		SiloName siloName = new SiloName();
		SiloNameReq siloNameReq = new SiloNameReq();
		siloNameReq.setSiloName("Bdkdidid");
		siloName.setSiloNameReq(siloNameReq);
		siloName.setSiloId(3L);
		siloNameDAO.save(siloName);
	}
}
