<%
	if(session.getAttribute("auth")!=null){
		session.setAttribute("auth", null);
	}
	out.print("true");
%>