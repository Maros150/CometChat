<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 <TITLE>Przyklad POST</TITLE>
 
 <script type="text/javascript">
	var request, ie,logged;
	
	function zaloguj()
	{
		logged = true;
		login = document.getElementById("LOGIN").value;
		document.getElementById("zaloguj").style.display = "none";
		document.getElementById("zalogowany").style.display = "block";
	}
	function newRequest() {
		var httpRequest;
		if (window.XMLHttpRequest) { // Mozilla, Safari, ...
			ie = 0;
			httpRequest = new XMLHttpRequest();
			if (httpRequest.overrideMimeType) {
				httpRequest.overrideMimeType('text/xml');
			}
		}
		else { // IE
			ie = 1;
			try {
				httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
			}
			catch (e) {}
			
			if ( typeof httpRequest == 'undefined' ) {
			
				try {
					httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
				}
				catch (f) {}
			}
		}
		if (!httpRequest) {
			alert('Giving up :( Cannot create an XMLHTTP instance');
			return false;
		}
		else {
			return httpRequest ;
		}

	}
 	function send(arg) {
 		var url = "http://localhost:8080/TryChatTest/test";
		if ( typeof request == 'undefined' ) {
			/* create new request */
				request =  newRequest() ;
			}
            request.open("post", url, true);
            request.setRequestHeader("Content-Type","application/x-javascript;");
            
       
            get(request);
            
            if ( arg.substring(0,4)=="send") {
    			arg = document.getElementById("message").value;
    			document.getElementById("message").value="";
    			document.getElementById("message").focus();
    			
    			request.send(arg);
    		}
    		else if (arg.substring(0,7)=="connect") {
    			
    			request.send();
    		}    
        }
  	function get(req) {
 		req.onreadystatechange = function() {
            if (req.readyState == 4) {
                if (req.status == 200){
                    if (req.responseText) {
                        document.getElementById("history").value = 
                        	document.getElementById("history").value + req.responseText;
                        send('connect');
                    }
                }
                
            }
        };
	}
 
 </script>
 
 
</HEAD>
<body onload="send('connect')">
<H1 ALIGN="CENTER">Sample HTTP Chat</H1>
    CHAT<br>
    <textarea rows="10" cols="60" id="history" readonly="readonly" style="resize: none;"></textarea><br>
     <textarea id="message"></textarea>
     <INPUT TYPE="SUBMIT" VALUE="Wyslij" onclick="send('send')">
	 <BR>
	 <P>
	 <BR>
	 <DIV id="zaloguj">
	 Zaloguj siÄ
	 <INPUT TYPE="TEXT" id="LOGIN">
	 <INPUT TYPE="SUBMIT" VALUE="Zaloguj" onclick="zaloguj()">
	 </DIV>
</body> 

</HTML>