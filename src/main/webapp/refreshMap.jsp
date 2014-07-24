<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="com.nokia.test.qc.*"%>
<%@page import="java.util.*"%>
<%
String refresh = request.getParameter("refresh");
if(refresh!=null&&refresh.equals("1")){
	com.nokia.test.qc.QcHelper.refresh();
}
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8"> 
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
<title>Refresh Mapping Info</title>
<script>
function confirm(){
	window.location.href='refreshMap.jsp?refresh=1';
}
</script>
</head>
<body>
<button onclick="confirm()">Refresh Mapping</button>
<table>
<% for(String type: QcHelper.MAPPING_DATA.keySet() ){
	Map<String,Map<String,String>> v = QcHelper.MAPPING_DATA.get( type ); 
%>
<tr><td>
	<%=type%>:
	<ul>
	<% for(String k : v.keySet(  )){
			Map<String,String> vls = v.get(k);
	%>
	<li><%=k %> - <ul>
	<%for(String kk : vls.keySet()){ %>
	<li><%=kk %>:<%=vls.get(kk)%></li>
	<%} %>
	</ul></li>
	<%} %>
	</ul>
</td></tr>
<%} %>
</table>
</body>
</html>