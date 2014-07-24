<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%
int execId = CommonUtils.parseInt(request.getParameter("execId"),0);
int caseId = CommonUtils.parseInt(request.getParameter("caseId"),0);
int bugId = CommonUtils.parseInt(request.getParameter("bugId"),0);
String bugInfo = request.getParameter("bugInfo");
if(bugId != 0 ){
	if(caseId>0)
		StatisticManager.bindBugInfo(execId,caseId,bugId,bugInfo);
	else if(caseId==0 && request.getParameter("caseId")!=null){
		String[] carr = request.getParameter("caseId").split(",");
		for(String cas : carr){
			StatisticManager.bindBugInfo(execId,CommonUtils.parseInt(cas,0),bugId,bugInfo);
		}
	}
}
out.println("ok");
%>