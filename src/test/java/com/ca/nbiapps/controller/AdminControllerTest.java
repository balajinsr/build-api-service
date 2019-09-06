/**
 * 
 */
package com.ca.nbiapps.controller;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ca.nbiapps.dao.SiloNameDAO;
import com.ca.nbiapps.entities.SiloName;
import com.ca.nbiapps.entities.SiloNameReq;

/**
 * @author Balaji N
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AdminControllerTest {

	@Autowired
	SiloNameDAO siloNameDAO;

	@Test
	void saveSiloNameTest() {
		SiloName siloName = new SiloName();
		SiloNameReq siloNameReq = new SiloNameReq();
		siloNameReq.setSiloName("Bdkdidid");
		siloName.setSiloNameReq(siloNameReq);
		siloName.setSiloId(BigInteger.valueOf(3));
		siloNameDAO.save(siloName);
	}
}
