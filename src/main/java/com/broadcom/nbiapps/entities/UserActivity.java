package com.broadcom.nbiapps.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


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
	@Column(name="user_act_id", unique=true, nullable=false)
	private BigInteger userActId;

	@Column(name="create_date_time", nullable=false)
	private Timestamp createDateTime;

	@Column(name="cycle_id", nullable=false)
	private BigInteger cycleId;

	@Column(name="silo_id", nullable=false)
	private BigInteger siloId;

	@Lob
	@Column(name="user_action_details", nullable=false)
	private String userActionDetails;

	@Column(name="user_name", nullable=false, length=50)
	private String userName;

	public UserActivity() {
	}

	public BigInteger getUserActId() {
		return this.userActId;
	}

	public void setUserActId(BigInteger userActId) {
		this.userActId = userActId;
	}

	public Timestamp getCreateDateTime() {
		return this.createDateTime;
	}

	public void setCreateDateTime(Timestamp createDateTime) {
		this.createDateTime = createDateTime;
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

	public String getUserActionDetails() {
		return this.userActionDetails;
	}

	public void setUserActionDetails(String userActionDetails) {
		this.userActionDetails = userActionDetails;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}