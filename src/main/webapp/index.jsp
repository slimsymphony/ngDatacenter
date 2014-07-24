<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%
	List<Project> projects = ScvInfoManager.getProjects( false, true );
	List<Product> products = StatisticManager.getProducts(false);
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8"> 
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width, initial-scale=1.0"> 
	<title>WelCome to MP S40 CI Automation Testing Data Center!</title>
	<link rel="stylesheet" type="text/css" href="css/cupertino/jquery-ui-1.8.22.custom.css" />
	<link rel="stylesheet" type="text/css" href="css/introjs.min.css" />
	<link rel="stylesheet" type="text/css" href="css/introjs-rtl.min.css" />
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
			width : 90%;
			height: 700px;
		}
	</style>
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
	<script type="text/javascript" src="js/intro.min.js"></script>
	<script type="text/javascript">
		var products = new Array();
		<%int v =0;for(Product p:products){%>
		products[<%=v++%>] = '<%=p.getName()%>';
		<%}%>
		$(function() {
			$( "#start" ).datepicker();
			$( "#end" ).datepicker();
			$("#go").click(function(){
				var url = "internalApi.jsp?project="+$('#project').val()+"&branch="+$('#branch').val()+"&start="+$( "#start" ).val()+"&end="+$( "#end" ).val();
				$( "#resultFrame" ).attr('src',url );
			});
			$('#statistic').click(function(){
				var url = "internalStatisticApi.jsp?project="+$('#project').val()+"&branch="+$('#branch').val()+"&start="+$( "#start" ).val()+"&end="+$( "#end" ).val();
				$( "#resultFrame" ).attr('src',url );
			});
			
			$('#statisticBc').click(function(){
				var url = "statisticBycommitor.jsp?project="+$('#project').val()+"&branch="+$('#branch').val()+"&start="+$( "#start" ).val()+"&end="+$( "#end" ).val();
				$( "#resultFrame" ).attr('src',url );
			});
			
			$('#weeklystat').click(function(){
				var url = "weeklyStatistic.jsp?project="+$('#project').val()+"&start="+$( "#start" ).val()+"&end="+$( "#end" ).val();
				$( "#resultFrame" ).attr('src',url );
			});
			$('#bugBtn').click(function(){
				$("#bugForm").dialog("open");
			});
			var projects = [];
			var branches = [];
<%	for(Project p : projects){%>
			 projects['<%=p.getName()%>'] = '<%=p.getName()%>';
			 branches['<%=p.getName()%>'] = [<%for(Branch b : p.getBranches()){out.print("'"+b.getName()+"',");}%>];
<%}%>	
			for(pid in projects){
				$("#project").append( new Option(projects[pid],pid) );
				for(bn in branches[pid]){
					$("#branch").append( new Option(branches[pid][bn],branches[pid][bn]) );
				}
			}
			$("#project").change(function(){
				$("#branch").empty();
				for(bn in branches[$(this).val()]){
					$("#branch").append( new Option(branches[$(this).val()][bn],branches[$(this).val()][bn]) );
				}
			});
			
			$("#bugForm").dialog(
					{
						autoOpen : false,
						show : "fold",
						hide : "explode",
						height : 250,
						width : 650,
						modal : true,
						buttons : {
							"OK" : function(){
								$("#bugForm").dialog("close");
								var url = 'bugHistory.jsp?swErrorOnly='+$('#swErrorOnly').val()+"&product="+$("#product").val();
								window.open(url);
							},
							"Cancel" : function() {
								$("#bugForm").dialog("close");
							}
						}
					});
			function initSel(data,obj){
				for(var i=0;i<data.length;i++){
					obj.append( new Option(data[i],data[i]));
				}
			}
			initSel(products,$('#product'));
			introJs().start();
		});
	</script>
</head>
<body>
	<center>
	<div id="title-info" style="width:900px" data-step="1" data-intro="Welcome to DataCenter" data-position='bottom'>
		<p id="caption"><a href='javascript:void(0)' onclick='introJs().start();'>WelCome</a> to MP S40CI Automation Testing Data Center<a href="flappy.html">!</a></p>
	</div>
	</center>
	<div id="condition" data-step="9" data-intro="Enjoy your usage!">
		<table align="center">
			<tr data-step="2" data-intro="This area provide conditions for check scriptors's commits history and metrics">
				<th>Project:<select id="project"></select></th>
				<th>Branch:<select id="branch"></select></th>
				<th>Start:<input id="start" type="text"/></th>
				<th>End:<input id="end" type="text"/></th>
				<th>
					<button id="go">GO</button>&nbsp;
					<button id="statistic">statistic</button>&nbsp;
					<button id="statisticBc">statistic By commitor</button>&nbsp;
				</th>
				<th><button id="weeklystat">weekly statistic</button></th>
			</tr>
			<tr>
				<th data-step="3" data-intro="Click here, go to the statistic view."><a href='statistic.jsp'>Statistics</a></th>
				<th data-step="4" data-intro="View the Execution results Maintain, click here."><a href='maintain.jsp'>Data Maintain</a></th>
				<th data-step="5" data-intro="Go to the Reports view, if you are our stakeholders, please click here."><a href='javascript:void(0)' onclick="window.open('selectExport.jsp');">Report For Stakeholders</a></th>
				<th data-step="6" data-intro="Check the status of all the test cases in record, at once!"><a href='javascript:void(0)' onclick="window.open('caseStat.jsp');">Test Case Status</a></th>
				<th data-step="7" data-intro="Testware design workflow starts here, come on! Eric :)" style="text-align:left"><a href='javascript:void(0)' onclick="window.open('textcaseMain.jsp');">Testware design process</a></th>
				<th data-step="8" data-intro="Want to Check the bug Info and statistics? Click here" style="text-align:left"><a id="bugBtn" href='javascript:void(0)'>Bug Tracker</a></th>
			</tr>
		</table>
	</div>
	<div id="result">
		<iframe id="resultFrame" src="world.html"></iframe>
	</div>
	<div id="tail">
		<p id="power">&copy;powered by Nokia SW Test Beijing Tools</p>
	</div>
	<div id="bugForm" title="Bug Track Options">
		<form>
			<fieldset>
				<label for="product">Product</label> <select id="product"><option value="">ALL</option></select>
				<br /> 
				<label for="swErrorOnly">Only Trace SW Error?(Filter Tool Error)</label>
				<select id="swErrorOnly"><option value="1">Yes</option><option value="0">No</option></select>
			</fieldset>
		</form>
	</div>
</body>
</html>