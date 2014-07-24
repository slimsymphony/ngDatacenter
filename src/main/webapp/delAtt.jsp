<%@page import="com.nokia.granite.analyzer.*"%><%@page import="com.nokia.test.casedesign.*"%><%
	int attId = CommonUtils.parseInt(request.getParameter("attId"),0);
	if(attId<=0)
		out.print("Invalid attachment Id:"+attId);
	Attachment attach = AttachmentManager.getAttachmentById(attId);
	if(attach!=null){
		boolean ret = AttachmentManager.delAttachmentById(attId);
		if(ret){
			OperationRecord op = new OperationRecord();
			try{
				LDAPAuthenticator auth = ( LDAPAuthenticator ) session.getAttribute( "auth" );
				String currUser = "";
				if ( auth != null ) {
					currUser = auth.getNoe();
				}
				op.setUser(currUser);
				op.setOperation_type(OperationRecord.Operation.DELATTACH);
				op.setRel_caseId(attach.getRefId());
				op.setExtension("Attachment:"+attach.toString());
				CaseDesignManager.addOperationRecord(op);
			}catch(Exception ex){
				LogUtils.getDesignLog().error("Del Operation Record failed:"+op.toString(), ex);
			}
			out.print("true");
		}else{
			out.print("delete failure,please contact system administrator to check the detail logs.");
		}
	}else{
		out.print("No attachment found with attID:"+attId);
	}
%>