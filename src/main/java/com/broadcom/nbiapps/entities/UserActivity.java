package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigInteger;


/**
 * The persistent class for the user_activities database table.
 * 
 */
@Entity
@Table(name="user_activities")
@NamedQuery(name="UserActivity.findAll", query="SELECT u FROM UserActivity u")
public class UserActivity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_act_id")
	private Long userActId;

	@Lob
	private String action;

	@Column(name="cycle_id")
	private BigInteger cycleId;

	@Column(name="silo_id")
	private BigInteger siloId;

	private Timestamp timestamp;

	@Column(name="user_name")
	private String userName;

	public UserActivity() {
	}

	public Long getUserActId() {
		return this.userActId;
	}

	public void setUserActId(Long userActId) {
		this.userActId = userActId;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public BigInteger getCycleId() {
		return this.cycleId;
	}

	public void setCycleId(BigInteger cycleId) {
		this.cycleId = cycleId;
	}

	public BigInteger getSiloId() {
		return this.siloId;
	}

	public void setSiloId(BigInteger siloId) {
		this.siloId = siloId;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}