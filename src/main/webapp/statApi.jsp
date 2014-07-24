<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%@page import="com.google.gson.reflect.*" %>
<%@page import="java.util.concurrent.ConcurrentSkipListSet" %>
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
<%
	String item = request.getParameter("item");
	String product = request.getParameter("product");
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
	if(item.equals("tfc")){
		int max = CommonUtils.parseInt(request.getParameter("max"),0);
		String topfailureCases  = DataAnalyzer.getTopFailureCases(product,s,e,max,false);
		String topfailureCasesOri  = DataAnalyzer.getTopFailureCases(product,s,e,max,true);
		TypeToken<List<String[]>> tt = new TypeToken<List<String[]>>(){};
		List<String[]> xsa = CommonUtils.fromJson( topfailureCases, tt.getType() );
		List<String[]> xsaOri = CommonUtils.fromJson( topfailureCasesOri, tt.getType() );
%>
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
		                text: 'Top Failed Test Cases'
		            },
		            xAxis: {
		                categories: [
						<%for(String[] its : xsa) {%>
							'<%=its[2]%>',
						<%}%>
		                ],
		                labels: {
		                    rotation: -55,
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
		                    text: 'Failure Count'
		                }
		            },
		            legend: {
		                enabled: true
		            },
		            tooltip: {
		                formatter: function() {
		                    return '<b>'+ this.x +'</b><br/>'+
		                        'Failed Times: '+ Highcharts.numberFormat(this.y, 0);
		                }
		            },
		            series: [{
		                name: 'Updated',
		                data: [
							<%for(String[] its : xsa) {%>
							<%=its[1]%>,
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
							<%for(String[] its : xsaOri) {%>
							<%=its[1]%>,
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
			<tr><th colspan="3">Updated Results</th></tr>
			<tr>
				<th nowrap>Failure Case</th>
				<th nowrap>Failure Count</th>
				<th nowrap>Failure Info</th>
			</tr>
			<tr>
				<th colspan="3"><hr/></th>
			</tr>
			
		<%for(String[] its : xsa) {%>
			<tr title="<%=its[4]%>">
				<td><a href="viewTestCase.jsp?id=<%=its[0]%>"><%=its[2] %></td>
				<td style="text-align : center;"><%=its[1] %></td>
				<td><%=its[3] %></td>
			</tr>
		<%} %>
			
		</table>
		<table align="center">
			<tr><th colspan="3">Original Results</th></tr>
			<tr>
				<th nowrap>Failure Case</th>
				<th nowrap>Failure Count</th>
				<th nowrap>Failure Info</th>
			</tr>
			<tr>
				<th colspan="3"><hr/></th>
			</tr>
			
		<%for(String[] its : xsaOri) {%>
			<tr title="<%=its[4]%>">
				<td><a href="viewTestCase.jsp?id=<%=its[0]%>"><%=its[2] %></td>
				<td style="text-align : center;"><%=its[1] %></td>
				<td><%=its[3] %></td>
			</tr>
		<%} %>
			
		</table>
<%
}else if(item.equals("tfcfg")){
	int tsId = CommonUtils.parseInt(request.getParameter("tsId"),1);
	int max = CommonUtils.parseInt(request.getParameter("max"),0);
	String topfailureCases  = DataAnalyzer.getTopFailureCasesByTestset(product,tsId,s,e,max,false);
	String topfailureCasesOri  = DataAnalyzer.getTopFailureCasesByTestset(product,tsId,s,e,max,true);
	TypeToken<List<String[]>> tt = new TypeToken<List<String[]>>(){};
	List<String[]> xsa = CommonUtils.fromJson( topfailureCases, tt.getType() );
	List<String[]> xsaOri = CommonUtils.fromJson( topfailureCasesOri, tt.getType() );
%>
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
	                text: 'Top Failed Test Cases for testseet'
	            },
	            xAxis: {
	                categories: [
					<%for(String[] its : xsa) {%>
						'<%=its[2]%>',
					<%}%>
	                ],
	                labels: {
	                    rotation: -55,
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
	                    text: 'Failure Count'
	                }
	            },
	            legend: {
	                enabled: true
	            },
	            tooltip: {
	                formatter: function() {
	                    return '<b>'+ this.x +'</b><br/>'+
	                        'Failed Times: '+ Highcharts.numberFormat(this.y, 0);
	                }
	            },
	            series: [
		            {
		                name: 'Updated',
		                data: [
							<%for(String[] its : xsa) {%>
							<%=its[1]%>,
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
							<%for(String[] its : xsaOri) {%>
							<%=its[1]%>,
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
		<tr>
			<th colspan="3">Updated Result</th>
		</tr>
		<tr>
			<th nowrap>Failure Case</th>
			<th nowrap>Failure Count</th>
			<th nowrap>Failure Info</th>
		</tr>
		<tr>
			<th colspan="3"><hr/></th>
		</tr>
		
	<%for(String[] its : xsa) {%>
		<tr title="<%=its[4]%>">
			<td><a href="viewTestCase.jsp?id=<%=its[0]%>"><%=its[2] %></td>
			<td style="text-align : center;"><%=its[1] %></td>
			<td><%=its[3] %></td>
		</tr>
	<%} %>
		
	</table>
	
	<table align="center">
		<tr>
			<th colspan="3">Original Result</th>
		</tr>
		<tr>
			<th nowrap>Failure Case</th>
			<th nowrap>Failure Count</th>
			<th nowrap>Failure Info</th>
		</tr>
		<tr>
			<th colspan="3"><hr/></th>
		</tr>
		
	<%for(String[] its : xsaOri) {%>
		<tr title="<%=its[4]%>">
			<td><a href="viewTestCase.jsp?id=<%=its[0]%>"><%=its[2] %></td>
			<td style="text-align : center;"><%=its[1] %></td>
			<td><%=its[3] %></td>
		</tr>
	<%} %>
		
	</table>
<%
	}else if(item.equals("tff")){
		String topfailureGroups = DataAnalyzer.getTopFailureFeatureGroup(product,s,e);
		TypeToken<List<String[]>> tt = new TypeToken<List<String[]>>(){};
		List<String[]> xsa = CommonUtils.fromJson( topfailureGroups, tt.getType() );
		%>
		<script type="text/javascript">
			$(function(){
				var chart;
			    $(document).ready(function() {
			    	
			    	// Radialize the colors
					Highcharts.getOptions().colors = $.map(Highcharts.getOptions().colors, function(color) {
					    return {
					        radialGradient: { cx: 0.5, cy: 0.3, r: 0.7 },
					        stops: [
					            [0, color],
					            [1, Highcharts.Color(color).brighten(-0.3).get('rgb')] // darken
					        ]
					    };
					});
					
					// Build the chart
			        chart = new Highcharts.Chart({
			            chart: {
			                renderTo: 'container',
			                plotBackgroundColor: null,
			                plotBorderWidth: null,
			                plotShadow: false
			            },
			            title: {
			                text: 'Top Failure Feature Groups from [<%=start%>] to [<%=end%>]'
			            },
			            tooltip: {
			        	    pointFormat: '{series.name}: <b>{point.percentage}%</b>',
			            	percentageDecimals: 1
			            },
			            plotOptions: {
			                pie: {
			                    allowPointSelect: true,
			                    cursor: 'pointer',
			                    dataLabels: {
			                        enabled: true,
			                        color: '#000000',
			                        connectorColor: '#000000',
			                        formatter: function() {
			                            return '<b>'+ this.point.name +'</b>: '+ this.point.x;
			                        }
			                    }
			                }
			            },
			            series: [{
			                type: 'pie',
			                name: 'Feature Group Failure Count',
			                data: [
			                    <%for(String[] its : xsa) {%>
			                    	['<%=its[0]%>',<%=its[1]%>],
			                    <%}%>
			                    ['Others',   0]
			                ]
			            }]
			        });
			    });
			});
		</script>
		<div id="container" style="min-width: 500px; height: 700px; margin: 0 auto">
		</div>
		<hr/>
		<table align="center">
			<tr>
				<th nowrap>Feature Group</th>
				<th nowrap>Failure Count</th>
			</tr>
			<tr>
				<th colspan="3"><hr/></th>
			</tr>
			
		<%for(String[] its : xsa) {%>
			<tr>
				<td style="text-align : center;"><%=its[0] %></td>
				<td style="text-align : center;"><%=its[1] %></td>
			</tr>
		<%} %>
			
		</table>
<%
	} else if (item.equals("tprfg")){
		int tsId = CommonUtils.parseInt(request.getParameter("tsId"),1);
		String passrate = DataAnalyzer.getPassrate( product, tsId, s, e, true);
		TypeToken<Map<String,Map<String,Float>>> tt = new TypeToken<Map<String,Map<String,Float>>>(){};
		Map<String,Map<String,Float>> map = CommonUtils.fromJson(passrate,tt.getType());
		List<String> fgs = new ArrayList<String>();
		Map<String,Integer> vs = new HashMap<String,Integer>();
		for( Map.Entry<String,Map<String,Float>> entry: map.entrySet() ){
			for(String fg : entry.getValue().keySet()){
				if(!fgs.contains(fg))
					fgs.add(fg);
			}
		}
		for(int i=0;i<fgs.size();i++){
			vs.put(fgs.get(i),i);
		}
%>		
	<script type="text/javascript">
		$(function () {
			Highcharts.setOptions({
				global: {
					useUTC: false
				}
			});
			var map = {};
			<%for(int i=0;i<fgs.size();i++){%>
			map['<%=i%>'] = '<%=fgs.get(i)%>';
			<%}%>
		    var chart;
		    $(document).ready(function() {
		        chart = new Highcharts.Chart({
		            chart: {
		                renderTo: 'container',
		                type: 'column',
		                marginRight: 130,
		                marginBottom: 25
		            },
		            title: {
		                text: 'TestSet Pass Rate by Feature Group for Product:<%=product%>',
		                x: -20 //center
		            },
		            xAxis: {
	                    labels: {  
			                rotation: 15,
			                //align: 'right',
			                style: {
			                     fontSize: '10px',
			                },
			                //x:30,
			                //y:-10,
	                        formatter: function () {  
	                            return map[this.value];
	                        }  
	                    }  
		            },
		            yAxis: {
		                title: {
		                    text: 'Pass Rate %'
		                },
		                plotLines: [{
		                    value: 0,
		                    width: 1,
		                    color: '#808080'
		                }],
		                formatter: function () {  
                            return this.value+"%";
                        },
                        min: 0, 
                        max: 100
		            },
		            tooltip: {
		                formatter: function() {
		                        return "<b>Pass Ratio:</b>"+this.y+"% <br/>" + map[this.x] + '<br/>'+this.series.name ;
		                }
		            },
		            legend: {
		                layout: 'vertical',
		                align: 'right',
		                verticalAlign: 'top',
		                x: 20,
		                y: 100,
		                borderWidth: 0
		            },
		            series: [
		            <%for(Map.Entry<String,Map<String,Float>> entry: map.entrySet()){
						String teName = entry.getKey();
						Map<String,Float> tsData = entry.getValue();%>
		    			{
			                name: '<%=teName%>',
			                data: [
			            <%for(Map.Entry<String,Float> ev : tsData.entrySet()){%>
			                      {y:<%=ev.getValue()*100f%>,x:<%=vs.get(ev.getKey())%>},
			            <%}%>
			                ]
			            },	
		    		<%	}%>
		             ]
		        });
		    });
		    
		});
	</script>
	
    <div id="container" style="min-width: 500px; height: 700px; margin: 0 auto">
    	
	</div>
	<hr/>
	<%for(Map.Entry<String,Map<String,Float>> entry: map.entrySet()){
		String teName = entry.getKey();
		Map<String,Float> tsData = entry.getValue();
	 %>
	 
	 <center><h3>Execution :[<%=teName %>]</h3></center>
		<table width="80%" align="center">
			<tr>
				<th nowrap>Feature Group</th>
				<th nowrap>Pass Ratio[Percentage]</th>
			</tr>
			<tr>
				<th colspan="3"><hr/></th>
			</tr>
			
		<%for(Map.Entry<String,Float> its: tsData.entrySet()) {%>
			<tr">
				<td style="text-align : center;"><%=its.getKey() %></td>
				<td style="text-align : center;"><%=its.getValue()*100f %>%</td>
			</tr>
		<%} %>
			
		</table><br/>
	<%}
  } else if (item.equals("tpr")){
	int tsId = CommonUtils.parseInt(request.getParameter("tsId"),1);
	Testset ts = StatisticManager.getTestsetById(tsId);
	String tsName = ts.getName();
	String tpr = DataAnalyzer.getPassrate( product, tsId, s, e, false );
	String oritpr = DataAnalyzer.getOriPassrate( product, tsId, s, e, false );
	TypeToken<List<String[]>> tt = new TypeToken<List<String[]>>(){};
	List<String[]> res = CommonUtils.fromJson( tpr, tt.getType() );
	List<String[]> resOri = CommonUtils.fromJson( oritpr, tt.getType() );
	%>
	<script type="text/javascript">
		$(function () {
			Highcharts.setOptions({
				global: {
					useUTC: false
				}
			});
			var map = {};
			var list = {};
			<%for(int i=0;i<res.size();i++){%>
			map['<%=i%>'] = '<%=res.get(i)[1]%>';
		    list['<%=i%>']= '<%=res.get(i)[0]%>';
			<%}%>
			
			
				
		    var chart;
		    $(document).ready(function() {
		        chart = new Highcharts.Chart({
		            chart: {
		                renderTo: 'container',
		                type: 'line',
		                marginRight: 130,
		                marginBottom: 25
		            },
		            title: {
		                text: 'Pass Ratio for Product:<%=product%>',
		                x: -20 //center
		            },
		            xAxis: {
	                    labels: {  
	                        formatter: function () {  
	                            return map[this.value];
	                        }  
	                    }  
		            },
		            yAxis: {
		                title: {
		                    text: 'Pass Rate!'
		                },
		                plotLines: [{
		                    value: 0,
		                    width: 1,
		                    color: '#808080'
		                }],
		                formatter: function () {  
                            return this.value+"%";
                        },
                        min: 0, 
                        max: 100
		            },
		            tooltip: {
		                formatter: function() {
		                        return "<b>Pass Ratio:</b>"+this.y+"% <br/>" + list[this.x] +"<br/>"+map[this.x];
		                }
		            },
		            legend: {
		                layout: 'vertical',
		                align: 'right',
		                verticalAlign: 'top',
		                x: -10,
		                y: 100,
		                borderWidth: 0
		            },
		            series: [
		    			{
			                name: 'Updated Results for <%=tsName%>',
			                data: [
			                <%for(int i=0;i<res.size();i++){%>
			                      {y:<%=Float.parseFloat(res.get(i)[2])*100f%>,x:<%=i%>},
			                <%}%>
			                ]
			            },
			            {
			                name: 'Original Results for <%=tsName%>',
			                data: [
			                <%for(int i=0;i<resOri.size();i++){%>
			                      {y:<%=Float.parseFloat(resOri.get(i)[2])*100f%>,x:<%=i%>},
			                <%}%>
			                ]
			            },
		             ]
		        });
		    });
		    
		});
	</script>
	
    <div id="container" style="min-width: 500px; height: 700px; margin: 0 auto">
    	
	</div>
	<hr/>
	    <center><h3>TESTSET[<%=tsName %>]</h3></center>
		<table width="80%" align="center">
			<tr>
				<th nowrap>Pass Ratio[Updated]</th>
				<th nowrap>Pass Ratio[Original]</th>
				<th nowrap>SW Version</th>
				<th nowrap>Execution Time</th>
			</tr>
			<tr>
				<th colspan="4"><hr/></th>
			</tr>
			
		<%for(int i=0; i<res.size();i++) { String[] its =res.get(i);String[] its2 =resOri.get(i);%>
			<tr style="cursor:pointer;" onclick="window.open('executionReport2.jsp?teId=<%=its2[3]%>');" >
				<td style="text-align : center;<%if(!its[2].equals(its2[2])){out.println("color:red;");}%>"><%=CommonUtils.parseFloat(its[2],0f)*100 %>%</td>
				<td style="text-align : center;<%if(!its[2].equals(its2[2])){out.println("color:red;");}%>"><%=CommonUtils.parseFloat(its2[2],0f)*100 %>%</td>
				<td style="text-align : center;"><%=its[1] %></td>
				<td style="text-align : center;"><%=its[0] %></td>
			</tr>
		<%} %>
			
		</table><br/>
<%  } else if (item.equals("pt")){
	String passTrend = DataAnalyzer.getPassTrend(product,s,e);
	TypeToken<Map<String, List<String[]>>> tt = new TypeToken<Map<String, List<String[]>>>(){};
	Map<String, List<String[]>> xsa = CommonUtils.fromJson( passTrend, tt.getType() ); 
	Map<String,Map<String,String>> swandtime = new HashMap<String,Map<String,String>>();
	
	SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	
	
	for(Map.Entry<String, List<String[]>> entry : xsa.entrySet()){
		String tsname = entry.getKey();
		List<String[]> vls = entry.getValue();
		swandtime.put(tsname,new HashMap<String,String>());
		for(String[] ars : vls){
			String time = ars[1];
			String sw = ars[2];
			swandtime.get(tsname).put(ars[2],ars[1]);
		}
	}
	List<String> set = new ArrayList<String>();
	Map<Long,String> sws = new TreeMap<Long,String>();
	for(List<String[]> ss : xsa.values()){
		for(String[] ar : ss){
			if(ar!=null&&ar.length>=3){
				sws.put(sd.parse(ar[1]).getTime(), ar[2] );
			}
		}
	}
	
	for(long key:sws.keySet()){
		if(!set.contains(sws.get(key)))
			set.add(sws.get(key));
	}
	Map<String,Integer> swor = new HashMap<String,Integer>();
	for(int i=0;i<set.size();i++){
		swor.put(set.get(i),i);
	}
%>
<script type="text/javascript">
		$(function () {
			Highcharts.setOptions({
				global: {
					useUTC: false
				}
			});
			var map = {};
			<%for(int i=0;i<set.size();i++){%>
			map['<%=i%>']='<%=set.get(i)%>';
			<%}%>
			var list = new Array();
			<%for(Map.Entry<String,Map<String,String>> env : swandtime.entrySet()){
			%>
			list['<%=env.getKey()%>'] = {};	
			<% for(Map.Entry<String,String> env2 : env.getValue().entrySet()){%>
			list['<%=env.getKey()%>']['<%=env2.getKey()%>'] = '<%=env2.getValue()%>';
			<%}
			}%>
		    var chart;
		    $(document).ready(function() {
		        chart = new Highcharts.Chart({
		            chart: {
		                renderTo: 'container',
		                type: 'line',
		                marginRight: 130,
		                marginBottom: 25
		            },
		            title: {
		                text: 'Pass Ratio for Product:<%=product%>',
		                x: -20 //center
		            },
		            xAxis: {
		            	//type: 'datetime',  
	                    labels: {  
	                        /*step: 8,*/
	                        /*tickInterval: 1000*3600*2,*/
	                        formatter: function () {  
	                            //return Highcharts.dateFormat('%m-%d %H:%M', this.value);
	                            
	                            return map[this.value];
	                        }  
	                    }  
		            },
		            yAxis: {
		                title: {
		                    text: 'Pass Rate!'
		                },
		                plotLines: [{
		                    value: 0,
		                    width: 1,
		                    color: '#808080'
		                }],
		                formatter: function () {  
                            return this.value+"%";
                        },
                        min: 0, 
                        max: 100
		            },
		            tooltip: {
		                formatter: function() {
		                        return "<b>Pass Ratio:</b>"+this.y+"% <br/>" + list[this.series.name][map[this.x]] ;
		                }
		            },
		            legend: {
		                layout: 'vertical',
		                align: 'right',
		                verticalAlign: 'top',
		                x: -10,
		                y: 100,
		                borderWidth: 0
		            },
		            series: [
		            <%for(Map.Entry<String,List<String[]>> entry:xsa.entrySet()){
						String tsName = entry.getKey();
						List<String[]> tsData = entry.getValue();%>
		    			{
			                name: '<%=tsName%>',
			                data: [
			            <%for(String[] ar:tsData){%>
			                      {y:<%=ar[0]%>,x:<%=swor.get(ar[2])%>},
			            <%}%>
			                ]
			            },	
		    		<%	}%>
		             ]
		        });
		    });
		    
		});
	</script>
	
    <div id="container" style="min-width: 500px; height: 700px; margin: 0 auto">
    	
	</div>
	<hr/>
	<%for(Map.Entry<String,List<String[]>> entry:xsa.entrySet()){
		String tsName = entry.getKey();
		List<String[]> tsData = entry.getValue();
	 %>
	    <center><h3>TESTSET[<%=tsName %>]</h3></center>
		<table width="80%" align="center">
			<tr>
				<th nowrap>Pass Ratio[Percentage]</th>
				<th nowrap>PassCnt</th>
				<th nowrap>FailCnt</th>
				<th nowrap>NoResultCnt</th>
				<th nowrap>SW Version</th>
				<th nowrap>Execution Time</th>
			</tr>
			<tr>
				<th colspan="6"><hr/></th>
			</tr>
			
		<%for(String[] its : tsData) {%>
			<tr style="cursor:pointer;" onclick="window.open('executionReport2.jsp?teId=<%=its[3]%>');">
				<td style="text-align : center;"><%=its[0]%></td>
				<td style="text-align : center;"><%=its[4]%></td>
				<td style="text-align : center;"><%=its[5]%></td>
				<td style="text-align : center;"><%=its[6]%></td>
				<td style="text-align : center;"><%=its[2] %></td>
				<td style="text-align : center;"><%=its[1] %></td>
			</tr>
		<%} %>
			
		</table><br/>
	<%}

    } else if(item.equals("ft")){
    	String failureTrend = DataAnalyzer.getFailureTrend(product,s,e);
    	TypeToken<Map<String, List<String[]>>> tt = new TypeToken<Map<String, List<String[]>>>(){};
		Map<String, List<String[]>> xsa = CommonUtils.fromJson( failureTrend, tt.getType() );
		Map<String,Map<String,String>> swandtime = new HashMap<String,Map<String,String>>();
		
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		
		
		for(Map.Entry<String, List<String[]>> entry : xsa.entrySet()){
			String tsname = entry.getKey();
			List<String[]> vls = entry.getValue();
			swandtime.put(tsname,new HashMap<String,String>());
			for(String[] ars : vls){
				String time = ars[2];
				String sw = ars[3];
				swandtime.get(tsname).put(ars[3],ars[2]);
			}
		}
		List<String> set = new ArrayList<String>();
		Map<Long,String> sws = new TreeMap<Long,String>();
		for(List<String[]> ss : xsa.values()){
			for(String[] ar : ss){
				if(ar!=null&&ar.length>=4){
					sws.put(sd.parse(ar[2]).getTime(), ar[3] );
				}
			}
		}
		
		for(long key:sws.keySet()){
			if(!set.contains(sws.get(key)))
				set.add(sws.get(key));
		}
		Map<String,Integer> swor = new HashMap<String,Integer>();
		for(int i=0;i<set.size();i++){
			swor.put(set.get(i),i);
		}
%>
	<script type="text/javascript">
		$(function () {
			Highcharts.setOptions({
				global: {
					useUTC: false
				}
			});
			var map = {};
			<%for(int i=0;i<set.size();i++){%>
			map['<%=i%>']='<%=set.get(i)%>';
			<%}%>
			var list = new Array();
			<%for(Map.Entry<String,Map<String,String>> env : swandtime.entrySet()){
			%>
			list['<%=env.getKey()%>'] = {};	
			<% for(Map.Entry<String,String> env2 : env.getValue().entrySet()){%>
			list['<%=env.getKey()%>']['<%=env2.getKey()%>'] = '<%=env2.getValue()%>';
			<%}
			}%>
		    var chart;
		    $(document).ready(function() {
		        chart = new Highcharts.Chart({
		            chart: {
		                renderTo: 'container',
		                type: 'line',
		                marginRight: 130,
		                marginBottom: 25
		            },
		            title: {
		                text: 'Failure Trend for Product:<%=product%>',
		                x: -20 //center
		            },
		            xAxis: {
		            	//type: 'datetime',  
	                    labels: {  
	                        /*step: 8,*/
	                        /*tickInterval: 1000*3600*2,*/
	                        formatter: function () {  
	                            //return Highcharts.dateFormat('%m-%d %H:%M', this.value);
	                            
	                            return map[this.value];
	                        }  
	                    }  
		            },
		            yAxis: {
		                title: {
		                    text: 'Failure case count'
		                },
		                plotLines: [{
		                    value: 0,
		                    width: 1,
		                    color: '#808080'
		                }]
		            },
		            tooltip: {
		                formatter: function() {
		                        return "<b>"+this.y+"</b> Failure cases <br/>" + list[this.series.name][map[this.x]] ;
		                }
		            },
		            legend: {
		                layout: 'vertical',
		                align: 'right',
		                verticalAlign: 'top',
		                x: -10,
		                y: 100,
		                borderWidth: 0
		            },
		            series: [
		            <%for(Map.Entry<String,List<String[]>> entry:xsa.entrySet()){
						String tsName = entry.getKey();
						List<String[]> tsData = entry.getValue();%>
		    			{
			                name: '<%=tsName%>',
			                data: [
			            <%for(String[] ar:tsData){%>
			                      {y:<%=ar[1]%>,x:<%=swor.get(ar[3])%>},
			            <%}%>
			                ]
			            },	
		    		<%	}%>
		             ]
		        });
		        
		    });
		    
		    
		});
	</script>
	
    <div id="container" style="min-width: 500px; height: 700px; margin: 0 auto">
    	
	</div>
	<hr/>
	<%for(Map.Entry<String,List<String[]>> entry:xsa.entrySet()){
		String tsName = entry.getKey();
		List<String[]> tsData = entry.getValue();
	 %>
	    <center><h3>TESTSET[<%=tsName %>]</h3></center>
		<table width="80%" align="center">
			<tr>
				<th nowrap>Failure Count</th>
				<th nowrap>SW Version</th>
				<th nowrap>Execution Time</th>
			</tr>
			<tr>
				<th colspan="3"><hr/></th>
			</tr>
			
		<%for(String[] its : tsData) {%>
			<tr style="cursor:pointer;" onclick="window.open('executionReport2.jsp?teId=<%=its[0]%>');">
				<td style="text-align : center;"><%=its[1] %></td>
				<td style="text-align : center;"><%=its[3] %></td>
				<td style="text-align : center;"><%=its[2] %></td>
			</tr>
		<%} %>
			
		</table><br/>
	<%} %>
<%
	} else if (item.equals("bt")){
		int swErrorOnly = CommonUtils.parseInt(request.getParameter("swErrorOnly"),1);
		Map<String, List<Object[]>> bugTrends = null;
		if(swErrorOnly==1)
			bugTrends = StatisticManager.getBugTrendByProduct(true,product,s,e);
		else 
			bugTrends = StatisticManager.getBugTrendByProduct(false,product,s,e);
		int i=0;
%>	
	<script type="text/javascript">
		$(function () {
			Highcharts.setOptions({
				global: {
					useUTC: false
				}
			});
			var map = {};
			var list = {};
			<%for(Map.Entry<String,List<Object[]>> entry:bugTrends.entrySet()){%>
			map['<%=i%>'] = '<%=entry.getKey()%>';
		    list['<%=i%>']= '<%=entry.getValue().size()%>';
			<%i++;}%>
			
			
				
		    var chart;
		    $(document).ready(function() {
		        chart = new Highcharts.Chart({
		            chart: {
		                renderTo: 'container',
		                type: 'line',
		                marginRight: 130,
		                marginBottom: 25
		            },
		            title: {
		                text: 'Bug Trends for Product:<%=product%>',
		                x: -20 //center
		            },
		            xAxis: {
	                    labels: {  
	                    	rotation: -55,
	                        formatter: function () {  
	                            return map[this.value];
	                        }  
	                    }  
		            },
		            yAxis: {
		                title: {
		                    text: 'Bugs Count'
		                },
		                plotLines: [{
		                    value: 0,
		                    width: 1,
		                    color: '#808080'
		                }],
		                formatter: function () {  
                            return this.value;
                        } 
		            },
		            tooltip: {
		                formatter: function() {
		                        return "<b>Bugs Count:</b>"+this.y+" <br/>" + map[this.x] ;
		                }
		            },
		            legend: {
		                layout: 'vertical',
		                align: 'right',
		                verticalAlign: 'top',
		                x: -10,
		                y: 100,
		                borderWidth: 0
		            },
		            series: [
		    			{
			                data: [
			                <%i = 0;
			                for(Map.Entry<String,List<Object[]>> entry:bugTrends.entrySet()){%>
			                      {y:<%=entry.getValue().size()%>,x:<%=i++%>},
			                <%}%>
			                ]
			            }
		             ]
		        });
		    });
		    
		});
	</script>
	
    <div id="container" style="min-width: 500px; height: 700px; margin: 0 auto">
    	
	</div>
	<hr/>
	    <center><h3>Bug Trends for [<%=product %>]</h3></center>
		<table width="80%" align="center">
			<tr>
				<th nowrap>SW Version</th>
				<th nowrap>Bug Count</th>
				<th nowrap>Details</th>
			</tr>
			<tr>
				<th colspan="3"><hr/></th>
			</tr>
			
		<%for(Map.Entry<String,List<Object[]>> entry:bugTrends.entrySet()){
			List<Object[]> list = entry.getValue();
		%>
			<tr">
				<td style="text-align : center;"><%=entry.getKey() %></td>
				<td style="text-align : center;"><%=list.size()%></td>
				<td><ul>
					<%for(Object[] objs : list){%>
					<li><a href="javascript:void(0)" onclick="window.open('https://mzilla.nokia.com/show_bug.cgi?id=<%=objs[0]%>')">Bug[<%=objs[0]%>]:<%=objs[1] %></a></li>
					<%} %>
				</ul></td>
			</tr>
		<%} %>
		</table><br/>
<%  } %>
</body>
</html>