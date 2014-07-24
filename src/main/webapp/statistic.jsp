<%@page contentType="text/html;charset=UTF-8"%>
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
	List<String> sws = StatisticManager.getSWVersions();
	List<String> tsnames = StatisticManager.getTestsetNames();
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8"> 
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
	<title>WelCome to MP S40 CI Automation Testing Data Center!</title>
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
			<%if(sws!=null && !sws.isEmpty()){for(String sw: sws){%>
			$('#sws').append(new Option('<%=sw%>','<%=sw%>'));
			<%}}%>
			
			$('#tsn').append(new Option('AllWithoutFota',''));
			<%if(tsnames!=null && !tsnames.isEmpty()){for(String tsn: tsnames){%>
			$('#tsn').append(new Option('<%=tsn%>','<%=tsn%>'));
			<%}}%>
			
			$('#product').change(function(){
				$('#tss').empty();
				for(var ts in sss[$(this).val()])
					$('#tss').append( new Option(sss[$(this).val()][ts].name,sss[$(this).val()][ts].id) );
			});
			$( "#program").change(function(){
				if(this.value=='tfc'||this.value=='ttc'||this.value=='tfcfg'){
					$("#ltfc").css('display', 'block');
				}else{
					$("#ltfc").css('display', 'none');
				}
				
				if(this.value=='pcs'){
					$('#ttt').css('display','block');
					$('#tsname').css('display','block');
				}else{
					$('#ttt').css('display','none');
					$('#tsname').css('display','none');
				}
				
				if(this.value=='tprfg'||this.value=='tpr'||this.value=='ttc'||this.value=='tfcfg'){
					$("#tsId").css('display', 'block');
					if(this.value=='ttc'){
						$('#tss').empty();
						$('#tss').append( new Option('ALL','ALL') );
						for(var ts in sss[$('#product').val()])
							$('#tss').append( new Option(sss[$('#product').val()][ts].name,sss[$('#product').val()][ts].id) );
					}else{
						$('#tss').empty();
						for(var ts in sss[$('#product').val()])
							$('#tss').append( new Option(sss[$('#product').val()][ts].name,sss[$('#product').val()][ts].id) );		
					}
				}else{
					$("#tsId").css('display', 'none');
				}
			});
			$("#go").click(function(){
				var url = "statApi.jsp?item="+$('#program').val()+"&product="+$('#product').val()+"&start="+$( "#start" ).val()+"&end="+$( "#end" ).val();
				if($('#program').val()=='pcs'){
					url = "swCompare.jsp?sw="+$('#sws').val()+"&tsName="+$("#tsn").val();
				}
				
				if($('#program').val()=='tfc')
					url += "&max="+$('#ext').val();
				if($('#program').val()=='tfcfg')
					url += "&max="+$('#ext').val()+"&tsId="+$("#tss").val();;
				if($('#program').val()=='tprfg'||$('#program').val()=='tpr')
					url += "&tsId="+$("#tss").val();
				if($('#program').val()=='ttc')
					url = "durations.jsp?"+"&product="+$('#product').val()+"&max="+$('#ext').val()+"&tsId="+$("#tss").val()+"&start="+$( "#start" ).val()+"&end="+$( "#end" ).val();
				if($('#program').val()=='bt'){
					var ret = window.confirm("Do you want only trace SW Error?");
					if(ret)
						url +="&swErrorOnly=1"; 
					else
						url +="&swErrorOnly=0";
				}
				$( "#resultFrame" ).attr('src',url );
			});
			
			
			if($('#program').val()=='tfc'){
				$("#ltfc").css('display', 'block');
			}else{
				$("#ltfc").css('display', 'none');
			}
			if($('#program').val()=='tprfg'||$('#program').val()=='tpr'){
				$("#tsId").css('display', 'block');
			}else{
				$("#tsId").css('display', 'none');
			}
			
			for(var ts in sss[$('#product').val()])
				$('#tss').append( new Option(sss[$('#product').val()][ts].name,sss[$('#product').val()][ts].id) );
		})
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
				<th>Item:
					<select id="program">
						<option value="tfc">Top Failure Cases</option>
						<option value="tfcfg">Top Failure Cases By testset</option>
						<option value="tprfg">TestSet Pass rate by Feature Group </option>
						<option value="tpr">TestSet Pass rate </option>
						<option value="tff">Top Failure FeatureGroup</option>
						<option value="pt">Pass Trend</option>
						<option value="ft">Failure Trend</option>
						<option value="ttc">Top Time Consuming </option>
						<option value="bt">Bug Trend </option>
						<option value="pcs">Passrate compare between same sw</option>
					</select>
				</th>
				<th id="ltfc">
					<label>Max Case Count</label><input type="text" id="ext" value="20"/>
				</th>
				<th id="tsId">
					<label>TestSet</label><select id="tss"></select>
				</th>
				<th id="tsname" style="display:none">
					<label>TestSet</label><select id="tsn"></select>
				</th>
				<th id="ttt" style="display:none">
					<label>SW Version</label><select id="sws"></select>
				</th>
				<th>Start:<input id="start" type="text"/></th>
				<th>End:<input id="end" type="text"/></th>
				<th><button id="go">GO</button></th>
			</tr>
			<tr><th colspan="7" align="center"><a href="index.jsp">Back to Data Center Home</a></th></tr>
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