<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%
	String product = request.getParameter("product");
	int tsId = CommonUtils.parseInt(request.getParameter("tsId"),0);
	String tsName = null;
	Map<String,List<TestCase>> cases = null;
	int totalCaseCnt = 0;
	if(tsId==0){
		cases = StatisticManager.getAllTestcases(product);
		for(List<TestCase> t : cases.values()){
			totalCaseCnt += t.size();
		}
	}else{
		tsName = StatisticManager.getTestsetByIdOnly(tsId).getName();
		Testset ts  = StatisticManager.getTestsetById(tsId);
		if(ts!=null){
			cases = ts.getTestcases();
			for(List<TestCase> t : cases.values()){
				totalCaseCnt += t.size();
			}
		}
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
<h3>total script Number:<%=totalCaseCnt %></h3>
<table>
<tr>
	<th>Feature Group</th>
	<th>TestCases</th>
</tr>
<%if(cases!=null){
	int i=0;
	for(String fg : cases.keySet()){%>
<tr><td colspan="2"><hr/></td></tr>
<tr>
	<td><%=fg%></td>
	<td>
		<button onclick="switchBtn(<%=i%>)">switch</button>Totally <%= cases.get(fg).size()%> test scripts.
		<div id="div<%=i++%>" class="hide">
			<ul>	 
	<%for(TestCase tc : cases.get(fg)) {%>
				<li><a href='javascript:void(0)' onclick="window.open('viewTestCase.jsp?id=<%=tc.getId()%>')"><%=tc.getCaseName()%></a></li>
	<%}%>
			</ul>
		</div>
	</td>
</tr>
<%	}
} 
%>
</table>
</body>
</html>