package com.nokia.test.statistic;

import java.sql.Timestamp;

import com.nokia.granite.analyzer.CommonUtils;

public class SubExecution {
	private int execId;
	private int subId;
	private Timestamp execTime;
	private String url;
	private String report;
	
	public int getExecId() {
		return execId;
	}

	public void setExecId( int execId ) {
		this.execId = execId;
	}

	public int getSubId() {
		return subId;
	}

	public void setSubId( int subId ) {
		this.subId = subId;
	}

	public Timestamp getExecTime() {
		return execTime;
	}

	public void setExecTime( Timestamp execTime ) {
		this.execTime = execTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl( String url ) {
		this.url = url;
	}
	
	public String getReport() {
		return report;
	}

	public void setReport( String report ) {
		this.report = report;
	}

	public String toString() {
		return CommonUtils.toJson( this );
	}
}
