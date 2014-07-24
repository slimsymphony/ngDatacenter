<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="org.joda.time.Duration" %>
<%
	String product = request.getParameter("product");
	int tsId = CommonUtils.parseInt(request.getParameter("tsId"),0);
	String start = request.getParameter("start");
	String end = request.getParameter("end");
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	Timestamp s = null;
	Timestamp e = null;
	if(start!=null&&!start.trim().isEmpty()){
		s = new Timestamp(sdf.parse(start).getTime());
	}
	if(end!=null&&!end.trim().isEmpty()){
		e = new Timestamp(sdf.parse(end).getTime()+(long)1000*60*60*24);
	}
	String tsName = null;
	Map<TestCase,Integer> cases = null;
	int topCount = CommonUtils.parseInt(request.getParameter("max"),20);
	if(tsId==0){
		cases = StatisticManager.getTopDurationTestcases(product, topCount,s,e);
	}else{
		tsName = StatisticManager.getTestsetByIdOnly(tsId).getName();
		cases = StatisticManager.getTopDurationTestcasesInTestset(tsId, topCount,s, e);
	}
	
%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta charset="utf-8"> 
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript">
		$(function(){
			
		});
		function switchBtn(i){
			if($('#div'+i).attr('class')=='hide'){
				$('#div'+i).removeClass("hide");
			}else{
				$('#div'+i).addClass("hide");
			}
		}
	</script>
	<style type="text/css">
		.hide{
			display:none;
		}
	</style>
</head>
<body>
<%if(tsId>0) {%><h2>Testcases of [<%=tsName%>]</h2><%}%>
<h3>Time Consuming top <%=topCount %></h3>
<table>
<tr>
	<th>Duration (Seconds)</th>
	<th>Name</th>
	<th>FeatureGroup</th>
	<th>Feature</th>
</tr>
<%if(cases!=null){
	for(TestCase tc:cases.keySet()){
%>
<tr>
	<td><%=cases.get(tc)%> - about <%= Duration.standardSeconds( cases.get(tc) ).getStandardMinutes() %>min</td>
	<td><%=tc.getCaseName()%></td>
	<td><%=tc.getFeatureGroup()%></td>
	<td><%=tc.getFeature()%></td>
</tr>
<%	}
} 
%>
</table>
</body>
</html>