<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%! String notNull(String str){
	if(str==null)
		return "";
	else
		return str;
}
String convertProductName(String productName){
    
    String[] productParams = productName.split("_");
    String productNameNew = "";
    
    if (productParams.length > 1){
        productNameNew = productParams[0] + productParams[1].toUpperCase();
    }else if (productParams.length == 1){
        productNameNew = productParams[0];
    }
    
    return productNameNew;
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
		table{
			width: 90%;
			height: 60%
			text-align : center;
		}
		td{
			text-align : center;
		}
	</style>
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
	<script type="text/javascript">
		function check(id){
			if(id>0){
				window.open("updateTeResult.jsp?teId="+id);
			}
		}
		
		function report(id){
			if(id>0){
				window.open("executionReport2.jsp?teId="+id);
			}
		}
		
	</script>
</head>
<body>
<%
	String sw = request.getParameter("commit");
	String product = convertProductName(request.getParameter("product"));
	String name = request.getParameter("name");
	List<TestExecution> execs = StatisticManager.queryExecutionBySw_Product_Name( sw, product, name );
%>
	<table>
		<tr>
			<th>Execution Name</th>
			<th>Execution Time</th>
			<th>Source SW</th>
			<th>Target SW</th>
			<th>Execution Type</th>
			<th>URL</th>
			<th>Operation</th>
		</tr>
		<tr><td colspan="6"><hr/></td></tr>
<% for(TestExecution te:execs) {%>	
		<tr>
			<td><%=te.getName() %></td>
			<td><%=te.getExecTime() %></td>
			<td><%=notNull(te.getFrom()) %></td>
			<td><%=te.getSw() %></td>
			<td><%=te.getType() %></td>
			<td><%if(te.getUrl()!=null&&te.getUrl().indexOf("artifact")>0){%><a href='javascript:void(0)' onclick="window.open('<%=te.getUrl().substring(0,te.getUrl().indexOf("artifact"))%>')"><%=te.getUrl().substring(0,te.getUrl().indexOf("artifact"))%></a><%}%></td>
			<td>
				<button onclick="check(<%=te.getId() %>)">Select</button>
				<button onclick="report(<%=te.getId() %>)">Get Report</button>
			</td>
		</tr>
<%}%>	
	</table>

</body>
</html>