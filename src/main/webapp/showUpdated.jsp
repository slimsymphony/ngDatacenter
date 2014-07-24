<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.*"%>
<%@page import="java.text.*"%>
<%
String product = request.getParameter("product");
String start = request.getParameter("start");
String end = request.getParameter("end");
String mode = request.getParameter("mode");
int tsId = CommonUtils.parseInt(request.getParameter("tsId"),0);
Testset ts = StatisticManager.getTestsetByIdOnly(tsId);
SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
Timestamp s = null;
Timestamp e = null;
if(start!=null&&!start.trim().isEmpty()){
	s = new Timestamp(sdf.parse(start).getTime());
}
if(end!=null&&!end.trim().isEmpty()){
	e = new Timestamp(sdf.parse(end).getTime()+(long)1000*60*60*24);
}
Connection conn = null;
PreparedStatement ps = null;
ResultSet rs = null;
try{
	conn = CommonUtils.getConnection();
	String sql = "select stat_results.caseid, casename, count(execId) cnt from stat_results left join stat_testcases on stat_results.caseid=stat_testcases.id where oriresult='FAIL' and result='PASS' and product=? and execId in (select id from stat_executions where (exec_time between ? and ?) and testsetid=? ";
	if( mode!=null && !mode.trim().isEmpty( )){
		sql +=" and type=?";
	}
	sql += ") group by stat_results.caseid order by cnt desc";
	ps = conn.prepareStatement(sql);
	ps.setString(1,product);
	ps.setTimestamp(2,s);
	ps.setTimestamp(3,e);
	ps.setInt(4,tsId);
	if( mode!=null && !mode.trim().isEmpty( )){
		ps.setString(5,mode);
	}
	rs = ps.executeQuery();

%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		body{
			font-family: Calibri;
			font-size: 11pt;
		}
		td{
			padding : 0px;
			margin: 0px;
			width: 90px;
		}
	</style>
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
	<script type="text/javascript">
	$(function(){
	});
	</script>
</head>
<body>
<h3>Most maintain efforts costs on <%=product%> from <%=s %> to <%=e %> in TestSet[<%=ts.getName() %>]</h3>
<table style="width:80%;" border="1px">
	<tr>
		<th>TestCase</th><th>Maintain Times</th>
	</tr>
<%		while(rs.next()){%>
	<tr>
		<td style="text-align:left"><a href='viewTestCase.jsp?id=<%=rs.getInt(1)%>'><%= rs.getString(2)%></a></td><td style="text-align:left"><%= rs.getInt(3)%></td>
	</tr>
<%		}
%>	
	
</table>
</body>
</html>
<%
}catch(Exception ex){
	LogUtils.getStatLog().error("Get maintain effort top cases failed.", ex);
}finally{
	CommonUtils.closeQuitely(rs);
	CommonUtils.closeQuitely(ps);
	CommonUtils.closeQuitely(conn);
}
%>