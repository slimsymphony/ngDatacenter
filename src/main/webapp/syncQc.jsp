<%@page import="com.nokia.granite.analyzer.*"%><%@page import="com.nokia.test.statistic.*"%><%@page import="com.nokia.test.qc.*"%><%
String username = request.getParameter("username");
String password = request.getParameter("password");
String product =  request.getParameter("product");
String tsName = request.getParameter("tsName");
//String createNew = request.getParameter("createNew");
String type = request.getParameter("type");
int teId = CommonUtils.parseInt( request.getParameter("teId"), 0);
if(teId<=0)
	out.print("No Such Test Execution");
try{
	String domain = null;
	String project = null;
	boolean createNewBool = false;
	String dictType = QcHelper.DICT_REGRESSION;
	if(type!=null){
		dictType =  type;
		/*if(type.equals(QcHelper.MAPPING_FRT)){
			domain = "MP_S40";
			project = "S40_NG_CA";
		}else{
			domain = "MASTER";
			project = "S40_MASTER";
		}*/
		domain = "MASTER";
		project = "S40_MASTER";
	}
	TestExecution te = StatisticManager.getExecutionById( teId );
	QcHelper helper = new QcHelper(username,password,domain,project,product,dictType);
	helper.setBaseUrl( "https://qc11.nokia.com/qcbin/" );
	helper.startProcessExecution( product, tsName, te);
	out.print("OK");
}catch(Exception e){
	LogUtils.getStatLog().error( "Sync result to Qc Failed,tsName="+tsName+",teId="+teId+",type="+type, e );
	out.print(e.getMessage());
}
%>