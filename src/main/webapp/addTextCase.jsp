<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%><%@page import="com.nokia.granite.analyzer.*"%><%@page import="com.nokia.test.statistic.*"%><%@page import="com.nokia.test.casedesign.*"%><%@page import="java.util.*"%><%
try{
	String designer = request.getParameter("designer");
	String featureGroup = request.getParameter("featureGroup");
	String feature = request.getParameter("feature");
	String validFor = request.getParameter("validFor");
	String automationState = request.getParameter("automationState");
	String name = request.getParameter("name");
	String content = request.getParameter("content");
	String testArea = request.getParameter("testArea");
	String testType = request.getParameter("testType");
	String subject = request.getParameter("subject");
	String srtLevel = request.getParameter("srtLevel");
	String condition = request.getParameter("condition");
	String type = request.getParameter("type");
	int traceable = CommonUtils.parseInt(request.getParameter("traceable"),0);
	String automationOwner=request.getParameter("automationOwner");
	int errorId = CommonUtils.parseInt(request.getParameter("errorId"),0);
	String comments = request.getParameter("comments");
	TextCase tc = new TextCase(); 
	tc.setName(name);
	tc.setDesigner(designer);
	tc.setFeature(feature);
	tc.setFeatureGroup(featureGroup);
	tc.setValidFor(validFor);
	tc.setAutomationState(automationState);
	tc.setContent(content);
	tc.setTestArea(testArea);
	tc.setTestType(testType);
	tc.setSubject(subject);
	tc.setTraceable(traceable);
	tc.setAutomationOwner(automationOwner);
	tc.setErrorId(errorId);
	tc.setComments(comments);
	tc.setSrtLevel(srtLevel);
	tc.setCondition(condition);
	tc.setType(type);
	CaseDesignManager.createTextCase(tc);
	OperationRecord op = new OperationRecord();
	try{
		LDAPAuthenticator auth = ( LDAPAuthenticator ) session.getAttribute( "auth" );
		String currUser = "";
		if ( auth != null ) {
			currUser = auth.getNoe();
		}
		op.setUser(currUser);
		op.setOperation_type(OperationRecord.Operation.ADD);
		op.setRel_caseId(tc.getId());
		CaseDesignManager.addOperationRecord(op);
	}catch(Exception ex){
		LogUtils.getDesignLog().error("Add Operation Record failed:"+op.toString(), ex);
	}
	out.print("true");
}catch(Exception e){
	out.print(e.getMessage());
}%>