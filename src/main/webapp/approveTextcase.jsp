<%@page import="com.nokia.granite.analyzer.*"%><%@page import="com.nokia.test.casedesign.*"%><%
int id = CommonUtils.parseInt(request.getParameter("id"),0);
int result = CommonUtils.parseInt(request.getParameter("result"),0);
String comments = request.getParameter("comments");
String user = request.getParameter("user");
if(CaseDesignManager.approveTextCase(id, result, user, comments)){
	OperationRecord op = new OperationRecord();
	try{
		LDAPAuthenticator auth = ( LDAPAuthenticator ) session.getAttribute( "auth" );
		String currUser = "";
		if ( auth != null ) {
			currUser = auth.getNoe();
		}
		op.setUser(currUser);
		op.setOperation_type(OperationRecord.Operation.APPROVE);
		op.setRel_caseId(id);
		CaseDesignManager.addOperationRecord(op);
	}catch(Exception ex){
		LogUtils.getDesignLog().error("Approve Operation Record failed:"+op.toString(), ex);
	}
	out.print("true");
}else{
	out.print("Approve Failed");
}
%>