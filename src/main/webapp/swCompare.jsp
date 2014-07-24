<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%
	String sw = request.getParameter( "sw" );
	String tsName = request.getParameter("tsName");
	List<TestExecution> tes = StatisticManager.getExecutionInfosBySw( sw, tsName );
%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		#title{
			text-align : center;
			font-size: 30px;
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
	</style>
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
	<script type="text/javascript" src="js/highcharts.js"></script>
	<script type="text/javascript" src="js/modules/exporting.js"></script>
</head>
<body>
<script type="text/javascript">
		$(function () {
		    var chart;
		    $(document).ready(function() {
		        chart = new Highcharts.Chart({
		            chart: {
		                renderTo: 'container',
		                type: 'column',
		                margin: [ 50, 50, 100, 80]
		            },
		            title: {
		                text: 'Passrate comparation for SW:<%=sw%>'
		            },
		            xAxis: {
		                categories: [
						<%for(TestExecution te : tes) {%>
							'<%=te.getName()%>',
						<%}%>
		                ],
		                labels: {
		                    rotation: -35,
		                    align: 'right',
		                    style: {
		                        fontSize: '13px',
		                        fontFamily: 'Verdana, sans-serif'
		                    }
		                }
		            },
		            yAxis: {
		                min: 0,
		                title: {
		                    text: 'Execution Pass rate'
		                }
		            },
		            legend: {
		                enabled: true
		            },
		            tooltip: {
		                formatter: function() {
		                    return '<b>'+ this.x +'</b><br/>'+
		                        'Pass rate: '+ Highcharts.numberFormat(this.y, 0)+" %";
		                }
		            },
		            series: [{
		                name: 'Updated',
		                data: [
							<%for(TestExecution te : tes) {%>
							<%=te.getPassRate()*100%>,
							<%}%>
		                       ],
		                dataLabels: {
		                    enabled: true,
		                    rotation: 0,
		                    percentageDecimals: 0,
		                    color: '#FFFFFF',
		                    align: 'center',
		                    x: 4,
		                    y: 30,
		                    style: {
		                        fontSize: '13px',
		                        fontFamily: 'Verdana, sans-serif'
		                    }
		                }
		            },
		            {
		                name: 'Original',
		                data: [
							<%for(TestExecution te : tes) {%>
							<%=te.getOriPassRate()*100%>,
							<%}%>
		                       ],
		                dataLabels: {
		                    enabled: true,
		                    rotation: 0,
		                    percentageDecimals: 0,
		                    color: '#FFFFFF',
		                    align: 'center',
		                    x: 4,
		                    y: 30,
		                    style: {
		                        fontSize: '13px',
		                        fontFamily: 'Verdana, sans-serif'
		                    }
		                }
		            }
		            ]
		        });
		    });
		    
		});
		</script>
		<div id="container" style="min-width: 500px; height: 700px; margin: 0 auto">
		</div>
		<hr/>
		<table align="center">
			<tr><th colspan="4">Execution Results for <%=sw %></th></tr>
			<tr>
				<th nowrap>Execution name</th>
				<th nowrap>Execution time</th>
				<th nowrap>Original Pass rate</th>
				<th nowrap>Updated Pass rate</th>
			</tr>
			<tr>
				<th colspan="4"><hr/></th>
			</tr>
			
		<%for(TestExecution te : tes) {%>
			<tr style="cursor:pointer;" onclick="window.open('executionReport2.jsp?teId=<%=te.getId()%>');">
				<td><%=te.getName() %></td>
				<td><%=te.getExecTime() %></td>
				<td><%=te.getOriPassRate() %></td>
				<td><%=te.getPassRate() %></td>
			</tr>
		<%} %>
			
		</table>
</body>
</html>