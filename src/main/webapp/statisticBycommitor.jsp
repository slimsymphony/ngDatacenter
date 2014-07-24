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
	Map<String,int[]> qr = ScvInfoManager.statByCommitor(project,branch,s,e);
	
	out.println("<table border='1' align='center' width='90%'>");
	out.println("<tr><th>COMMITOR</th><th>ADD_CASE_CNT</th><th>UPDATED_CASE_CNT</th></tr>");
	if(qr!=null&&!qr.isEmpty()){
		for(Map.Entry<String,int[]> entry: qr.entrySet()){
			out.print("<tr>");
			out.print("<td>"+entry.getKey()+"</td>");
			out.print("<td>"+entry.getValue()[0]+"</td>");
			out.print("<td>"+entry.getValue()[1]+"</td>");
			out.println("</tr>");
		}
	}
	out.println("</table>");
%>
</body>
</html>