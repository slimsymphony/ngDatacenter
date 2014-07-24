<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%! String notNull(String str){
	if(str==null)
		return "";
	else
		return str;
} %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		#title{
			text-align : center;
			font-size: 30px;
		}
		table{
			width: 90%;
			height: 60%
			text-align : center;
		}
		td{
			text-align : center;
		}
	</style>
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
	<script type="text/javascript">
		function check(id){
			if(id>0){
				window.open("updateTeResult.jsp?teId="+id);
			}
		}
		
		function report(id){
			if(id>0){
				window.open("executionReport2.jsp?teId="+id);
			}
		}
		
	</script>
</head>
<body>
<%
String type= request.getParameter("type");
if(type==null||type.trim().isEmpty()){
	return;
}
if(type.equals("ts")){
	String product = request.getParameter("product");
	String start = request.getParameter("start");
	String end = request.getParameter("end");
	String mode = request.getParameter("mode");
	if(mode==null)
		mode = "";
	int tsId = CommonUtils.parseInt(request.getParameter("tsId"),0);
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	Timestamp s = null;
	Timestamp e = null;
	if(start!=null&&!start.trim().isEmpty()){
		s = new Timestamp(sdf.parse(start).getTime());
	}
	if(end!=null&&!end.trim().isEmpty()){
		e = new Timestamp(sdf.parse(end).getTime()+(long)1000*60*60*24);
	}
	List<TestExecution> execs = StatisticManager.getExecutionsByTestset( product, tsId, mode, s, e );
%>
	<table>
		<tr>
			<th>Execution Name</th>
			<th>Execution Time</th>
			<th>Source SW</th>
			<th>Target SW</th>
			<th>Execution Type</th>
			<th>URL</th>
			<th>Operation</th>
		</tr>
		<tr><td colspan="6"><hr/></td></tr>
<% for(TestExecution te:execs) {%>	
		<tr>
			<td><%=te.getName() %></td>
			<td><%=te.getExecTime() %></td>
			<td><%=notNull(te.getFrom()) %></td>
			<td><%=te.getSw() %></td>
			<td><%=te.getType() %></td>
			<td><%if(te.getUrl()!=null&&te.getUrl().indexOf("artifact")>0){%><a href='javascript:void(0)' onclick="window.open('<%=te.getUrl().substring(0,te.getUrl().indexOf("artifact"))%>')"><%=te.getUrl().substring(0,te.getUrl().indexOf("artifact"))%></a><%}%></td>
			<td>
				<button onclick="check(<%=te.getId() %>)">Select</button>
				<button onclick="report(<%=te.getId() %>)">Get Report</button>
			</td>
		</tr>
<%}%>	
	</table>
<%
}else if(type.equals("case")){
	
}
%>
</body>
</html>