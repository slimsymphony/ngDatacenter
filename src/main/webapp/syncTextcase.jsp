<%@page import="com.nokia.granite.analyzer.*"%><%@page import="com.nokia.test.casedesign.*"%><%
int id = CommonUtils.parseInt(request.getParameter("id"),0);
String password = request.getParameter("password");
String user = request.getParameter("user");
if(CaseDesignManager.syncToQc(id,user,password)){
	OperationRecord op = new OperationRecord();
	try{
		LDAPAuthenticator auth = ( LDAPAuthenticator ) session.getAttribute( "auth" );
		String currUser = "";
		if ( auth != null ) {
			currUser = auth.getNoe();
		}
		op.setUser(currUser);
		op.setOperation_type(OperationRecord.Operation.SYNC);
		op.setRel_caseId(id);
		CaseDesignManager.addOperationRecord(op);
	}catch(Exception ex){
		LogUtils.getDesignLog().error("Sync Operation Record failed:"+op.toString(), ex);
	}
	out.print("true");
}else{
	out.print("Synchronized to QC Failed");
}
%>