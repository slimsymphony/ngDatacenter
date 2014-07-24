<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%
	//List<Testset> dsSets =  StatisticManager.getTestsets(0,0,null,"aquaDS");
	//List<Testset> ssSets =  StatisticManager.getTestsets(0,0,null,"aquaSS");
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
<title>Data Maintain</title>
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
		var Testset = function(id,name){
			this.id = id;
			this.name = name;
		}
		var sss = [];
		<%for(Map.Entry<String,List<Testset>> entry:sss.entrySet()){%>
		sss['<%=entry.getKey()%>'] = [];
		<%	for(int i=0;i<entry.getValue().size();i++){%>
		sss['<%=entry.getKey()%>'][<%=i%>] = new Testset(<%=entry.getValue().get(i).getId()%>,'<%=entry.getValue().get(i).getName()%>');
		<%	}%>
		<%}%>
		
		$(function() {
			$( "#start" ).datepicker();
			$( "#end" ).datepicker();
			$('#product').change(function(){
				$('#tss').empty();
				for(var ts in sss[$(this).val()])
					$('#tss').append( new Option(sss[$(this).val()][ts].name,sss[$(this).val()][ts].id) );
				
			});
			
			$("#go").click(function(){
				var url = "list.jsp?type=ts&product="+$('#product').val()+"&start="+$( "#start" ).val()+"&end="+$( "#end" ).val()+ "&tsId="+$("#tss").val()+"&mode="+$('#mode').val();
				$( "#resultFrame" ).attr('src',url );
			});
			
			
			$('#compare').click(function(){
				var url = "resultCompare.jsp?product="+$('#product').val()+"&start="+$( "#start" ).val()+"&end="+$( "#end" ).val()+ "&tsId="+$("#tss").val()+"&mode="+$('#mode').val();
				window.open(url);
			});
			
			$('#list').click(function(){
				var url = "showUpdated.jsp?product="+$('#product').val()+"&start="+$( "#start" ).val()+"&end="+$( "#end" ).val()+ "&tsId="+$("#tss").val()+"&mode="+$('#mode').val();
				window.open(url);
			});
			
			for(var ts in sss[$('#product').val()])
				$('#tss').append( new Option(sss[$('#product').val()][ts].name,sss[$('#product').val()][ts].id) );
			
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
				
				<th id="tsId">
					<label>TestSet</label><select id="tss"></select>
				</th>
				<th>
					<label>Mode</label> <select id="mode"><option value="">All</option><option value="weekly">weekly</option><option value="daily">daily</option></select>
				</th>
				<th>Start:<input id="start" type="text"/></th>
				<th>End:<input id="end" type="text"/></th>
				<th>
					<button id="go">GO</button>&nbsp;&nbsp;
					<button id="compare">Compare!</button>
					<button id="list">List!</button>
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