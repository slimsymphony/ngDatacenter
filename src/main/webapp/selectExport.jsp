<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%
List<Product> products  = StatisticManager.getProducts( false );
Map<String,Map<String,Map<Integer,String>>> sss = new LinkedHashMap<String,Map<String,Map<Integer,String>>>();
for(Product p : products){
	sss.put(p.getName(), StatisticManager.getSwVersionsByProduct(p.getName()));
}
//Map<String,Map<Integer,String>> swsDs = StatisticManager.getSwVersionsByProduct("aquaDS");
//Map<String,Map<Integer,String>> swsSs = StatisticManager.getSwVersionsByProduct("aquaSS");
%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
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
			width : 90%;
			height: 700px;
			text-align:center;
		}
	</style>
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
	<script type="text/javascript">
		$(function(){
			var pss = [];
			<%
			int n=0;
			for(Map.Entry<String,Map<String,Map<Integer,String>>> entry: sss.entrySet()){%>
			pss['<%=entry.getKey()%>'] = [];
			<%	for(Map.Entry<String,Map<Integer,String>> en : entry.getValue().entrySet() ){%>
			pss['<%=entry.getKey()%>']['<%=en.getKey()%>']  =[];
			<% 		for(Map.Entry<Integer,String> en2 : en.getValue().entrySet()){ %>
			pss['<%=entry.getKey()%>']['<%=en.getKey()%>']['<%=en2.getValue()%>'] = '<%=en2.getKey()%>';	
			<%		}
				}
			}%>
			
			
			$('#product').change(function(){
				$('#ts').empty();
				for(v in pss[$(this).val()])
					$('#ts').append( new Option(v,v) );
				$('#ts').change();
			});
			
			$('#ts').change(function(){
				var val = $(this).val();
				$('#sw').empty();
				for(v in pss[$('#product').val()][$(this).val()]){
					$('#sw').append( new Option(v,pss[$('#product').val()][$(this).val()][v]) );
				}
			});
			
			$('#go').click(function(){
				$( "#resultFrame" ).attr('src',"executionReport2.jsp?teId="+$('#sw').val() );
			});
			
			$('#product').change();
			$('#ts').change();
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
				<th>TestSet:<select id="ts"></select></th>
				<th>SW Version:<select id="sw"></select></th>
				<th>
					<button id="go">GO</button>&nbsp;
				</th>
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