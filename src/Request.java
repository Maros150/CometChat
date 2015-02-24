import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;


public class Request {
	 HttpServletRequest request;
	 private String MessageAtr = "#Message: ";
	 private String NadawcaAtr = "#Nadawca: ";
	 private String OdbiorcaAtr = "#Odbiorca: ";
	 
	public Request(HttpServletRequest request){
		this.request = request;
	}
	
	
	public void setAttribute(java.lang.String name, java.lang.Object o){
		 request.setAttribute(name,o);
	}
	public InputStream getInputStream() throws IOException{
	 return request.getInputStream();
	 }
	public String getMessage(String reqMessage){
		int start = reqMessage.indexOf(MessageAtr);
		String temp = reqMessage.substring(start);
		int stop = temp.indexOf("\",");
		return reqMessage.substring(start+MessageAtr.length()+1, start+stop);
	}
	public String getSessionId(){
		return request.getSession(true).getId();
	}
	public HttpServletRequest getRequest(){
		return request;
	}
	public String getNadawca(String reqMessage){
		int start = reqMessage.indexOf(NadawcaAtr);
		String temp = reqMessage.substring(start);
		int stop = temp.indexOf("\",");
		reqMessage.substring(start+NadawcaAtr.length()+1,start + stop);
		return getSessionId().substring(0, 5);
	}
	public String getAdresat(String reqMessage){
		int start = reqMessage.indexOf(OdbiorcaAtr);
		String temp = reqMessage.substring(start);
		int stop = temp.indexOf("\",");
		return reqMessage.substring(start+OdbiorcaAtr.length()+1,start + stop);
	}
}
