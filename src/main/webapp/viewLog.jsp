<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.casedesign.*"%>
<%
	response.setHeader("Pragma","no-cache"); 
	response.setHeader("Cache-Control","no-cache"); 
	response.setDateHeader("Expires", 0); 
	int refId = CommonUtils.parseInt(request.getParameter("relid"),0);
	String user = request.getParameter("user");
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
	List<OperationRecord> records = null; 
	if(refId>0){
		records = CaseDesignManager.getOperationRecordsByCase(refId);
	}else if(user!=null && !user.trim().isEmpty()){
		records = CaseDesignManager.getOperationRecordsByUser(user);
	}else {
		records = CaseDesignManager.listOperationRecordsByTime(s,e);
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Case Process History</title>
<link rel="stylesheet" type="text/css"
	href="css/cupertino/jquery-ui-1.8.22.custom.css" />
<style type="text/css">
body {
	text-align: center;
}

div {
	text-align: center;
}

table {
	text-align: center;
	border: solid 2px;
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
}

#caption {
	font-size: 24pt;
}

#power {
	font-size: 15pt;
}

#resultFrame {
	width: 95%;
	height: 750px;
}

.hide {
	display: none;
}
</style>
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.22.custom.min.js"></script>
</head>
<body>
<br/>
<h2>Operation Records</h2>
<table style="width:90%">
	<tr>
		<th>Operation</th>
		<th>User</th>
		<th>Time</th>
		<th>Related Case</th>
		<th>Desc</th>
	</tr>
	<%if(records!=null){
		for(OperationRecord op : records){
			TextCase tc = CaseDesignManager.getTextCaseById(op.getRel_caseId(),true);
			String caseInfo = "";
			String caseDesc = "";
			
			if(tc!=null){
				caseInfo = tc.getName();
				caseDesc = tc.getContent();
			}else{
				if(!op.getOperation_type().equals(OperationRecord.Operation.LOGIN) && op.getOperation_type().equals(OperationRecord.Operation.UNKNOWN)){
					caseInfo = "Case["+op.getRel_caseId() + "], been deleted.";
				}
			}
		%>
	<tr>
		<td><%=op.getOperation_type() %></td>
		<td><%=op.getUser() %></td>
		<td nowrap><%=op.getOperation_time() %></td>
		<td title="<%=caseDesc%>"><%=caseInfo %></td>
		<td><%=CommonUtils.notNull(op.getExtension(),true) %></td>
	</tr>
	<% }
	}%>
</table>
</body>
</html>