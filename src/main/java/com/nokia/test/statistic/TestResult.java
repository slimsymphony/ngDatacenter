package com.nokia.test.statistic;

import com.nokia.granite.analyzer.CommonUtils;

public class TestResult {
	
	final public static String PASS = "PASS";
	final public static String FAIL = "FAIL";
	final public static String NORESULT = "NORESULT";
	private int caseId;
	private String result;
	private int execId;
	private int duration;
	private String message;
	private String detail;
	private int bugId;
	private String bugInfo;
	private String reference;
	private String oriResult;
	private int subId;
	
	public int getCaseId() {
		return caseId;
	}

	public void setCaseId( int caseId ) {
		this.caseId = caseId;
	}

	public String getResult() {
		return result;
	}

	public void setResult( String result ) {
		this.result = result;
	}

	public int getExecId() {
		return execId;
	}

	public void setExecId( int execId ) {
		this.execId = execId;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration( int interval ) {
		this.duration = interval;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage( String message ) {
		this.message = message;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail( String detail ) {
		this.detail = detail;
	}

	public int getBugId() {
		return bugId;
	}

	public void setBugId( int bugId ) {
		this.bugId = bugId;
	}

	public String getBugInfo() {
		return bugInfo;
	}

	public void setBugInfo( String bugInfo ) {
		this.bugInfo = bugInfo;
	}

	public String getReference() {
		return reference;
	}

	public void setReference( String reference ) {
		this.reference = reference;
	}

	public String getOriResult() {
		return oriResult;
	}

	public void setOriResult( String oriResult ) {
		this.oriResult = oriResult;
	}

	public int getSubId() {
		return subId;
	}

	public void setSubId( int subId ) {
		this.subId = subId;
	}

	public String toString() {
		return CommonUtils.toJson( this );
	}
}
