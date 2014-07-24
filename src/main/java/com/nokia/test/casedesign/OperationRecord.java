package com.nokia.test.casedesign;

import java.sql.Timestamp;

import com.nokia.granite.analyzer.CommonUtils;

public class OperationRecord {
	public enum Operation{
		LOGIN,ADD,UPDATE,REVIEW,APPROVE,SYNC,DEL,ATTACH,DELATTACH, UNKNOWN;
		public static Operation parse(String str) {
			if(str==null||str.trim().isEmpty())
				return UNKNOWN;
			for( Operation op : Operation.values() ) {
				if(op.name().equalsIgnoreCase( str ))
					return op;
			}
			return UNKNOWN;
		}
	};
	private String user;
	private Operation operation_type;
	private Timestamp operation_time;
	private int rel_caseId ;
	private String extension;
	public String getUser() {
		return user;
	}
	public void setUser( String user ) {
		this.user = user;
	}
	public Operation getOperation_type() {
		return operation_type;
	}
	public void setOperation_type( Operation operation_type ) {
		this.operation_type = operation_type;
	}
	public Timestamp getOperation_time() {
		return operation_time;
	}
	public void setOperation_time( Timestamp operation_time ) {
		this.operation_time = operation_time;
	}
	public int getRel_caseId() {
		return rel_caseId;
	}
	public void setRel_caseId( int rel_caseId ) {
		this.rel_caseId = rel_caseId;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension( String extension ) {
		this.extension = extension;
	}
	
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
