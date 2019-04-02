package com.gigaspaces.mq.common;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;


/**
 * A simple object used to work with the Space. Important properties include the id
 * of the object, a type (used to perform routing when working with partitioned space),
 * the raw data and processed data, and a boolean flag indicating if this Data object
 * was processed or not.
 */
@SpaceClass
public class Employee {


	private Boolean processed;
	private String ID;
	private String FIRSTNAME;
	private String MIDINIT;
	private String LASTNAME;
	private String workdept;
	private String PHONENO;
	private String HIREDATE;
	private String JOB;
	private String edlevel;
	private String SEX;
	private String BIRTHDAY;
	private String SALARY;
	private String BONUS;
	private String COMM;

	/**
	 * Constructs a new Data object.
	 */
	public Employee() {

		this.processed = false;
	}
	public Employee(String empNo, String firstName, String midInit, String lastName, String workdpt, String phoneNo,
					String hireDate, String job, String edLevel, String sex, String birthday, String salary,
					String bonus, String comm) {

		this.processed = true;
		this.ID = empNo;
		this.FIRSTNAME = firstName;
		this.MIDINIT = midInit;
		this.LASTNAME = lastName;
		this.workdept = workdpt;
		this.PHONENO = phoneNo;
		this.HIREDATE = hireDate;
		this.JOB = job;
		this.edlevel = edLevel;
		this.SEX = sex;
		this.BIRTHDAY = birthday;
		this.SALARY = salary;
		this.BONUS = bonus;
		this.COMM = comm;

	}

	/**
	 * Constructs a new Data object with the given type
	 * and raw data.
	 */

	public String getFIRSTNAME() {
		return FIRSTNAME;
	}

	public void setFIRSTNAME(String fIRSTNAME) {
		FIRSTNAME = fIRSTNAME;
	}

	public String getLASTNAME() {
		return LASTNAME;
	}

	public void setLASTNAME(String lASTNAME) {
		LASTNAME = lASTNAME;
	}

	public String getEDLEVEL(){
		return edlevel;

	}

	public void setEDLEVEL(String edlevel){
		this.edlevel = edlevel;
	}


	public String getWORKDEPT(){
		return workdept;

	}

	public void setWORKDEPT(String workdept){
		this.workdept = workdept;
	}

	@SpaceId
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getMIDINIT() {
		return MIDINIT;
	}

	public void setMIDINIT(String MIDINIT) {
		this.MIDINIT = MIDINIT;
	}

	public String getPHONENO() {
		return PHONENO;
	}

	public void setPHONENO(String PHONENO) {
		this.PHONENO = PHONENO;
	}

	public String getHIREDATE() {
		return HIREDATE;
	}

	public void setHIREDATE(String HIREDATE) {
		this.HIREDATE = HIREDATE;
	}

	public String getJOB() {
		return JOB;
	}

	public void setJOB(String JOB) {
		this.JOB = JOB;
	}

	public String getSEX() {
		return SEX;
	}

	public void setSEX(String SEX) {
		this.SEX = SEX;
	}

	public String getBIRTHDAY() {
		return BIRTHDAY;
	}

	public void setBIRTHDAY(String BIRTHDAY) {
		this.BIRTHDAY = BIRTHDAY;
	}

	public String getSALARY() {
		return SALARY;
	}

	public void setSALARY(String SALARY) {
		this.SALARY = SALARY;
	}

	public String getBONUS() {
		return BONUS;
	}

	public void setBONUS(String BONUS) {
		this.BONUS = BONUS;
	}

	public String getCOMM() {
		return COMM;
	}

	public void setCOMM(String COMM) {
		this.COMM = COMM;
	}

	public Boolean getProcessed() {
		return processed;
	}



	/**
	 * The processed data this object holds.
	 */

	/**
	 * A boolean flag indicating if the data object was processed or not.
	 */
	public Boolean isProcessed() {
		return processed;
	}

	/**
	 * A boolean flag indicating if the data object was processed or not.
	 */
	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	public String toString() {
		return "id[" + ID + "]  processed[" + processed + "]";
	}
}
