<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%
int teId = CommonUtils.parseInt(request.getParameter("teId"),0);
TestExecution te = StatisticManager.getExecutionById(teId);
String product = te.getProduct();
if(product.equalsIgnoreCase( "aquaDS" ))
	product = "Aqua DS";
else if(product.equalsIgnoreCase( "aquaSS" ))
	product = "Aqua SS";
else if(product.equalsIgnoreCase( "pegasusDS" ))
	product = "Pegasus DS";
else if(product.equalsIgnoreCase( "pegasusSS" ))
	product = "Pegasus SS";
else if(product.equalsIgnoreCase( "orionDS" ))
	product = "Orion DS";
else if(product.equalsIgnoreCase( "orionSS" ))
	product = "Orion SS";
Testset ts = StatisticManager.getTestsetById(te.getTestsetId());
List<TestResult> res = te.getResults();
Map<Integer,String[]> bugs = new HashMap<Integer,String[]>();
Map<Integer,String[]> toolbugs = new HashMap<Integer,String[]>();
for(TestResult tr:te.getResults()) {
	if(tr.getBugId()>0){
		String bugUrl = "https://mzilla.nokia.com/show_bug.cgi?id="+tr.getBugId();
		if(tr.getResult().equals(TestResult.NORESULT)){
			toolbugs.put(tr.getBugId(),new String[]{tr.getBugInfo(), bugUrl});
		}else{
			bugs.put(tr.getBugId(),new String[]{tr.getBugInfo(), bugUrl});
		}
	}
}
String wk = CommonUtils.getCurrentWk2();
%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<style>
		body{
			font-family: Calibri;
			font-size: 11pt;
		}
		
		div{
			width:1000px;
		}
		#title{
			text-align : left;
			background-color: #5EB2C0;
		}
		
		#summay{
			text-align : left;
			background-color: #DE00DE;
		}
		
		#environment{
			text-align : left;
			background-color: #595959;
		}
		
		th{
			text-align : left;
			background-color: #00B0F0;
		}
		
		.bg{
			background-color: #EEECE1;
		}
		
		
		table{
			width: 100%;
			text-align : left;
		}
		
	</style>
	<link type="text/css" href="css/cupertino/jquery-ui-1.8.22.custom.css" rel="stylesheet" />
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
	<script type="text/javascript">
	$(function(){
		$( "#qcForm" ).dialog({
			autoOpen: false,
			show: "fold",
			hide: "explode",
			height: 250,
			width: 650,
			modal: true,
			buttons: {
				"Select": function(){
					var button = $(".ui-dialog-buttonpane button:contains('Select')");
				    $(button).button("disable");
					$.get( 
							"syncQc.jsp", 
						    { teId:<%=teId%>,tsName:$("#tsName").val(),username:$('#username').val(),password:$('#password').val(),product:'<%=product%>'},
							function(data, textStatus){
								if(data=='OK'){
									var button = $(".ui-dialog-buttonpane button:contains('Select')");
								    $(button).button("enable");
									$( "#qcForm" ).dialog( "close" );
									alert("Synchronized to QC Successfully.");
								}else{
									alert("Sycn with QC failure:"+data);
								}
					  		},
					  		'text' 
					);
				},
				Cancel: function() {
					$( this ).dialog( "close" );
					var button = $(".ui-dialog-buttonpane button:contains('Select')");
				    $(button).button("enable");
				}
			}
		});
	});
	
	function sync(){
		$('#qcForm').dialog( "open" );
	}
	</script>
