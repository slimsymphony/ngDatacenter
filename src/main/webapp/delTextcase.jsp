<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%><%@page import="com.nokia.granite.analyzer.*"%><%@page import="com.nokia.test.statistic.*"%><%@page import="com.nokia.test.casedesign.*"%><%@page import="java.util.*"%><%
int id = CommonUtils.parseInt(request.getParameter("id"),0);
if(CaseDesignManager.deleteTextCase(id)){
	OperationRecord op = new OperationRecord();
	try{
		LDAPAuthenticator auth = ( LDAPAuthenticator ) session.getAttribute( "auth" );
		String currUser = "";
		if ( auth != null ) {
			currUser = auth.getNoe();
		}
		op.setUser(currUser);
		op.setOperation_type(OperationRecord.Operation.DEL);
		op.setRel_caseId(id);
		CaseDesignManager.addOperationRecord(op);
	}catch(Exception ex){
		LogUtils.getDesignLog().error("Del Operation Record failed:"+op.toString(), ex);
	}
	out.println("true");
}else{
	out.println("Failed");
}
%>