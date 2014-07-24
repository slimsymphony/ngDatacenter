<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="com.nokia.granite.analyzer.*"%>
<%@page import="com.nokia.test.statistic.*"%>
<%@page import="com.nokia.test.casedesign.*"%>
<%@page import="java.util.*"%>
<%
	boolean isLogon = false;
	LDAPAuthenticator auth = ( LDAPAuthenticator ) session.getAttribute( "auth" );
	String fullname = "";
	String currUser = "";
	if ( auth != null ) {
		isLogon = true;
		fullname = auth.getFullName();
		currUser = auth.getNoe();
	}
%>
<%
	Map<Integer,String> featureGroups = CaseDesignManager.getSimpleDict("featureGroup");
	Map<String,List<String>> features = new HashMap<String,List<String>>();
	for(int fg : featureGroups.keySet()){
		String fgName = featureGroups.get( fg );
		features.put(fgName,CaseDesignManager.getSimpleDict("feature","featureGroupId="+fg));				
		
	}
	List<Product> products = StatisticManager.getProducts(false);
	List<String[]> users = CaseDesignManager.getUsers();
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Case Process Main</title>
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
}

th {
	text-align: center;
}

td {
	text-align: left;
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
	$(function() {
		function onLogin(){
			var button = $(".ui-dialog-buttonpane button:contains('Select')");
			$(button).button("disable");
			$.get(
				"auth.jsp",
				{
					username : $('#username').val(),
					password : $('#password').val()
				},
				function(data, textStatus) {
					var button = $(".ui-dialog-buttonpane button:contains('Select')");
					$(button).button("enable");
					if (data == 'true') {
						$("#loginForm").dialog("close");
						window.location.reload();
					} else {
						alert("Login failed["+ data + "]. please try again.");
					}
				}, 
				'text');
		}
		$("#loginForm")
				.dialog(
						{
							autoOpen : false,
							show : "fold",
							hide : "explode",
							height : 250,
							width : 650,
							modal : true,
							close : function() {
								$(".ui-dialog-buttonpane button:contains('Select')").button("enable");
								if (!logon)
									window.location.reload();
							},
							buttons : {
								"Select" : onLogin,
								Cancel : function() {
									$(".ui-dialog-buttonpane button:contains('Select')").button("enable");
									window.history.back();
								}
							}
						});

		$("#addForm")
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
													"addTextCase.jsp",
													{
														designer : currUser,
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
															$("#addForm").dialog("close");
															alert("Add Case successful");
														} else {
															alert("Add new Case failed["+ data+ "]. please try again.");
														}
													}, 'text');
								},
								Cancel : function() {
									$(".ui-dialog-buttonpane button:contains('Submit')").button("enable");
									$('#addForm').dialog("close");
								}
							}
						});
		
		$('#add').click(function() {
			$('#addForm').dialog("open");
		});
		
		$('#qsw').click(function() {
			if ($('#queryDiv').attr('class') == 'hide') {
				$('#queryDiv').removeClass("hide");
			} else {
				$('#queryDiv').addClass("hide");
			}
		});
		
		$("#password").keypress(function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 13) { //Enter keycode
				onLogin();
			}
		});
		
		$('#query').click(function() {
			var condition = 'designer='+$('#designer').val()+'&featureGroup='+$('#featureGroupQ').val()
				+"&feature="+$('#featureQ').val()+"&product="+$('#product').val()+"&autoStatus="
				+$('#autoStatus').val()+"&status="+$('#status').val()+"&name="+$('#name').val()
				+"&content="+$('#contentQ').val()+"&reviewer="+$('#reviewer').val()+"&approver="
				+$('#approver').val()+"&srtLevel="+$('#srtLevelQ').val()+"&condition="+$('#conditionQ').val()
				+"&type="+$('#typeQ').val();
			var url = "queryCases.jsp?" + condition;
			$('#resultFrame').attr('src', url);
		});
		
		if (!logon) {
			$('#loginForm').dialog("open");
		}
		
		$('#logout').click(function() {
			$.get("logout.jsp", {}, function(data, textStatus) {
				if (data == 'true') {
					window.location.reload();
				} else {
					alert("Logout failed[" + data + "]. please try again.");
				}
			}, 'text');
		});
		
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
			if(selected==''){
				fesel.empty();
				fesel.append( new Option("ALL",""));
			}
			for(var i=0;i<data.length;i++){
				if(data[i].getFeatureGroup()==selected){
					fesel.empty();
					var feas = data[i].getFeatures();
					fesel.append( new Option("ALL",""));
					for(var j=0;j<feas.length;j++){
						fesel.append( new Option(feas[j],feas[j]));
					}
					break;
				}
			}
		}
		$('#featureGroup').change(function(){
			onSelFeature(fgs,$('#featureGroup'),$('#feature'));
		});
		$('#featureGroupQ').change(function(){
			onSelFeature(fgs,$('#featureGroupQ'),$('#featureQ'));
		});
		initSel(products,$('#product'));
		initSel(products,$('#validFor'));
		initSelFg(fgs,$('#featureGroup'));
		initSelFg(fgs,$('#featureGroupQ'));
		initSelUser(users,$('#designer'));
		initSelUser(users,$('#automationOwner'));
		initSelUser(users,$('#reviewer'));
		initSelUser(users,$('#approver'));
		$('#featureGroup').change();
		
		$( "#start" ).datepicker();
		$( "#end" ).datepicker();
		$( '#viewOpBtn').click(function(){
			if($('#op_type').val()==0){
				window.open('viewLog.jsp?user='+$('#op_val').val());
			}else{
				window.open('viewLog.jsp?start='+$('#start').val()+"&end="+$('#end').val());
			}
		});
		for(var i=0;i<users.length;i++){
			$('#op_val').append(new Option(users[i].name,users[i].noe));
		}
		$('#op_type').change(function(){
			if($(this).val() == 0){
				$('#op_val').css("display","block");
				$('#time_sel').css("display","none");
			}else{
				$('#op_val').css("display","none");
				$('#time_sel').css("display","block");
			}
		});
		$('#op_type').change();
	});
	</script>
