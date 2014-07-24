package com.nokia.test.statistic;

import java.util.HashMap;
import java.util.Map;

import com.nokia.granite.analyzer.CommonUtils;

public class TestCaseResult {

	private int qcId;
	private String caseName;
	private String result;
	private String featureGroup;
	private String feature;
	private Map<String,String> scripts = new HashMap<String,String>();
	private Map<Integer, String> bugs = new HashMap<Integer, String>();

	public int getQcId() {
		return qcId;
	}

	public void setQcId( int qcId ) {
		this.qcId = qcId;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName( String caseName ) {
		this.caseName = caseName;
	}

	public String getResult() {
		return result;
	}

	public void setResult( String result ) {
		this.result = result;
	}

	public String getFeatureGroup() {
		return featureGroup;
	}

	public void setFeatureGroup( String featureGroup ) {
		this.featureGroup = featureGroup;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature( String feature ) {
		this.feature = feature;
	}

	public void addBug( int bugId, String bugInfo ) {
		bugs.put( bugId, bugInfo );
	}

	public Map<Integer, String> getBugs() {
		return bugs;
	}

	public void setBugs( Map<Integer, String> bugs ) {
		this.bugs = bugs;
	}

	public Map<String, String> getScripts() {
		return scripts;
	}

	public void setScripts( Map<String, String> scripts ) {
		this.scripts = scripts;
	}
	
	public void addScript(String scriptName,String sResult) {
		scripts.put( scriptName, sResult );
	}

	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
