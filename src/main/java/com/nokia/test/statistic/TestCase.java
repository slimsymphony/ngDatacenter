package com.nokia.test.statistic;

import com.nokia.granite.analyzer.CommonUtils;

public class TestCase {

	private int id;
	private String caseId;
	private String caseName;
	private String feature;
	private String featureGroup;
	private String product;
	private String path;
	private String testClass;
	private String method;
	private int qcid;
	private int qcIdentifier;
	private String file;
	private String directory;
	
	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId( String caseId ) {
		this.caseId = caseId;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName( String caseName ) {
		this.caseName = caseName;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature( String feature ) {
		this.feature = feature;
	}

	public String getFeatureGroup() {
		return featureGroup;
	}

	public void setFeatureGroup( String featureGroup ) {
		this.featureGroup = featureGroup;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct( String product ) {
		this.product = product;
	}

	public String getPath() {
		return path;
	}

	public void setPath( String path ) {
		this.path = path;
	}

	public String getTestClass() {
		return testClass;
	}

	public void setTestClass( String testClass ) {
		this.testClass = testClass;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod( String method ) {
		this.method = method;
	}

	public int getQcid() {
		return qcid;
	}

	public void setQcid( int qcid ) {
		this.qcid = qcid;
	}

	public int getQcIdentifier() {
		return qcIdentifier;
	}

	public void setQcIdentifier( int qcIdentifier ) {
		this.qcIdentifier = qcIdentifier;
	}
	
	public String getFile() {
		return file;
	}

	public void setFile( String file ) {
		this.file = file;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory( String directory ) {
		this.directory = directory;
	}

	@Override
	public boolean equals( Object c ) {
		if ( c == null )
			return false;
		if ( c instanceof TestCase ) {
			String cid = ( ( TestCase ) c ).getCaseId();
			if ( this.caseId == null || cid == null )
				return false;
			else if ( this.caseId.equals( cid ) )
				return true;
			else
				return false;
		} else
			return false;
	}

	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
