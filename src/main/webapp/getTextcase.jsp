<%@page import="com.nokia.granite.analyzer.*"%><%@page import="com.nokia.test.casedesign.*"%><%
int id = CommonUtils.parseInt(request.getParameter("id"),0);
if(id==0){
	out.print("Fail[ Invalid caseID: "+ id +" ]");
	return;
}
TextCase tc = CaseDesignManager.getTextCaseById(id);
if(tc == null){
	out.print("Fail[ can't find case: "+ id +" ]");
}else{
	out.print(tc.toString());
}
%>