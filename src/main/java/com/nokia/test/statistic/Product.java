package com.nokia.test.statistic;

import java.sql.Timestamp;

import com.nokia.granite.analyzer.CommonUtils;

public class Product {
	
	private int id;
	private String name;
	private String platform;
	private int valid;
	private Timestamp created;
	public int getId() {
		return id;
	}
	public void setId( int id ) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName( String name ) {
		this.name = name;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform( String platform ) {
		this.platform = platform;
	}
	public int getValid() {
		return valid;
	}
	public void setValid( int valid ) {
		this.valid = valid;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated( Timestamp created ) {
		this.created = created;
	}
	
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
