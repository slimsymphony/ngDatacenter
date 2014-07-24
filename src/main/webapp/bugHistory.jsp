<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.*"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%
	response.setHeader("Pragma","no-cache"); 
	response.setHeader("Cache-Control","no-cache"); 
	response.setDateHeader("Expires", 0); 
	Map<Object[],Map<Integer,String>> bugs =  null;
	String product = request.getParameter("product");
	int swErrorOnly = CommonUtils.parseInt(request.getParameter("swErrorOnly"),1);
	if(swErrorOnly==1){
		if(product==null||product.trim().isEmpty()){
			bugs = StatisticManager.getBugInfos(true);
		}else{
			bugs = StatisticManager.getBugInfosByProduct(true,product);
		}
	}else{
		if(product==null||product.trim().isEmpty()){
			bugs = StatisticManager.getBugInfos(false);
		}else{
			bugs = StatisticManager.getBugInfosByProduct(false,product);
		}
	}
	Connection conn = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	try{
		conn = CommonUtils.getConnection();
		ps = conn.prepareStatement("select product from stat_testcases where id=?");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Bug history</title>
<link rel="stylesheet" type="text/css"
	href="css/cupertino/jquery-ui-1.8.22.custom.css" />
<style type="text/css">
body {
	text-align: center;
}

div {
	text-align: center;
}

table {
	text-align: center;
}

th {
	text-align: center;
	border: 1px dashed;
}

td {
	text-align: left;
	border: 1px dashed;
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
</style>
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
</head>
<body>
<br/>
<h2>Bug Track history</h2>
<table style="width:90%;border-style:solid;border-width:2px;">
	<tr>
		<th>BugID</th>
		<th>BugInfo</th>
		<th>Occurrence</th>
		<th>Related Test Cases</th>
	</tr>
	<%for(Map.Entry<Object[],Map<Integer,String>> bug : bugs.entrySet()){
		int bugId = (Integer)bug.getKey()[0];
		String bugInfo = (String)bug.getKey()[1];
		int occurrence = (Integer)bug.getKey()[2];
		Map<Integer,String> cases =  bug.getValue();
	%>
	<tr>
		<td><a href='javascript:void(0)' onclick='window.open("https://mzilla.nokia.com/show_bug.cgi?id=<%=bugId %>")'><%=bugId %></a></td>
		<td><a href='javascript:void(0)' onclick='window.open("https://mzilla.nokia.com/show_bug.cgi?id=<%=bugId %>")'><%=bugInfo %></a></td>
		<td style="text-align:center"><%= occurrence%></td>
		<td>
		<ul><%for(Map.Entry<Integer,String> entry : cases.entrySet()){
			ps.clearParameters();
			ps.setInt(1,entry.getKey());
			rs = ps.executeQuery();
			rs.next();
			String productV = rs.getString(1);
			CommonUtils.closeQuitely(rs);
		%>
			<li><a href='javascript:void(0)' onclick='window.open("viewTestCase.jsp?id=<%=entry.getKey()%>")'>[<%=productV %>]<%=entry.getValue()%></a></li>
			<%} %>
		</ul>
		</td>
	</tr>
	<%} %>
<%}catch(Exception e) {
		LogUtils.getStatLog().error("Fetch product for testcase failed.",e);
  }finally{
	  CommonUtils.closeQuitely(ps);
	  CommonUtils.closeQuitely(conn);
  }%>
</table>
</body>
</html>