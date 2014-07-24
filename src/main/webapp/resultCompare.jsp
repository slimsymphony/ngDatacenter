<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%
String product = request.getParameter("product");
String start = request.getParameter("start");
String end = request.getParameter("end");
String mode = request.getParameter("mode");
int tsId = CommonUtils.parseInt(request.getParameter("tsId"),0);
SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
Timestamp s = null;
Timestamp e = null;
if(start!=null&&!start.trim().isEmpty()){
	s = new Timestamp(sdf.parse(start).getTime());
}
if(end!=null&&!end.trim().isEmpty()){
	e = new Timestamp(sdf.parse(end).getTime()+(long)1000*60*60*24);
}
Testset ts = StatisticManager.getTestsetById(tsId);
List<TestExecution> execs = StatisticManager.getExecutionsByTestset( product, tsId, mode, s, e );
TreeMap<Integer,String> cases = new TreeMap<Integer,String>();
for(TestExecution te:execs){
	for(TestResult tr : te.getResults()){
		int caseId = tr.getCaseId();
		if(cases.keySet().contains(caseId)){
			continue;
		}
		TestCase tc = ts.getTestCaseById(caseId);
		if(tc==null)
			tc = StatisticManager.getTestCaseById(caseId);
		if(tc != null)
			cases.put(caseId,tc.getCaseName());
		else
			out.println("caseId:"+caseId+" not exist!");
	}
}
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
		td{
			padding : 0px;
			margin: 0px;
			width: 90px;
		}
		.PASS{
			background-color:green;
			padding : 0px;
			margin: 0px;
			width: 100%;
			height: 100%
		}
		.FAIL{
			background-color:red;
			padding : 0px;
			margin: 0px;
			width: 100%;
			height: 100%
		}
		.NORESULT{
			background-color:yellow;
			padding : 0px;
			margin: 0px;
			width: 100%;
			height: 100%
		}
		.updated{
			padding : 0px;
			margin: 0px;
			width: 100%;
			height: 90px
		}
		.original{
			padding : 0px;
			margin: 0px;
			width: 100%;
			height: 90px
		}
	</style>
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
	<script type="text/javascript">
	$(function(){
		$('#switcher').change(function(){
			if($(this).val()=='0'){
				$(".updated").css("display","block");
				$(".original").css("display","block");
			}else if($(this).val()=='1'){
				$(".updated").css("display","block");
				$(".original").css("display","none");
			}else if($(this).val()=='2'){
				$(".updated").css("display","none");
				$(".original").css("display","block");
			}
		});
	});
	</script>
</head>
<body>
	Switch Results Type <select id="switcher"><option value="0">ALL</option><option value="1">Updated</option><option value="2">Original</option></select> <br/>
	<table>
		<tr>
			<th>Case ID</th>
			<th>FeatureGroup</th>
			<th>Feature</th>
			<th>Case Name</th>
			<% for(int i=0;i<execs.size();i++){
				TestExecution te = execs.get(i);%>
			<th nowrap colspan="2" style="width:180px"><%= te.getSw()+":Total "+te.getTotalCnt()+"<br/>Ori:"+te.getOriPassCnt()+"/"+te.getOriFailCnt()+"<br/>Updated:"+te.getPassCnt()+"/"+te.getFailCnt()%></th>
			<th>Bugs</th>
			<%} %>
		</tr>
		<% for(int caseId:cases.keySet()){
			TestCase tc = ts.getTestCaseById(caseId);
			if(tc==null)
				tc = StatisticManager.getTestCaseById(caseId);
				
		%>
		<tr>
			<td><%=cases.get(caseId).substring(cases.get(caseId).indexOf("-")+1,cases.get(caseId).indexOf(")")) %></td>
			<td><%=tc.getFeatureGroup() %></td>
			<td><%=tc.getFeature() %></td>
			<td><%=cases.get(caseId) %></td>
			<%for(int i=0;i<execs.size();i++){
				TestExecution te = execs.get(i);
				TestResult tr = te.getResult(caseId);
				if(tr==null){
			%>
			<td colspan="2" style="background-color:grey">&nbsp;</td>
			<td style="">&nbsp;</td>
			<% }else{%>
			<td <%if(!tr.getResult().equals(TestResult.PASS)){%>title="<%=(tr.getBugInfo()+"\n"+tr.getMessage()+"\n"+tr.getDetail()).replaceAll("\"","'")%>"<%}%>><div  class="updated"><div class="<%= tr.getResult()%>"><%= tr.getResult()%></div></div></td>
			<td <%if(!tr.getOriResult().equals(TestResult.PASS)){%>title="<%=(tr.getBugInfo()+"\n"+tr.getMessage()+"\n"+tr.getDetail()).replaceAll("\"","'")%>"<%}%>><div class="original"><div class="<%= tr.getOriResult()%>"><%= tr.getOriResult()%></div></div></td>
			<%   if(tr.getBugId()>0) {%>
			<td><a href="https://mzilla.nokia.com/show_bug.cgi?id=<%=tr.getBugId() %>">Bug[<%=tr.getBugId() %>]</a></td>
			<%   } else {out.println("<td></td>");}%>
			<% }
			  }
			%>
		</tr>
		<%} %>
	</table>
</body>
</html>