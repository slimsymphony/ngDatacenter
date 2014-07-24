<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="com.nokia.test.casedesign.*"%>
<%@page import="java.util.*"%>
<%
	int id = CommonUtils.parseInt(request.getParameter("id"),0);
	TestCase tc = StatisticManager.getTestCaseById(id);
	List<TestResult> trs = StatisticManager.getCaseExecutionHistory(id);
	Map<Integer, String> bugs = StatisticManager.getCaseBugHistory(id);
	int avg = StatisticManager.getCaseAvgDuration(id);
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Test Case</title>
<link rel="stylesheet" type="text/css" href="css/cupertino/jquery-ui-1.8.22.custom.css" />
<style type="text/css">
body {
	text-align: center;
}

div {
	text-align: center;
}

table {
	text-align: center;
	border: solid 2px;
}

th {
	text-align: center;
	vertical-align:top;
	border: dashed 1px;
	border-bottom: 0px;
	border-left: 0px;
}

td {
	text-align: left;
	word-wrap:break-word; 
	overflow:hidden;
	border: dashed 1px;
	border-bottom: 0px;
	border-left: 0px;
	border-right: 0px;
}

#caption {
	font-size: 24pt;
}

#power {
	font-size: 15pt;
}

#resultFrame {
	width: 95%;
	height: 750px;
}

.hide {
	display: none;
}
.PASS{
	color:green;
}
.NORESULT{
	color:yellow;
}
.FAIL{
	color:red;
}
</style>
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
</head>
<body>
<%if(tc!=null){ %>
<table>
	<tr>
		<th>Case Name</th>
		<td><%=tc.getCaseName() %></td>
	</tr>
	<tr>
		<th>Feature Group</th>
		<td><%=tc.getFeatureGroup() %></td>
	</tr>
	<tr>
		<th>Feature</th>
		<td><%=tc.getFeature() %></td>
	</tr>
	<tr>
		<th>QC Identifier</th>
		<td><%=tc.getQcid() %></td>
	</tr>
	<tr>
		<th>Source Path</th>
		<td><%=tc.getDirectory()+"\\"+tc.getFile()+".py" %></td>
	</tr>
	<tr>
		<th>Class</th>
		<td><%=tc.getTestClass() %></td>
	</tr>
	<tr>
		<th>Method</th>
		<td><%=tc.getMethod() %></td>
	</tr>
	<tr>
		<th>Average Duration</th>
		<td><%=avg %> seconds</td>
	</tr>
	<tr>
		<th>Bug History</th>
		<td><ul><%for(Map.Entry<Integer,String> entry:bugs.entrySet()){ %>
		<li><a href='javascript:void(0)' onclick='window.open("https://mzilla.nokia.com/show_bug.cgi?id=<%=entry.getKey() %>")'><%=entry.getValue()%></a></li>
		<%} %></ul></td>
	</tr>
	<tr>
		<th>Execution History</th>
		<td><ul><%for(TestResult tr:trs){ %>
			<li>
				RESULT:<span class="<%= tr.getResult()%>"><%= tr.getResult()%></span>,
				Original RESULT:<span class="<%= tr.getOriResult()%>"><%= tr.getOriResult()%></span>, 
				Duration:<%=tr.getDuration()%>,
				<a href='javascript:void(0)' onclick='window.open("http://betstas01.china.nokia.com/scvMonitor/executionReport2.jsp?teId=<%=tr.getExecId()%>")'><%=StatisticManager.getExecutionById(tr.getExecId()).getSw()%></a>,
				<%if(tr.getBugId()>0){%>Bug:<a href='javascript:void(0)' onclick='window.open("https://mzilla.nokia.com/show_bug.cgi?id=<%=tr.getBugId() %>")'><%=tr.getBugInfo() %></a><%} %>
			</li>
			<%} %>
		</ul></td>
	</tr>
</table>
<%}else{ %><h1>Not found assigned TestCase.</h1><%} %>
</body>
</html>