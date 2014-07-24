<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="com.nokia.test.casedesign.*"%>
<%@page import="java.util.*"%>
<%!
String trans(int v){
	switch(v){
		case 0:
			return "No";
		case 1:
			return "Yes";
		default:
			return "";
	}	
}
%>
<%
	response.setHeader("Pragma","no-cache"); 
	response.setHeader("Cache-Control","no-cache"); 
	response.setDateHeader("Expires", 0); 
	boolean isLogon = false;
	LDAPAuthenticator auth = ( LDAPAuthenticator ) session.getAttribute( "auth" );
	String fullname = "";
	String currUser = "";
	if ( auth != null ) {
		isLogon = true;
		fullname = auth.getFullName();
		currUser = auth.getNoe();
	}
	Map<Integer,String> featureGroups = CaseDesignManager.getSimpleDict("featureGroup");
	Map<String,List<String>> features = new HashMap<String,List<String>>();
	for(int fg : featureGroups.keySet()){
		String fgName = featureGroups.get( fg );
		features.put(fgName,CaseDesignManager.getSimpleDict("feature","featureGroupId="+fg));				
		
	}
	List<Product> products = StatisticManager.getProducts(false);
	List<String[]> users = CaseDesignManager.getUsers();
%>
<%
	String queryString = request.getQueryString();
	LogUtils.getDesignLog().info("query:"+queryString);
	String designer = request.getParameter("designer");
	String reviewer = request.getParameter("reviewer");
	String approver = request.getParameter("approver");
	int status = CommonUtils.parseInt(request.getParameter("status"),0);
	String featureGroup = request.getParameter("featureGroup");
	String feature = request.getParameter("feature");
	String product = request.getParameter("product");
	String autoStatus = request.getParameter("autoStatus");
	String name = request.getParameter("name");
	String content = request.getParameter("content");
	String srtLevel = request.getParameter("srtLevel");
	String condition = request.getParameter("condition");
	String type = request.getParameter("type");
	LinkedHashMap<String,String> conditions = new LinkedHashMap<String,String>();
	switch(status){
		case 0:
			conditions.put("isreviewed","0");
			break;
		case 1:
			conditions.put("isreviewed","1");
			break;
		case 2:
			conditions.put("isapproved","1");
			break;
		case 3:
			conditions.put("issynchronized","1");
			break;
	}
								
	if(!CommonUtils.notNull(designer,false).trim().isEmpty()){
		conditions.put("designer",designer.trim());	
	}
	if(!CommonUtils.notNull(name,false).trim().isEmpty()){
		conditions.put("name",name.trim());	
	}
	if(!CommonUtils.notNull(content,false).trim().isEmpty()){
		conditions.put("content",content.trim());	
	}
	if(!CommonUtils.notNull(reviewer,false).trim().isEmpty()){
		conditions.put("reviewer",reviewer.trim());	
	}
	if(!CommonUtils.notNull(approver,false).trim().isEmpty()){
		conditions.put("approver",approver.trim());	
	}
	if(!CommonUtils.notNull(feature,false).trim().isEmpty()){
		conditions.put("feature",feature.trim());	
	}
	if(!CommonUtils.notNull(featureGroup,false).trim().isEmpty()){
		conditions.put("featureGroup",featureGroup.trim());	
	}
	if(!CommonUtils.notNull(product,false).trim().isEmpty()){
		conditions.put("validFor",product.trim());	
	}
	if(!CommonUtils.notNull(autoStatus,false).trim().isEmpty()){
		conditions.put("automationState",autoStatus.trim());	
	}
	if(!CommonUtils.notNull(srtLevel,false).trim().isEmpty()){
		conditions.put("srtlevel",srtLevel.trim());	
	}
	if(!CommonUtils.notNull(condition,false).trim().isEmpty()){
		conditions.put("condition",condition.trim());	
	}
	if(!CommonUtils.notNull(type,false).trim().isEmpty()){
		conditions.put("type",type.trim());	
	}
	
	List<TextCase> cases = CaseDesignManager.query(conditions);
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Case Query</title>
<link rel="stylesheet" type="text/css" href="css/cupertino/jquery-ui-1.8.22.custom.css" />
<style type="text/css">
body {
	text-align: center;
}