</head>
<body>
	<div id="title-info">
		<p id="caption">WelCome to MP S40 CI Automation Testing Data Center!</p>
	</div>
	<div id="welcome" style="color: green">
		Welcome, Dear <%=fullname%><button id="logout">logout</button>
	</div>
	<div id="conditionDiv" style="text-align: center">
		<table align="center" style="width:800px">
			<tr>
				<th colspan="4">
					<div id="queryDiv" class="hide" style="text-align: left">
						<label for="designer">OWNER</label> <select id="designer">
							<option value="">ALL</option>
						</select> &nbsp; <label for="reviewer">REVIEWER</label> <select
							id="reviewer">
							<option value="">ALL</option>
						</select> &nbsp; <label for="approver">APPROVER</label> <select
							id="approver">
							<option value="">ALL</option>
						</select>&nbsp; <label for="status">STATUS</label> <select id="status">
							<option value="-1">ALL</option>
							<option value="0">UNREVIEW</option>
							<option value="1">REVIEWED</option>
							<option value="2">APPROVED</option>
							<option value="3">SYNCED</option>
						</select> &nbsp; <br /> <label for="featureGroupQ">FEATUREGROUP</label> <select
							id="featureGroupQ">
							<option value="">ALL</option>
						</select> &nbsp; <label for="featureQ">FEATURE</label> <select id="featureQ">
							<option value="">ALL</option>
						</select> &nbsp; <label for="product">PRODUCT</label> <select id="product">
							<option value="">ALL</option>
						</select> &nbsp; <br /> <label for="autoStatus">AUTOMATION STATUS</label> <select
							id="autoStatus">
							<option value="">ALL</option>
							<option value="0">Not Applicable</option>
							<option value="1">Applicable</option>
						</select> &nbsp; <label for="name">CASE NAME</label> <input size="50" type="text"
							id="name" /><br />
						<label for="srtLevelQ">SRT Level</label><select id="srtLevelQ"><option value="">ALL</option><option value="Basic">Basic</option><option value="Advanced">Advanced</option></select>
						<label for="conditionQ">CONDITION</label><select id="conditionQ">
							<option value="">ALL</option>
							<option value="Memory">Memory</option>
							<option value="Networking">Networking</option>
							<option value="SIM">SIM</option>
							<option value="Power Management">Power Management</option>
							<option value="Test Content">Test Content</option>
						</select>
						<label for="typeQ">TYPE</label><select id="typeQ"><option value="">ALL</option><option value="MANUAL">MANUAL</option><option value="AUTOMATED">AUTOMATED</option></select>
						<br/><label for="content">DESC</label><textarea id="contentQ" cols="80"></textarea>
					</div>
				</th>
			</tr>
			<tr>
				<th><a id="qsw" href="javascript:void(0)">Switch</a>
					<button id="query">Query Cases</button>&nbsp;&nbsp;
				</th>
				<th>
					<button id="add">Add New Case</button>&nbsp;&nbsp;
				</th>
			</tr>
			<tr>
				<th colspan="2" nowrap>
					<table align="center"><tr>
					<td>
					<label>Operation records</label>
					</td>
					<td>
					<select id="op_type"><option value="0">BY_USER</option><option value="1">BY_TIME</option></select>
					</td>
					<td>
					<select id="op_val" style="display:none"></select>
					</td>
					<td>
					<span id="time_sel" style="display:none">
						Start:<input id="start" type="text"/>
						End:<input id="end" type="text"/> 
					</span>
					</td>
					<td>
					<button id="viewOpBtn">View</button>
					</td>
					</tr></table>
				</th>
			</tr>
			<tr>
				<th colspan="4" align="center"><a href="index.jsp">Back to
						Data Center Home</a></th>
			</tr>
		</table>
	</div>
	<div id="result">
		<iframe id="resultFrame" src=""></iframe>
	</div>
	<div id="tail">
		<p id="power">&copy;powered by Nokia SW Test Beijing Tools</p>
	</div>
	<div id="loginForm" title="Login into DataCenter">
		<form>
			<fieldset>
				<label for="username">NOE or MAIL</label> <input type="text"
					id="username" /><br /> <label for="password">Password</label> <input
					type="password" id="password" />
			</fieldset>
		</form>
	</div>
	<div id="addForm" title="Add new Case Here" style="text-align: left">
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
</body>
</html>