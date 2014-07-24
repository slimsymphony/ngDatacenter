<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%@page import="com.google.gson.reflect.*" %>
<%@page import="java.util.concurrent.ConcurrentSkipListSet" %>
<%
	String project = request.getParameter("project");
	String start = request.getParameter("start");
	String end = request.getParameter("end");
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	Timestamp s = null;
	Timestamp e = null;
	if(start!=null&&!start.trim().isEmpty()){
		s = new Timestamp(sdf.parse(start).getTime());
	}else{
		s = CommonUtils.getEarliestTime(project);
	}
	if(end!=null&&!end.trim().isEmpty()){
		e = new Timestamp(sdf.parse(end).getTime()+(long)1000*60*60*24);
	}else{
		e = new Timestamp(System.currentTimeMillis());
	}
	TreeMap<String,int[]> result = new TreeMap<String,int[]>();
	TreeMap<String,Timestamp[]> map = CommonUtils.parseWeeks(s,e);
	for(Map.Entry<String,Timestamp[]> entry : map.entrySet() ){
		result.put(entry.getKey(),ScvInfoManager.getStatByTime( project, entry.getValue()[0], entry.getValue()[1] ));
	}
	List<String> wks = new ArrayList<String>(result.keySet());
	Map<String,Integer> vs = new HashMap<String,Integer>();
	for(int i=0;i<wks.size();i++){
		vs.put(wks.get(i),i);
	}
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
		td{
			text-align : center;
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
			Highcharts.setOptions({
				global: {
					useUTC: false
				}
			});
		   
			var chart;
		    $(document).ready(function() {
		        chart = new Highcharts.Chart({
		            chart: {
		                renderTo: 'container',
		                type: 'column',
		                margin: [ 50, 50, 100, 80]
		            },
		            title: {
		                text: 'Commits/TestCases Statistics'
		            },
		            xAxis: {
		            	categories: [<% for(String wk:wks){%>'<%=wk%>',<%}%>]
		            },
		            yAxis: {
		                min: 0,
		                title: {
		                    text: 'Number of Commits/Cases'
		                }
		            },
		            legend: {
		                enabled: true,
		                align: 'right',
		                verticalAlign: 'top',
		                x: 0,
		                y: 100
		            },
		            tooltip: {
		                formatter: function() {
		                	return '<b>'+ this.x +'</b><br/>'+
	                        this.series.name +': '+ this.y +'<br/>'+
	                        'Total: '+ this.point.stackTotal;
		                }
		            },
		            plotOptions: {
		                column: {
		                    stacking: 'normal'
		                }
		            },
		            series: [
		              {
		                name: 'New case Commits',
		                data: [<%for(int[] its : result.values()) {%><%=its[0]%>,<%}%>],
		                stack: "commits number",
		                dataLabels: {
		                    enabled: true,
		                    rotation: 0,
		                    percentageDecimals: 0,
		                    color: '#FFFFFF',
		                    align: 'center',
		                    style: {
		                        fontSize: '13px',
		                        fontFamily: 'Verdana, sans-serif'
		                    }
		                }
		              },
		              {
			                name: 'Update case Commits',
			                data: [<%for(int[] its : result.values()) {%><%=its[1]%>,<%}%>],
			                stack: "commits number",
			                dataLabels: {
			                    enabled: true,
			                    rotation: 0,
			                    percentageDecimals: 0,
			                    color: '#FFFFFF',
			                    align: 'center',
			                    style: {
			                        fontSize: '13px',
			                        fontFamily: 'Verdana, sans-serif'
			                    }
			                }
			            },
			            {
			                name: 'New Cases',
			                data: [<%for(int[] its : result.values()) {%><%=its[2]%>,<%}%>],
			                stack: "cases number",
			                dataLabels: {
			                    enabled: true,
			                    rotation: 0,
			                    percentageDecimals: 0,
			                    color: '#FFFFFF',
			                    align: 'center',
			                    style: {
			                        fontSize: '13px',
			                        fontFamily: 'Verdana, sans-serif'
			                    }
			                }
			              },
			              {
				                name: 'Updated Cases',
				                data: [<%for(int[] its : result.values()) {%><%=its[3]%>,<%}%>],
				                stack: "cases number",
				                dataLabels: {
				                    enabled: true,
				                    rotation: 0,
				                    percentageDecimals: 0,
				                    color: '#FFFFFF',
				                    align: 'center',
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
		<table align="center" border="1">
			<tr>
				<th nowrap>Week</th>
				<th nowrap>Commits for New Cases</th>
				<th nowrap>Commits for Updated Cases</th>
				<th nowrap>New Cases</th>
				<th nowrap>Updated Cases</th>
			</tr>
			<tr>
				<th colspan="5"><hr/></th>
			</tr>
			
		<%for(Map.Entry<String,int[]> entry : result.entrySet()) {
			int[] its = entry.getValue();
		%>
			<tr>
				<td><%=entry.getKey() %></td>
				<td><%=its[0] %></td>
				<td><%=its[1] %></td>
				<td><%=its[2] %></td>
				<td><%=its[3] %></td>
			</tr>
		<%} %>
			
		</table>
</body>
</html>