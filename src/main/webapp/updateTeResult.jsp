<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.*"%>
<%
int teId = CommonUtils.parseInt(request.getParameter("teId"),0);
int execId = CommonUtils.parseInt(request.getParameter("execId"),0);
int caseId = CommonUtils.parseInt(request.getParameter("caseId"),0);
String hide = request.getParameter("hide");
String fgv = request.getParameter("fg");
String result = request.getParameter("result");
if( execId > 0 && caseId > 0 && result!=null){
	StatisticManager.updateTestResult(execId,caseId,result);
}else if(execId>0 && caseId==0&&request.getParameter("caseId")!=null){
	String[] carr = request.getParameter("caseId").split(",");
	for(String casId : carr){
		StatisticManager.updateTestResult(execId,CommonUtils.parseInt(casId,0),result);
	}
}
TestExecution te = StatisticManager.getExecutionById(teId);
List<SubExecution> subs = StatisticManager.getSubExecutionsByExecId( teId );
Testset ts = StatisticManager.getTestsetById(te.getTestsetId());
List<TestResult> res = te.getResults();
List<String> fgs = StatisticManager.getFeatureGroupByExecution( teId );
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
		.hide{
			display:none;
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
		var execId = <%=teId%>;
		var caseId;
		
		function getAllSelected(){
			var caseIds = '';
			$("td :checked").each(function(){
				var tr = $(this).parent().parent();
				if(tr.attr("name")=='title')
					return;
				else
					caseIds +=tr.attr("cid") +",";
			});
			if(caseIds!=''){
				caseIds = caseIds.substr(0,caseIds.length-1); 
			}
			return caseIds;
		}
		
		function updateResults(){
			var caseIds = getAllSelected();
			if(caseIds==''){
				alert("Please select record first");
				return false;
			}
			if(window.confirm("Are you sure to Change these results?")){
				caseId = caseIds;
				$('#result').css("top",(250-$('html').offset().top)+'px');
				$('#result').css("left",(500-$('html').offset().left)+'px');
				$('#result').css("display","block");
			}
		}
		
		function updateResult(eId,cId){
			if(window.confirm("Are you sure to Change the result?")){
				execId = eId;
				caseId = cId;
				$('#result').css("top",(250-$('html').offset().top)+'px');
				$('#result').css("left",(500-$('html').offset().left)+'px');
				$('#result').css("display","block");
			}
		}
		
		function uiChanges(){
			var caseIds = getAllSelected();
			if(caseIds==''){
				alert("Please select record first");
				return false;
			}
			var comment = window.prompt("Please Input the Comments for UIChange:");
			if(comment==null)
				comment = '';
			if(!window.confirm("Are you sure to mark this failure As a UICHANGE Error?")){
				return false;
			}
			
			jQuery.ajax({
				     url:    "bingBug.jsp?execId="+execId+"&caseId="+caseIds+"&bugId=-1&bugInfo="+comment,
		  	         success: function(result) {
		  	        	var carr = caseIds.split(",");
	        	 		for(i in carr){
			         		$("tr[cid='"+carr[i]+"'] td:nth-child(7)").html('<a href="javascript:void(0)">UIChange:'+comment+"</a>");
	        	 		}
						resetAll();
	                 },
		 	         async:   true
				    }); 
		}
		
		function uiChange(eId,cId){
			var comment = window.prompt("Please Input the Comments for UIChange:");
			if(comment==null)
				comment = '';
			if(!window.confirm("Are you sure to mark this failure As a UICHANGE Error?")){
				return false;
			}
			jQuery.ajax({
			     url:    "bingBug.jsp?execId="+eId+"&caseId="+cId+"&bugId=-1&bugInfo="+comment,
	  	         success: function(result) {
		         		//location.reload();
		         		$("tr[cid='"+cId+"'] td:nth-child(7)").html(comment);
                 },
	 	         async:   true
			    }); 
		}
		
		function comments(){
			var caseIds = getAllSelected();
			if(caseIds==''){
				alert("Please select record first");
				return false;
			}
			var comment = window.prompt("Please Input the Comments:");
			if(comment==null || comment == '')
				return false;
			if(!window.confirm("Are you sure to add this Comment?")){
				return false;
			}
			jQuery.ajax({
			     url:    "bingBug.jsp?execId="+execId+"&caseId="+caseIds+"&bugId=-2&bugInfo="+comment,
	  	         success: function(result) {
	  	        	 var carr = caseIds.split(",");
	  	        	 for( i in carr){
	  	        		$("tr[cid='"+carr[i]+"'] td:nth-child(7)").html(comment);
	  	        	 }
					resetAll();
                 },
	 	         async:   true
			    });
		}
		
		function comment(eId,cId){
			var comment = window.prompt("Please Input the Comments:");
			if(comment==null)
				comment = '';
			if(!window.confirm("Are you sure to add this Comment?")){
				return false;
			}
			jQuery.ajax({
			     url:    "bingBug.jsp?execId="+eId+"&caseId="+cId+"&bugId=-2&bugInfo="+comment,
	  	         success: function(result) {
		         		//location.reload();
	  	        		$("tr[cid='"+cId+"'] td:nth-child(7)").html(comment);
                 },
	 	         async:   true
			    }); 
		}
		
		
		function bindBugs(){
			var caseIds = getAllSelected();
			if(caseIds==''){
				alert("Please select record first");
				return false;
			}
			var bugId = window.prompt("Please Input the bug ID(number):");
			if(!$.isNumeric(bugId) || Math.floor(bugId)<=0 ){
				alert('Only Integer accepted!');
				return false;
			}else{
				bugId = Math.floor(bugId);
				var bugInfo = window.prompt("Please Input the Detail Info:");
				if(bugInfo==null)
					bugInfo = '';
				if(!window.confirm("Are you sure to Bind the Bug Info?")){
					return false;
				}
				jQuery.ajax({
				  url:    "bingBug.jsp?execId="+execId+"&caseId="+caseIds+"&bugId="+bugId+"&bugInfo="+bugInfo,
				  success: function(result) {
					  var carr = caseIds.split(",");
				  	  for( i in carr){
					       $("tr[cid='"+carr[i]+"'] td:nth-child(7)").html("<a href='https://mzilla.nokia.com/show_bug.cgi?id="+bugId+"'>BugId:"+bugId+">>"+bugInfo+"</a>");
					  }
					  resetAll();
				  },
				  async:   true
				});
			}

		}
		
		function bindBug(eId,cId){
			var bugId = window.prompt("Please Input the bug ID(number):");
			if(!$.isNumeric(bugId) || Math.floor(bugId)<=0 ){
				alert('Only Integer accepted!');
				return false;
			}else{
				bugId = Math.floor(bugId);
				var bugInfo = window.prompt("Please Input the Detail Info:");
				if(bugInfo==null)
					bugInfo = '';
				if(!window.confirm("Are you sure to Bind the Bug Info?")){
					return false;
				}
				jQuery.ajax({
			         url:    "bingBug.jsp?execId="+eId+"&caseId="+cId+"&bugId="+bugId+"&bugInfo="+bugInfo,
			         success: function(result) {
			        	 		//location.reload();
			        			//$("tr[cid='"+cId+"'] td:nth-child(7)").html(bugId+":"+bugInfo);
			        			$("tr[cid='"+cId+"'] td:nth-child(7)").html("<a href='https://mzilla.nokia.com/show_bug.cgi?id="+bugId+"'>BugId:"+bugId+">>"+bugInfo+"</a>");
			                  },
			         async:   true
			    }); 
			}
			
		}
		
		function resetAll(){
			$("td input[type='checkbox']").attr('checked',false);
		}

		var isHide = false;
		$(function(){
			$('#hide').change(function(){
				if($(this).is(":checked")) {
					$("tr[name='PASS']").addClass("hide");
		        }else{
		        	$("tr[name='PASS']").removeClass("hide");
		        	if($('#fg').val()!=''){
		        		$("tr[fg!='"+$('#fg').val()+"']").addClass("hide");
		        		$("tr[fg='']").removeClass("hide");
		        	}
		        }
			});
			
			$('#selAll').change(function(){
				if($(this).is(":checked")) {
					$("td input[type='checkbox']").each(function(){
						if($(this).parent().parent().attr("class")!='hide'){
							$(this).attr('checked',true);
						}
					});
						
				}else{
					$("td input[type='checkbox']").attr("checked",false);
				}
			});
			
			$('#gonext').click(function(){
				$('#result').css("display","none");
				if($.isNumeric(caseId)){
					jQuery.ajax({
				         url:    "updateTeResult.jsp?teId=<%=teId%>&execId="+execId+"&caseId="+caseId+"&result="+$('#reval').val()+"&hide="+$('#hide').is(":checked")+"&fg="+$('#fg').val(),
				         success: function(result) {
				        	 		//location.reload();
				        	 		$("tr[cid='"+caseId+"']").attr('name',$('#reval').val());
				        	 		$("tr[cid='"+caseId+"'] td:nth-child(3)").html($('#reval').val());
				        	 		if($('#reval').val()=='PASS'){
				        	 			$("tr[cid='"+caseId+"'] td:nth-child(7)").html('');	
				        	 		}
				        	 		$('#hide').change();
				                  },
				         async:   false
				    }); 
				} else{
					jQuery.ajax({
				         url:    "updateTeResult.jsp?teId=<%=teId%>&execId="+execId+"&caseId="+caseId+"&result="+$('#reval').val()+"&hide="+$('#hide').is(":checked")+"&fg="+$('#fg').val(),
				         success: function(result) {
				        	 		//location.reload();
				        	 		var carr = caseId.split(",");
				        	 		for(i in carr){
					        	 		$("tr[cid='"+carr[i]+"']").attr('name',$('#reval').val());
					        	 		$("tr[cid='"+carr[i]+"'] td:nth-child(3)").html($('#reval').val());
					        	 		if($('#reval').val()=='PASS'){
					        	 			$("tr[cid='"+carr[i]+"'] td:nth-child(7)").html('');	
					        	 		}
				        	 		}
									resetAll();
				        	 		$('#hide').change();
				                  },
				         async:   false
				    }); 
				}
			});
			
			$('#gocancel').click(function(){
				$('#result').css("display","none");
				caseId = 0;
			});
			
			<% for(String fg:fgs){%>
			$('#fg').append($('<option>', { 
		        value: '<%=fg%>',
		        text : '<%=fg%>' 
		    }));
			<%}%>
			$('#fg').change(function(){
				if($(this).val()!=''){
					$("tr[fg!='"+$(this).val()+"']").addClass("hide");
					$("tr[fg='"+$(this).val()+"']").removeClass("hide");
					$("tr[fg='']").removeClass("hide");
				}else{
					$("tr").removeClass("hide");
				}
				if($('#hide').is(":checked")){
					$("tr[name='PASS']").addClass("hide");
					$("tr[fg='']").removeClass("hide");
				}
			});
			<%if(hide!=null&&hide.equals("false")){%>$('#hide').attr('checked', true);<%}%>
			<%if(fgv!=null){%>$('#fg').val('<%=fgv%>');<%}%>
		});
	</script>
</head>
<body>
	<h1>Current Updating test execution: <%= te.getName()%></h1>
	<input type="checkbox" id="hide" checked/><label for="hide">Show Or Hide All the success cases.</label><br/>
	<label for="fg">select Feature Group</label> <select size="10" id="fg"><option value="" selected>ALL</option></select><br/>
	<button onclick="updateResults()">Updates</button>
	<button onclick="bindBugs()">Bind Bugs</button>
	<button onclick="uiChanges()">UIchanges</button>
	<button onclick="comments()">Comments</button>
	<button onclick="window.close()">Close</button><br/>
	<table  width="100%">
		<tr name='title' fg=''>
			<th>All<input type="checkbox" id="selAll" /></th>
			<th>Case Id</th>
			<th>Result</th>
			<th>Fail Message</th>
			<th>Fail Detail</th>
			<th>URL</th>
			<th>BugInfo</th>
			<th>Operation</th>
		</tr>
		<tr><td colspan="8"><hr/></td></tr>
<%
for(TestResult tr:res){
	TestCase tc = ts.getTestCaseById(tr.getCaseId());
	if(tc==null)
		tc = StatisticManager.getTestCaseById(tr.getCaseId());
	String caseName = "";
	String url = te.getUrl();
	String bugInfo = null;
	String bugUrl = null;
	String fg = tc.getFeatureGroup();
	if(tr.getBugId()==0){
		bugInfo = "";
		bugUrl = "javascript:void(0)";
	}else if(tr.getBugId()==-1){
		bugInfo = "UI Change:"+tr.getBugInfo();
		bugUrl = "javascript:void(0)";
	}else if(tr.getBugId()==-2){
		bugInfo = tr.getBugInfo();
		bugUrl = "javascript:void(0)";
	}else{
		bugInfo = "BugId:"+tr.getBugId()+"  >> "+tr.getBugInfo();
		bugUrl = "https://mzilla.nokia.com/show_bug.cgi?id="+tr.getBugId();
	}
	if(tc!=null){
		caseName = tc.getCaseName();
	}else{
		try{caseName = StatisticManager.getTestCaseById(tr.getCaseId()).getCaseName();}catch(Exception e){e.printStackTrace();}
	}
	Timestamp switchTate = new Timestamp(1363572061001L);// granite 1.1.7 start to use.
	Timestamp switchDate = new Timestamp(1379779200000L);// granite 1.3.0 start to use.
	String ext = tr.getReference();
	if( url!=null && url.indexOf("artifact")>0 ){
		if( ext != null && !ext.trim().isEmpty(  ) ){
			url = url.substring(0,url.indexOf("artifact"))+"artifact/test_results/" + ext;
		} else {
			if( te.getName().toLowerCase().startsWith("s40_ng_ui") ){
				url = url.substring(0,url.indexOf("artifact"))+"artifact/test_results/test_results_1/html/"+caseName.replaceAll(" ","_")+".html";
			}else if( te.getExecTime().getTime() > switchDate.getTime() ){
				url = url.substring(0,url.indexOf("artifact"))+"artifact/test_results/html/"+caseName.replaceAll(" ","_")+".html";
			}else if( te.getExecTime().getTime() > switchTate.getTime() && te.getExecTime().getTime()< switchDate.getTime() ){
				url = url.substring(0,url.indexOf("artifact"))+"artifact/test_results/html/"+caseName.replaceAll(" ","%20")+".html";
			}else{
				url = url.substring(0,url.indexOf("artifact"))+"artifact/test_results/xml/"+caseName.replaceAll(" ","%20")+".xml";
			}
		}
		
	} else {
		for( SubExecution sub :subs ){
			if(sub.getSubId()==tr.getSubId( )){
				url = sub.getUrl();
				if(url!=null && !url.isEmpty(  )){
					if( ext != null && !ext.trim().isEmpty(  ) ){
						url = url.substring(0,url.indexOf("artifact"))+"artifact/test_results/" + ext;
					} else {
						if( te.getExecTime().getTime() > switchDate.getTime() ){
							url = url.substring(0,url.indexOf("artifact"))+"artifact/test_results/html/"+caseName.replaceAll(" ","_")+".html";
						}else{
							url = url.substring(0,url.indexOf("artifact"))+"artifact/test_results/html/"+caseName.replaceAll(" ","%20")+".html";
						}
					}
				}
			}
		}
	}
%>
		<tr cid="<%=tr.getCaseId() %>" name="<%= tr.getResult()%>" <% if(tr.getResult().equals("PASS")){%>class="hide"<%}%> fg='<%=fg%>'>
			<td><input type="checkbox" id="case_<%=tr.getCaseId()%>" /></td>
			<td><p style="width:200px"><%= caseName%></p></td>
			<td><%= tr.getResult()%></td>
			<td style="text-align:left;"><p style="width:400px;word-wrap:break-word; overflow:hidden;"><%= CommonUtils.notNull(tr.getMessage(),true)%></p></td>
			<td style="text-align:left;"><p style="width:400px;word-wrap:break-word; overflow:hidden;"><%= CommonUtils.notNull(tr.getDetail(),true)%></p></td>
			<td style="text-align:left;"><p style="width:400px;word-wrap:break-word; overflow:hidden;"><a href="javascript:void(0)" onclick='window.open("<%= url%>")'><%= url%></a></p></td>
			<td><a href='javascript:void(0)' onclick="window.open('<%=bugUrl%>')"><%= bugInfo%></a></td>
			<td>
				<button onclick="updateResult(<%= tr.getExecId()%>,<%= tr.getCaseId()%>)">Update</button>
				<button onclick="bindBug(<%= tr.getExecId()%>,<%= tr.getCaseId()%>)">Bind Bug</button>
				<button onclick="uiChange(<%= tr.getExecId()%>,<%= tr.getCaseId()%>)">UIchange</button>
				<button onclick="comment(<%= tr.getExecId()%>,<%= tr.getCaseId()%>)">Comment</button>
			</td>
		</tr>
<%} %>
	</table>
<div id="result" style="display:none;width:250px;height:100px;position: absolute; top: 250px; left: 500px;background-color:yellow;text-align:center">
	<br/>
	<br/>
	<select id="reval">
		<option value="<%=TestResult.PASS%>"><%=TestResult.PASS%></option>
		<option value="<%=TestResult.FAIL%>"><%=TestResult.FAIL%></option>
		<option value="<%=TestResult.NORESULT%>"><%=TestResult.NORESULT%></option>
	</select>
	<button id="gonext">OK</button>&nbsp;&nbsp;
	<button id="gocancel">CANCEL</button>
</div>
</body>
</html>