</head>
<body>
	<div><button onclick="sync()">Sync with QC</button></div>
	<div id="title">System Automation Test daily regression test report for <%=te.getProduct() %>, F2, <%=te.getSw()%></div><br/>
	<div id="summay"><font color="grey">test result summary</font></div>
	<div class="bg">
		<font color="#DE00DE"><%=te.getFailCnt() %> Failed, <%=te.getPassCnt() %> Pass, <%=te.getNoResultCnt() %> No Result/<%=te.getTotalCnt() %> cases</font>
		<br/>
		SW Errors<br/>
		<%for(int bugId : bugs.keySet()) {
			String bugInfo = "Bug["+bugId+"]  : "+bugs.get(bugId)[0];
			String bugUrl = bugs.get(bugId)[1];
		%>
		<a href="<%=bugUrl%>"><%=bugInfo %></a><br/>
		<%}%>
		Non-SW Errors<br/>
		<%for(int bugId : toolbugs.keySet()) {
			String bugInfo = "Bug["+bugId+"]  : "+toolbugs.get(bugId)[0];
			String bugUrl = toolbugs.get(bugId)[1];
		%>
		<a href="<%=bugUrl%>"><%=bugInfo %></a><br/>
		<%}%>
	</div>
	<div id="environment"><font color="white">test environment</font></div>
	<div class="bg">
		<table>
			<tr>
				<td width="180">SW update</td>
				<td><%=te.getType()%></td>
			</tr>
			<tr>
				<td width="180">Product</td>
				<td><%=te.getProduct() %></td>
			</tr>
			<tr>
				<td width="180">SW version</td>
				<td><%=te.getSw()%></td>
			</tr>
			<tr>
				<td width="180">HW</td>
				<td>F2</td>
			</tr>
			<tr>
				<td width="180">SIM card</td>
				<td>2 BTN SIM Cards</td>
			</tr>
			<tr>
				<td width="180">Test tool</td>
				<td>Granite</td>
			</tr>
		</table>
		<br/>
	</div>
	<div class="bg">
	<table>
		<tr>
			<th>identifier</th>
			<th>test case</th>
			<th style="text-align : center; width: 170px;">bug</th>
			<th><%=te.getSw()%></th>
		</tr>
		<%for(TestResult tr : te.getResults()){
			TestCase tc = ts.getTestCaseById(tr.getCaseId());
			String caseName = "";
			if(tc!=null){
				caseName = tc.getCaseName();
			}else{
				try{caseName = StatisticManager.getTestCaseById(tr.getCaseId()).getCaseName();}catch(Exception e){e.printStackTrace();}
			}
			String bugInfo = null;
			String bugUrl = null;
			if(tr.getBugId()==0){
				bugInfo = "";
				bugUrl = "javascript:void(0)";
			}else if(tr.getBugId()==-1){
				bugInfo = "[UI Change]"+tr.getBugInfo();
				bugUrl = "javascript:void(0)";
			}else if(tr.getBugId()==-2){
				bugInfo = tr.getBugInfo();
				bugUrl = "javascript:void(0)";
			}else{
				if(tr.getResult().equals(TestResult.FAIL)){
					bugInfo = "[SW issue]: Bug "+tr.getBugId();
				}else{
					bugInfo = "Bug "+tr.getBugId();
				}
				bugUrl = "https://mzilla.nokia.com/show_bug.cgi?id="+tr.getBugId();
			}
			String idn = caseName.substring(caseName.indexOf("-")+1,caseName.indexOf(")"));
			String result = tr.getResult();
			if(result.equals("PASS"))
				result = "<font color='green'>PASS</font>";
			else if(result.equals("FAIL"))
				result = "<font color='red'>Failed</font>";
			else
				result = "<font color='grey'>No result</font>";
				
		%>
		<tr>
			<td><%= idn%></td>
			<td><%= caseName%></td>
			<td nowarp><%if(!bugUrl.equals("javascript:void(0)")){%><a href="<%=bugUrl%>"><%=bugInfo %></a><%}else{ %><%=bugInfo %><%} %></td>
			<td><%= result%></td>
		</tr>
		<%} %>
	</table>
	</div>
	<div id="qcForm" title="Fill in QC Sync releated info">
		<form>
			<fieldset>
				<label for="tsName">TestSet Name</label>
				<input type="text" id="tsName" value="<%=product%>_<%=wk%>_*_Feature_Tests_Granite" /><br/>
				<label for="username">Submitter NOE</label>
				<input type="text" id="username" /><br/>
				<label for="password">Submitter Password</label>
				<input type="password" id="password" />
			</fieldset>
		</form>
	</div>
</body>
</html>