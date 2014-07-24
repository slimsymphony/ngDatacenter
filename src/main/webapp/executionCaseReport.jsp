<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%
int teId = CommonUtils.parseInt(request.getParameter("teId"),0);
TestExecution te = StatisticManager.getExecutionById(teId);
List<TestCaseResult> tcs =  te.transform();
int succ=0,fail=0,nor=0;
for(TestCaseResult tcr : tcs){
	String re = tcr.getResult();
	if(re.equals("PASS"))
		succ++;
	else if(re.equals("FAIL"))
		fail++;
	else
		nor++;
}
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
		
		td{
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
	});
	
	</script>
</head>
<body>
	<div id="title">System Automation Test daily regression test report for <%=te.getProduct() %>, <%=te.getSw()%></div><br/>
	<div id="summay"><span style="color:white;font-weight:bold">test result summary</span></div>
	<div class="bg">
		<font color="#DE00DE"><%=fail %> Failed, <%=succ %> Pass, <%=nor %> No Result/<%=tcs.size() %> cases</font>
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
	<div id="environment"><span style="color:white;font-weight:bold">test environment</span></div>
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
				<td width="180">SIM card</td>
				<td>BTN SIM Cards</td>
			</tr>
			<tr>
				<td width="180">Test tool</td>
				<td>Granite</td>
			</tr>
			<%if(te.getFrom()!=null && !te.getFrom().isEmpty()){%>
			<tr>
				<td width="180">Source Sw Version</td>
				<td><%=te.getFrom() %></td>
			</tr>
			<%} %>
		</table>
		<br/>
	</div>
	<div class="bg">
	<table>
		<tr>
			<th>QC Identifier</th>
			<th>Test Case</th>
			<th style="text-align : center;">bug</th>
			<th>Result</th>
			<th>Test Scripts</th>
		</tr>
		<%
		for(TestCaseResult tcr : tcs){
			String bugInfo = null;
			String bugUrl = null;
			String result = tcr.getResult();
			if(result.equals("PASS"))
				result = "<font color='green'>PASS</font>";
			else if(result.equals("FAIL"))
				result = "<font color='red'>Failed</font>";
			else
				result = "<font color='grey'>No result</font>";
			String caseName = tcr.getCaseName();
			if(caseName.indexOf("(")>=0 && caseName.indexOf(")")>=0){
				caseName = caseName.substring(caseName.indexOf(")")+1).trim();
				if(caseName.startsWith("-"))
					caseName = caseName.substring( 1 );
			}
		%>
		<tr>
			<td nowrap width="10%"><%= tcr.getQcId()%></td>
			<td width="20%"><%= caseName%></td>
			<td width="30%">
				<%if( !tcr.getBugs().isEmpty()) {%>
					<ul>
						<%for(int bugid : tcr.getBugs().keySet()) {%>
						<li style="font-size:10pt">
						<%
						if(bugid==-1){
							bugInfo = "[UI Change]"+tcr.getBugs().get(bugid);
							bugUrl = "javascript:void(0)";
							out.print(bugInfo);
						}else if(bugid==-2){
							bugInfo = tcr.getBugs().get(bugid);
							bugUrl = "javascript:void(0)";
							out.print(bugInfo);
						}else if(bugid>0){
							bugInfo = "[SW issue]: Bug "+bugid;
							bugUrl = "https://mzilla.nokia.com/show_bug.cgi?id="+bugid;
							out.print("<a href='"+bugUrl+"'>"+bugInfo+"</a>");
						} %>	
						</li>
						<%}%>
					</ul>
				<%}%>
			</td>
			<td  width="10%"><%= result%></td>
			<td  width="30%"><ol>
				<%for(String sn : tcr.getScripts().keySet()){
					String re =  tcr.getScripts().get(sn);
					if(re.equals("PASS"))
						re = "<font color='green'>PASS</font>";
					else if(re.equals("FAIL"))
						re = "<font color='red'>Failed</font>";
					else
						re = "<font color='grey'>No result</font>";
				%>
				<li style="font-size:9pt"><%=sn %>&nbsp;-&nbsp;<%=re %></li>
				<%}%></ol>
			</td>
		</tr>
		<%} %>
	</table>
	</div>
</body>
</html>