<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%
	List<Product> products  = StatisticManager.getProducts( false );
	Map<String,List<Testset>> sss  = new TreeMap<String,List<Testset>>();
	for(Product p : products){
		sss.put(p.getName(),StatisticManager.getTestsetsOnly(0,0,null,p.getName()));
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta charset="utf-8"> 
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
<title>Test Case Status</title>
<link rel="stylesheet" type="text/css" href="css/cupertino/jquery-ui-1.8.22.custom.css" />
	<style type="text/css">
		body{
			text-align : center;
		}
		div{
			text-align : center;
		}
		table{
			text-align : center;
		}
		.hid{
			display : none;
		}
		th{
			text-align : center;
		}
		td{
			text-align : center;
		}
		#caption{
			font-size: 24pt;
		}
		#power{
			font-size: 15pt;
		}
		#resultFrame{
			width : 95%;
			height: 750px;
		}
	</style>
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
	<script type="text/javascript">
	$(function(){
		var sss = [];
		//var dstss = [];
		//var sstss = [];
<%	for(Map.Entry<String,List<Testset>> entry:sss.entrySet()){
		String pn = entry.getKey();
		List<Testset> tss = entry.getValue();%>
		sss['<%=pn%>'] = [];
<%		for(Testset ts : tss){%>
		sss['<%=pn%>'][<%=ts.getId()%>] = '<%=ts.getName()%>';
		<%}
}%>
		
		$('#product').change(function(){
			$('#tss').empty();
			for(var ts in sss[$(this).val()])
				$('#tss').append( new Option(sss[$(this).val()][ts],ts) );
		});
		
		$('#selType').change(function(){
			if($(this).val()=='0'){
				$('#tsId').addClass("hid");
			}else{
				$('#tsId').removeClass("hid");
			}
		});
		$('#product').change();
		$('#selType').change();
		$('#go').click(function(){
			var url = "caseExpress.jsp?product="+$('#product').val();
			if($('#selType').val()=='1')
				url += "&tsId="+$('#tss').val();
			$( "#resultFrame" ).attr('src',url );
		});
	});
	</script>
	</head>
<body>
	<div id="title-info">
		<p id="caption">WelCome to MP S40 CI Automation Testing Data Center!</p>
	</div>
	<div id="condition">
		<table align="center">
			<tr>
				<th>Product:<select id="product"><%for(Product p : products){ %><option><%=p.getName() %></option><%} %></select></th>
				<th>Type: <select id="selType"><option value="0">All</option><option value="1">By TestSet</option></select></th>
				<th id="tsId">
					<label>TestSet</label><select id="tss"></select>
				</th>
				<th>
					<button id="go">GO</button>&nbsp;&nbsp;
				</th>
			</tr>
			<tr>
				<th colspan="5" align="center"><a href="index.jsp">Back to Data Center Home</a> </th>
			</tr>
		</table>
	</div>
	<div id="result">
		<iframe id="resultFrame" src=""></iframe>
	</div>
	<div id="tail">
		<p id="power">&copy;powered by Nokia SW Test Beijing Tools</p>
	</div>
</body>
</html>