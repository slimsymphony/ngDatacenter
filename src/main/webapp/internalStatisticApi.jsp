<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		#title{
			text-align : center;
			font-size: 30px;
		}
		td{
			text-align : center;
		}
	</style>
</head>
<body>
<%
	String start = request.getParameter("start");
	String end = request.getParameter("end");
	String project = request.getParameter("project");
	String branch = request.getParameter("branch");
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	Timestamp s = null;
	Timestamp e = null;
	if(start!=null&&!start.trim().isEmpty()){
		s = new Timestamp(sdf.parse(start).getTime());
	}
	if(end!=null&&!end.trim().isEmpty()){
		e = new Timestamp(sdf.parse(end).getTime()+(long)1000*60*60*24);
	}
	QueryResult qr = ScvInfoManager.getByTimeIntervalStatic(project,branch,s,e);
	int totalCommits = 0;
	int totalCases = 0;
	int totalCaseAdded = 0;
	int totalCaseUpdated = 0;
	if(qr.getScvInfos()!=null)
		totalCommits = qr.getScvInfos().size();
	if(qr.getCases()!=null){
		if(qr.getCases().get( "Add" )!=null)
			totalCaseAdded = qr.getCases().get( "Add" ).size();
		if(qr.getCases().get( "Update" )!=null)
			totalCaseUpdated = qr.getCases().get( "Update" ).size();
	}
	totalCases = totalCaseAdded + totalCaseUpdated;	
		
		
	
	
	out.println("<div id='title'>Total Commits:"+totalCommits +",totalCases:"+totalCases+",totalCaseAdded:"+totalCaseAdded+",totalCaseUpdated:"+totalCaseUpdated+"</div><br/>");
	if(totalCaseAdded>0){
		out.println("Add Cases:");
		out.println("<table border='1' align='center' width='70%'>");
		out.println("<tr><th>Case</th></tr>");
		for(String str : qr.getCases().get( "Add" ).values()){
			out.println("<tr><td>"+str+"</td></tr>");
		}
		out.println("</table>");
		out.println("<hr/>");
	}
	
	if(totalCaseUpdated>0){
		out.println("Updates Cases:");
		out.println("<table border='1' align='center' width='70%'>");
		out.println("<tr><th>Case</th></tr>");
		for(String str : qr.getCases().get( "Update" ).values()){
			out.println("<tr><td>"+str+"</td></tr>");
		}
		out.println("</table>");
		out.println("<hr/>");
	}
	out.println("<table border='1' align='center' width='90%'>");
	out.println("<tr><th>TIME</th><th>TYPE</th><th>COMMITOR</th><th>SUBJECT</th><th>BRANCH</th><th>PATCHSET</th><th>CASE_CNT</th><th>URL</th><th>CASES</th><th>RESULT</th></tr>");
	if(qr.getScvInfos()!=null){
		for(ScvInfo si : qr.getScvInfos().values()){
			out.print("<tr title='"+si+"'>");
			out.print("<td>"+si.getTime()+"</td>");
			out.print("<td>"+si.getType()+"</td>");
			out.print("<td>"+si.getCommitor()+"</td>");
			out.print("<td>"+si.getSubject()+"</td>");
			out.print("<td>"+si.getBranch()+"</td>");
			out.print("<td>"+si.getGerritId()+"</td>");
			out.print("<td>"+si.getImpactCaseCnt()+"</td>");
			out.print("<td><a href='#' onclick='window.open(\""+si.getUrl()+"\")'>"+si.getUrl()+"</a></td>");
			out.print("<td><ol>");
			for(String str: si.getCases()){
				out.print("<li>"+str+"</li>");
			}
			out.print("<ol></td>");
			out.print("<td>"+si.getResult().name()+"</td>");
			out.println("</tr>");
		}
	}
	out.println("</table>");
%>
</body>
</html>