package com.nokia.test.casedesign;

import com.nokia.granite.analyzer.CommonUtils;

public class TextCase {
	private int id;
	private String name;
	private String content;
	private int isReviewed;
	private String reviewer;
	private int isSynchronized;
	private String feature;
	private String featureGroup;
	private String testArea;
	private String validFor;
	private String designer;
	private int traceable;
	private String subject;
	private int isApproved;
	private String approver;
	private String automationOwner;
	private String testType;
	private String automationState;
	private String comments;
	private int errorId;
	private String condition;
	private String srtLevel;
	private String type;
	private int qcId;
	
	
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
	public String getContent() {
		return content;
	}
	public void setContent( String content ) {
		this.content = content;
	}
	public int getIsReviewed() {
		return isReviewed;
	}
	public void setIsReviewed( int isReviewed ) {
		this.isReviewed = isReviewed;
	}
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer( String reviewer ) {
		this.reviewer = reviewer;
	}
	public int getIsSynchronized() {
		return isSynchronized;
	}
	public void setIsSynchronized( int isSynchronized ) {
		this.isSynchronized = isSynchronized;
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
	public String getTestArea() {
		return testArea;
	}
	public void setTestArea( String testArea ) {
		this.testArea = testArea;
	}
	public String getValidFor() {
		return validFor;
	}
	public void setValidFor( String validFor ) {
		this.validFor = validFor;
	}
	public String getDesigner() {
		return designer;
	}
	public void setDesigner( String designer ) {
		this.designer = designer;
	}
	public int getTraceable() {
		return traceable;
	}
	public void setTraceable( int traceable ) {
		this.traceable = traceable;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject( String subject ) {
		this.subject = subject;
	}
	public int getIsApproved() {
		return isApproved;
	}
	public void setIsApproved( int isApproved ) {
		this.isApproved = isApproved;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover( String approver ) {
		this.approver = approver;
	}
	public String getAutomationOwner() {
		return automationOwner;
	}
	public void setAutomationOwner( String automationOwner ) {
		this.automationOwner = automationOwner;
	}
	public String getTestType() {
		return testType;
	}
	public void setTestType( String testType ) {
		this.testType = testType;
	}
	public String getAutomationState() {
		return automationState;
	}
	public void setAutomationState( String automationState ) {
		this.automationState = automationState;
	}
	public String getComments() {
		return comments;
	}
	public void setComments( String comments ) {
		this.comments = comments;
	}
	public int getErrorId() {
		return errorId;
	}
	public void setErrorId( int errorId ) {
		this.errorId = errorId;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition( String condition ) {
		this.condition = condition;
	}
	public String getSrtLevel() {
		return srtLevel;
	}
	public void setSrtLevel( String srtLevel ) {
		this.srtLevel = srtLevel;
	}
	public String getType() {
		return type;
	}
	public void setType( String type ) {
		this.type = type;
	}
	
	public int getQcId() {
		return qcId;
	}
	public void setQcId( int qcId ) {
		this.qcId = qcId;
	}
	@Override
	public String toString() {
		return CommonUtils.toJson( this );
	}
}
