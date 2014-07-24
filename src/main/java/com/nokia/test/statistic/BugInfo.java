package com.nokia.test.statistic;

import com.nokia.granite.analyzer.CommonUtils;

public class BugInfo {
	
	private int id;
	private String featureGroup;
	private String feature;
	private String subFeature;
	private String pkgversion;
	private String status;
	private String resolution;
	private String priority;
	private String a360expteam;
	private String summary;
	private int groupId;
	private String affectProduct;
	private String founder;
	private String foundphase;
	private String hardware;
	private String keywords;
	private int rpn;
	private int rpnDetection;
	private int rpnOccurrence;
	private int rpnServerity;
	private String serverity;
	private String targetMilestone;
	private String url;
	private String errorCategory;
	private int interactionLevel;
	private String interruptAction;
	private int needTrace;
	private int relCaseID;
	private String relCaseName;
	
	public int getId() {
		return id;
	}



	public void setId( int id ) {
		this.id = id;
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



	public String getSubFeature() {
		return subFeature;
	}



	public void setSubFeature( String subFeature ) {
		this.subFeature = subFeature;
	}



	public String getPkgversion() {
		return pkgversion;
	}



	public void setPkgversion( String pkgversion ) {
		this.pkgversion = pkgversion;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus( String status ) {
		this.status = status;
	}



	public String getResolution() {
		return resolution;
	}



	public void setResolution( String resolution ) {
		this.resolution = resolution;
	}



	public String getPriority() {
		return priority;
	}



	public void setPriority( String priority ) {
		this.priority = priority;
	}



	public String getA360expteam() {
		return a360expteam;
	}



	public void setA360expteam( String a360expteam ) {
		this.a360expteam = a360expteam;
	}



	public String getSummary() {
		return summary;
	}



	public void setSummary( String summary ) {
		this.summary = summary;
	}



	public int getGroupId() {
		return groupId;
	}



	public void setGroupId( int groupId ) {
		this.groupId = groupId;
	}



	public String getAffectProduct() {
		return affectProduct;
	}



	public void setAffectProduct( String affectProduct ) {
		this.affectProduct = affectProduct;
	}



	public String getFounder() {
		return founder;
	}



	public void setFounder( String founder ) {
		this.founder = founder;
	}



	public String getFoundphase() {
		return foundphase;
	}



	public void setFoundphase( String foundphase ) {
		this.foundphase = foundphase;
	}



	public String getHardware() {
		return hardware;
	}



	public void setHardware( String hardware ) {
		this.hardware = hardware;
	}



	public String getKeywords() {
		return keywords;
	}



	public void setKeywords( String keywords ) {
		this.keywords = keywords;
	}



	public int getRpn() {
		return rpn;
	}



	public void setRpn( int rpn ) {
		this.rpn = rpn;
	}



	public int getRpnDetection() {
		return rpnDetection;
	}



	public void setRpnDetection( int rpnDetection ) {
		this.rpnDetection = rpnDetection;
	}



	public int getRpnOccurrence() {
		return rpnOccurrence;
	}



	public void setRpnOccurrence( int rpnOccurrence ) {
		this.rpnOccurrence = rpnOccurrence;
	}



	public int getRpnServerity() {
		return rpnServerity;
	}



	public void setRpnServerity( int rpnServerity ) {
		this.rpnServerity = rpnServerity;
	}



	public String getServerity() {
		return serverity;
	}



	public void setServerity( String serverity ) {
		this.serverity = serverity;
	}



	public String getTargetMilestone() {
		return targetMilestone;
	}



	public void setTargetMilestone( String targetMilestone ) {
		this.targetMilestone = targetMilestone;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl( String url ) {
		this.url = url;
	}



	public String getErrorCategory() {
		return errorCategory;
	}



	public void setErrorCategory( String errorCategory ) {
		this.errorCategory = errorCategory;
	}



	public int getInteractionLevel() {
		return interactionLevel;
	}



	public void setInteractionLevel( int interactionLevel ) {
		this.interactionLevel = interactionLevel;
	}



	public String getInterruptAction() {
		return interruptAction;
	}



	public void setInterruptAction( String interruptAction ) {
		this.interruptAction = interruptAction;
	}



	public int getNeedTrace() {
		return needTrace;
	}



	public void setNeedTrace( int needTrace ) {
		this.needTrace = needTrace;
	}



	public int getRelCaseID() {
		return relCaseID;
	}



	public void setRelCaseID( int relCaseID ) {
		this.relCaseID = relCaseID;
	}



	public String getRelCaseName() {
		return relCaseName;
	}



	public void setRelCaseName( String relCaseName ) {
		this.relCaseName = relCaseName;
	}



	public String toString() {
		return CommonUtils.toJson( this );
	}
}
