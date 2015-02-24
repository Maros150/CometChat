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
		login = document.getElementById("LOGIN").value; //w loginie będzie nazwa użytkownika
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
	
	function send2(g)
	{
		var url = "http://localhost:8084/MessageSender/test";
		if ( typeof request == 'undefined' ) {
			/* create new request */
				request =  newRequest() ;
			}
            request.open("post", url, true);
            request.setRequestHeader("Content-Type","application/x-javascript;");
            get(request);
            
            arg = "#Message: \""+document.getElementById("message2").value +"\",";
			arg = arg + "#Nadawca: \""+login +"\","; //nick odbiorcy
			arg = arg + "#Odbiorca: \""+g +"\",";    //nr grupy
			document.getElementById("message2").value="";
			document.getElementById("message2").focus();
			request.send(arg);
          /*  
		arg = document.getElementById("message2").value;
		arg="user2_user3_user4_user1_msg";
		
		tab = arg.split("_");
        wynik ="";
        from=tab[tab.length-2];
        msg=tab[tab.length-1];
        
        check=0;
        for (i = 0; i < tab.length-1; i++) { 
            if(tab[i] == login)
            	check=1;
        }   
		if(check==1){
			document.getElementById("message2").value="";
			document.getElementById("message2").focus();
			document.getElementById("history").value = 
	        document.getElementById("history").value + "["+from+"]" +msg + "\\\n";
	        document.getElementById("history").scrollTop = document.getElementById("history").scrollHeight
        }*/
	}
	
 	function send(arg) {
 		var url = "http://localhost:8084/MessageSender/test";
		if ( typeof request == 'undefined' ) {
			/* create new request */
				request =  newRequest() ;
			}
            request.open("post", url, true);
            request.setRequestHeader("Content-Type","application/x-javascript;");
            
       
            get(request);
            
            if ( arg.substring(0,4)=="send") {
    			arg = "#Message: \""+document.getElementById("message").value +"\",";
    			arg = arg + "#Nadawca: \""+"mla" +"\",";
    			arg = arg + "#Odbiorca: \""+"all" +"\",";
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
	Zaloguj sie
	 <INPUT TYPE="TEXT" id="LOGIN">
	 <INPUT TYPE="SUBMIT" VALUE="Zaloguj" onclick="zaloguj()">
	 </DIV>
	  <div id="zalogowany" style="display: none;">
	 Prywatne wiadomosci: <br>
	 <textarea id="message2"></textarea><br>
     <INPUT TYPE="SUBMIT" VALUE="Wyslij Znajomym" onclick="send2('1')">
     <INPUT TYPE="SUBMIT" VALUE="Wyslij Kolegom" onclick="send2('2')">
     <INPUT TYPE="SUBMIT" VALUE="Wyslij Przyjaciołom" onclick="send2('3')">
	 </div>
</body> 

</HTML>
