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
	List<ScvInfo> infos = ScvInfoManager.getByTimeInterval(project,branch,s,e);
	int totalCommits = infos.size();
	int totalCases = 0;
	int totalCaseAdded = 0;
	int totalCaseUpdated = 0;
	int totalCaseUnknown = 0;
	int totalCommitAdded = 0;
	int totalCommitUpdated = 0;
	int totalCommitUnknown = 0;
	for(ScvInfo si : infos){
		if(si.getType().equals("Add")){ //&& si.getGerritId()==1 && !si.getResult().equals(com.nokia.granite.analyzer.ScvInfo.RESULT.FAILURE)){
			totalCommitAdded++;
			if(si.getCases()!=null){
				totalCaseAdded += si.getCases().size();
				totalCases += si.getCases().size();
			}
		}else if (si.getType().equals("Update") ){//&& si.getGerritId()==1 && !si.getResult().equals(com.nokia.granite.analyzer.ScvInfo.RESULT.FAILURE)){
			totalCommitUpdated++;
			if(si.getCases()!=null){
				totalCaseUpdated += si.getCases().size();
				totalCases += si.getCases().size();
			}
		}else{
			totalCommitUnknown ++;
			if(si.getCases()!=null){
				totalCaseUnknown += si.getCases().size();
				totalCases += si.getCases().size();
			}
		}
	}
	out.println("<div id='title'>Total Commits:"+totalCommits +",Commit Added:"+totalCommitAdded+",Updated:"+totalCommitUpdated+",other:"+totalCommitUnknown+"<br/>Total Cases:"+totalCases+",Added:"+totalCaseAdded+",Updated:"+totalCaseUpdated+",other:"+totalCaseUnknown+"</div><br/>");
	out.println("<table border='1' align='center' width='90%'>");
	out.println("<tr><th>TIME</th><th>TYPE</th><th>COMMITOR</th><th>SUBJECT</th><th>BRANCH</th><th>PATCHSET</th><th>CASE_CNT</th><th>URL</th><th>CASES</th><th>RESULT</th></tr>");
	for(ScvInfo si : infos){
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
	out.println("</table>");
%>
</body>
</html>