div {
	text-align: center;
}

table {
	text-align: center;
}

th {
	text-align: center;
}

td {
	text-align: left;
	word-wrap:break-word; 
	overflow:hidden;
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
<script type="text/javascript">
var logon = <%=isLogon%>;
var currUser = '<%=currUser%>';
function initSel(data,obj){
	for(var i=0;i<data.length;i++){
		obj.append( new Option(data[i],data[i]));
	}
}
var Features = function( fgName, fgarr ){
	this.fg = fgName;
	this.fes = fgarr;
	this.getFeatureGroup= function(){
		return this.fg;
	};
	this.getFeatures= function(){
		return this.fes;
	};
}
var User = function(noe,name){
	this.noe = noe;
	this.name = name;
};

var users = new Array();
<%int x=0;
for(String[] arr:users){%>
users[<%=x++%>] = new User('<%=arr[0]%>','<%=arr[1]%>');
<%}%>
var fgs = new Array();
var fes;
<% int i=0;
for(String fgn : featureGroups.values()){
	int j=0;
%>
fes = new Array();
	<%for(String fea : features.get(fgn)){%>
	fes[<%=j++%>] = "<%=fea%>";	
	<%}%>
fgs[<%=i++%>] = new Features('<%=fgn%>',fes);
<%}%>
var products = new Array();
<%int v =0;for(Product p:products){%>
products[<%=v++%>] = '<%=p.getName()%>';
<%}%>

var submitUrl = '';
var currentTextId = 0;
function onDel(id){
	if(window.confirm('Are you sure to delete this case?')){
		$.get(
				"delTextcase.jsp",
				{
					id:id
				},
				function(data, textStatus) {
					if (data.trim() == 'true') {
						self.location.reload();
					} else {
						alert("Delete Case failed["+ data+ "]. please try again.");
					}
				}, 'text');
	}
}

function onReview(id){
	submitUrl = 'reviewTextcase.jsp';
	currentTextId = id;
	$("#operationForm").dialog('open');
}
function onGet(id){
	$.get(
		'getTextcase.jsp',
		{id:id},
		function(data,status){
			if(data instanceof String){
				alert(data);
			}else{
				initForm($("#updateForm"),data);
				$("#updateForm").dialog('open');
			}
		},
		"json"
	);
}

function initForm(target,json){
	$("#featureGroup").val(json.featureGroup);
	$("#featureGroup").change();
	$("#feature").val(json.feature);
	$("#automationState").val(json.automationState);
	$("#casename").val(json.name);
	$("#content").val(json.content);
	$("#testArea").val(json.testArea);
	$('#testType').val(json.testType);
	$('#subject').val(json.subject);
	$('#traceable').val(json.traceable);
	$('#automationOwner').val(json.automationOwner);
	$('#errorId').val(json.errorId);
	var arr = '';
	if(json.validFor && json.validFor != ''){
		arr=json.validFor.split(";");
		// Set the value
		$("#validFor").val(arr);
		// Then refresh
		//$("#validFor").multiselect("refresh");
	}
	$('#comments').val(json.comments);
	$('#condition').val(json.condition);
	$('#type').val(json.type);
	$('#srtLevel').val(json.setLevel);	
}

function onUpdate(id){
	currentTextId = id;
	onGet(id);
}
function onApprove(id){
	currentTextId = id;
	submitUrl = 'approveTextcase.jsp';
	$("#operationForm").dialog('open');
}
function onSync(id){
	var password = window.prompt("please input your NOE password","");
	if(password && password !=''){
		$.get(
			"syncTextcase.jsp",
			{id:id,password:password,user:'<%=currUser%>'},
			function(data,textStatus){
				if (parseInt(data)>0) {
					alert("QC Identifier:"+data);
				}else{
					alert("Sync to QC failed:"+data);
				}
			}
		);
	}
}
function onLog(id){
	window.open("viewLog.jsp?relid="+id);
}
function onAtt(id){
	$("#container").css('display','block');
	$('#file').val('');
	$('#refId').val(id);
	$('#queryString').val('<%=queryString%>');
}
function downloadAtt(attId){
	$('#_frame').attr('src', "downloadAttach?attId="+attId);
}
function delAtt(attId){
	$.get(
		"delAtt.jsp",
		{attId:attId},
		function(data, textStatus) {
			if (data.trim() == 'true') {
				self.window.location.reload();
			}else{
				alert("Delete Attachment failed:"+data);
			}
		},
		'text'
	);
}
function initSelFg(data,fgsel){
	for(var i=0;i<data.length;i++){
		fgsel.append( new Option(data[i].getFeatureGroup(),data[i].getFeatureGroup()));
	}
}

function initSelUser(data,usel){
	for(var i=0;i<data.length;i++){
		usel.append( new Option(data[i].name,data[i].noe));
	}
}

function onSelFeature(data,fgsel,fesel){
	var selected = fgsel.val();
	for(var i=0;i<data.length;i++){
		if(data[i].getFeatureGroup()==selected){
			fesel.empty();
			var feas = data[i].getFeatures();
			for(var j=0;j<feas.length;j++){
				fesel.append( new Option(feas[j],feas[j]));
			}
			break;
		}
	}
}
$(function(){
	$("#updateForm")
		.dialog(
			{
				autoOpen : false,
				show : "fold",
				hide : "explode",
				height : 850,
				width : 780,
				modal : true,
				close : function() {
					$(".ui-dialog-buttonpane button:contains('Submit')").button("enable");
				},
				buttons : {
					"Submit" : function() {
						var forProducts = '';
						for( var p in $('#validFor').val()){
							if( forProducts !='')
								forProducts += ";";
							forProducts +=$('#validFor').val()[p];
						}
						var button = $(".ui-dialog-buttonpane button:contains('Submit')");
						$(button).button("disable");
						$.post(
										"updateTextCase.jsp",
										{
											designer : currUser,
											id: currentTextId,
											featureGroup : $("#featureGroup").val(),
											feature : $("#feature").val(),
											automationState : $("#automationState").val(),
											name : $("#casename").val(),
											content : $("#content").val(),
											testArea : $("#testArea").val(),
											testType : $('#testType').val(),
											subject : $('#subject').val(),
											traceable : $('#traceable').val(),
											automationOwner:$('#automationOwner').val(),
											errorId : $('#errorId').val(),
											validFor : forProducts,
											comments : $('#comments').val(),
											condition : $('#condition').val(),
											type : $('#type').val(),
											srtLevel : $('#srtLevel').val(),
										},
										function(data, textStatus) {
											var button = $(".ui-dialog-buttonpane button:contains('Submit')");
											$(button).button("enable");
											if (data.trim() == 'true') {
												$("#updateForm").dialog("close");
												self.location.reload();
											} else {
												alert("Update Case failed["+ data+ "]. please try again.");
											}
										}, 'text');
					},
					Cancel : function() {
						$(".ui-dialog-buttonpane button:contains('Submit')").button("enable");
						$('#updateForm').dialog("close");
					}
				}
			}
		);
	
		$("#operationForm").dialog(
			{
				autoOpen : false,
				show : "fold",
				hide : "explode",
				height : 450,
				width : 700,
				modal : true,
				close : function() {
					$(".ui-dialog-buttonpane button:contains('Submit')").button("enable");
				},
				buttons : {
					"Submit" : function() {
						var button = $(".ui-dialog-buttonpane button:contains('Submit')");
						$(button).button("disable");
						$.post(
								submitUrl,
								{comments:$('#commentsA').val(),result:$('#result').val(),id:currentTextId,user:'<%=currUser%>'},
								function(data, textStatus) {
									var button = $(".ui-dialog-buttonpane button:contains('Submit')");
									$(button).button("enable");
									if (data.trim() == 'true') {
										self.location.reload();
									} else {
										alert("Operation failed["+ data+ "]. please try again.");
									}
								}, 
								'text'
							);
					},
					Cancel : function() {
						$(".ui-dialog-buttonpane button:contains('Submit')").button("enable");
						$('#operationForm').dialog("close");
					}
				}
			}
		);
	initSel(products,$('#validFor'));
	$('#featureGroup').change(function(){
		onSelFeature(fgs,$('#featureGroup'),$('#feature'));
	});
	initSelFg(fgs,$('#featureGroup'));
	initSelUser(users,$('#automationOwner'));
	$('#featureGroup').change();
	
});
</script>
</head>
<body>
<table border="1">
	<tr>
		<th>Name</th>
		<th>Linked errorId</th>
		<th>Reviewed</th>
		<th>Approved</th>
		<th>Synchronized</th>
		<th>FeatureGroup</th>
		<th>Feature</th>
		<!-- <th>TestArea</th> -->
		<th>Valid For</th>
		<th>Designer</th>
		<!-- <th>Trace Available</th>-->
		<!--<th>Subject</th> -->
		<!--<th>AutomationOwner</th> -->
		<!--<th>Test Type</th> -->
		<th>AutomationState</th>
		<th>SRL Level</th>
		<th>Condition</th>
		<th>Attachments</th>
		<th>Operations</th>
	</tr>
<%for(TextCase tc : cases) {
	List<Attachment> attachments = AttachmentManager.getAttachmentsByRefId(tc.getId());
%>
	<tr>
		<td><%=tc.getName() %></td>
		<td><%if(tc.getErrorId()>0) {%><a href="javascript:void(0)" onclick="window.open('https://mzilla.nokia.com/show_bug.cgi?id=<%=tc.getErrorId() %>')"><%=tc.getErrorId() %></a><%} %></td>
		<td><%=trans(tc.getIsReviewed())%></td>
		<td><%=trans(tc.getIsApproved()) %></td>
		<td><%=trans(tc.getIsSynchronized()) %></td>
		<td><%=tc.getFeatureGroup() %></td>
		<td><%=tc.getFeature() %></td>
		<!-- <td><%=tc.getTestArea() %></td> -->
		<td style="width:150px;overflow:hidden;word-wrap:break-word;word-break:break-all">
		<% if(tc.getValidFor()!=null && !tc.getValidFor().trim().equals("")){%><ul>
		<% 	for(String p : tc.getValidFor().split(";")){%>
			<li><%= p%></li>
		<%	}%></ul>
		<%}%>
		</td>
		<td><%=tc.getDesigner() %></td>
		<!-- <td><%=trans(tc.getTraceable()) %></td>-->
		<!-- <td><%=tc.getSubject() %></td> -->
		<!-- <td><%=tc.getAutomationOwner() %></td> -->
		<!-- <td><%=tc.getTestType() %></td> -->
		<td><%=tc.getAutomationState() %></td>
		<td><%=tc.getSrtLevel() %></td>
		<td><%=tc.getCondition() %></td>
		<td style="width:250px">
			<ol style="margin:1px;padding:1px">
			<%for(Attachment att : attachments){%>
				<li title="<%=att.getDescription()%>" style="word-wrap:break-word; overflow:hidden;">
					<a href='javascript:void(0)' onclick="downloadAtt(<%=att.getId() %>)"><%=att.getName()%></a>
					<button onclick="delAtt(<%=att.getId()%>)">del</button>
				</li>
			<%}%>
			</ol>
		</td>
		<td>
			<%if(tc.getIsReviewed()==0){%>
			<button onClick="onReview(<%=tc.getId()%>)">Review</button>
			<%} else if(tc.getIsApproved()==0) {%>
			<button onClick="onApprove(<%=tc.getId()%>)">Approve</button>
			<%} else if(tc.getIsSynchronized() ==0){%>
			<button disabled onClick="onSync(<%=tc.getId()%>)">SyncQC</button>
			<%} %>
			<button onClick="onUpdate(<%=tc.getId()%>)">Update</button>
			<button onClick="onDel(<%=tc.getId()%>)">Del</button>
			<button onClick="onAtt(<%=tc.getId()%>)">Attach</button>
			<button onClick="onLog(<%=tc.getId()%>)">History</button>
		</td>
	</tr>
<%} %>
</table>
	<div id="operationForm" title="Case Operation">
		<form>
			<fieldset>
				<label>Result</label>
				<select id="result"><option value="1">PASS</option><option value="0">REJECT</option></select><br/>
				<label for="commentsA">Comments</label> 
				<textarea id="commentsA" cols="40" rows="3"></textarea>
			</fieldset>
		</form>
	</div>
	<div id="updateForm" title="Add new Case Here" style="text-align: left">
		<form>
			<fieldset>
				<table>
					<tr>
						<th><label for="casename">CaseName</label></th>
						<td><input type="text" id="casename" size="50" /></td>
					</tr>
					<tr><th><label for="featureGroup">FeatureGroup</label>
					<td><select id="featureGroup"></select>
					</td></tr>
					<tr><th><label for="feature">Feature</label></th>
					<td><select id="feature"></select>
					</td></tr>
					<tr><th><label for="testArea">Test Area</label></th><td>
					<select id="testArea"><option value='SW Feature'>SW Feature</option></select>
					</td></tr>
					<tr><th><label for="testType">Test Type</label></th><td>
					<select id="testType">
						<option value="Conditional">Conditional</option>
						<option value="Feature Interaction">Feature Interaction</option>
					</select>
					</td></tr>
					<tr><th><label for="subject">Subject</label></th><td>
					<input type="text" id="subject" />
					</td></tr>
					<tr><th><label for="traceable">Trace Available</label></th><td>
					<select id="traceable">
						<option value="0">No</option>
						<option value="1">Yes</option>
					</select>
					</td>
					</tr>
					<tr><th><label for="automationOwner">Automation Owner</label></th><td>
					<select id="automationOwner"><option value="">&nbsp;</option></select>
					</td></tr>
					<tr><th><label for="automationState">Automation State</label></th><td>
					<select id="automationState">
						<option value="Not Applicable">Not Applicable</option>
						<option value="Applicable">Applicable</option>
					</select>
					</td></tr>
					<tr><th><label for="errorId">Linked ErrorID</label></th><td>
					<input type="text" id="errorId" />
					</td></tr>
					<tr><th><label for="validFor">Valid For</label></th><td>
					<select id="validFor" multiple>
					</select>
					</td></tr>
					<tr><th>SRT Level</th><td><select id="srtLevel"><option value="Basic">Basic</option><option value="Advanced">Advanced</option></select></td></tr>
					<tr><th>Condition</th><td><select id="condition">
							<option value="Memory">Memory</option>
							<option value="Networking">Networking</option>
							<option value="SIM">SIM</option>
							<option value="Power Management">Power Management</option>
							<option value="Test Content">Test Content</option>
						</select></td>
					</tr>
					<tr><th>Type</th><td><select id="type"><option value="MANUAL">MANUAL</option><option value="AUTOMATED">AUTOMATED</option></select></td></tr>
					<tr><th><label for="content">Description</label></th><td>
					<textarea id="content" cols="50" rows="6"></textarea>
					<tr><th><label for="comments">Comments</label></th><td><textarea id="comments" cols="50"  rows="6"></textarea></td></tr>
				</table>
			</fieldset>
		</form>
	</div>
	<div id="container" class="container" style="display:none;width:250px;height:100px;position: absolute; top: 250px; left: 500px;background-color:yellow">
	    <form id="fileupload" action="fileupload" method="POST" enctype="multipart/form-data" target="_self">
	        <div>
                   <input id="file" type="file" name="file"/>
                   <input id="refId" type="hidden" name="refId" value=""/>
                   <input id="queryString" type="hidden" name="queryString" value=""/><br/>
                   <label for="description">Description</label><input id="description" type="text" name="description" value="" /><br/>
	               <button id="uploadBtn" type="submit">Upload</button>
	               <button type="button" onclick="$('#container').css('display','none');">Cancel</button>
	        </div>
	    </form>
	</div>
	<iframe id="_frame" src="" style="display:none;width:1px;height:1px"></iframe>
</body>
</html>