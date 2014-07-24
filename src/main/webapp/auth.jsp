<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%><%@page import="com.nokia.granite.analyzer.*"%><%@page import="com.nokia.test.casedesign.*"%><%@page import="java.util.*"%><%
	String user = request.getParameter("username");
	String password = request.getParameter("password");
	LDAPAuthenticator la =  new LDAPAuthenticator();
	if(user.indexOf("@")>0){
		user = LDAPAuthenticator.getNoeFromMail(user,LDAPAuthenticator.LDAP_AREA);	
	}
	boolean isAuth = false;
	try{
		isAuth = la.authenticateUser(user,password);
		if(isAuth){
			session.setAttribute("auth",la);
			CaseDesignManager.checkExist(la);
			OperationRecord op = new OperationRecord();
			try{
				op.setUser(user);
				op.setOperation_type(OperationRecord.Operation.LOGIN);
				op.setRel_caseId(0);
				CaseDesignManager.addOperationRecord(op);
			}catch(Exception ex){
				LogUtils.getDesignLog().error("Login Operation Record failed:"+op.toString(), ex);
			}
		}
		out.print("true");
	}catch(Exception e){
		out.print(e.getMessage());
	}
	
%>