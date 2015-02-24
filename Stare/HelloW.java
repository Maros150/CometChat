import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;




public class HelloW extends HttpServlet implements CometProcessor{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2040902789691199567L;
	
	private static final Integer TIMEOUT = 60 * 1000;
	protected ArrayList<HttpServletResponse> connections = 
			new ArrayList<HttpServletResponse>();
		protected MessageSender messageSender = null;

		@Override
		public void init() throws ServletException {
			messageSender = new MessageSender();
			Thread messageSenderThread = 
				new Thread(messageSender, "MessageSender[" + getServletContext().getContextPath() + "]");
			messageSenderThread.setDaemon(true);
			messageSenderThread.start();
		}

		@Override
		public void destroy() {
			connections.clear();
			messageSender.stop();
			messageSender = null;
		}

	
	@Override
	public void event(final CometEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getHttpServletRequest();
        HttpServletResponse response = event.getHttpServletResponse();
        if (event.getEventType() == CometEvent.EventType.BEGIN) {
        	System.out.println("BEGIN");
            request.setAttribute("org.apache.tomcat.comet.timeout", TIMEOUT);
            
            log("Begin for session: " + request.getSession(true).getId());
			synchronized(connections) {
				connections.add(response);
			}
        } else if (event.getEventType() == CometEvent.EventType.ERROR) {
        	System.out.println("ERROR");
        	log("Error for session: " + request.getSession(true).getId());
			synchronized(connections) {
				connections.remove(response);
			}
            event.close();
        } else if (event.getEventType() == CometEvent.EventType.END) {
        	System.out.println("END");
        	log("End for session: " + request.getSession(true).getId());
			synchronized(connections) {
				connections.remove(response);
			}
            event.close();
        } else if (event.getEventType() == CometEvent.EventType.READ) {
        	System.out.println("READ");
        	InputStream is = request.getInputStream();
			StringBuilder message = new StringBuilder();
			byte[] buf = new byte[512];
			do {
				int n = is.read(buf); //can throw an IOException
				if (n > 0) {
					message.append(new String(buf, 0, n));
					log("Read " + n + " bytes: " + new String(buf, 0, n) 
					+ " for session: " + request.getSession(true).getId());
				} else if (n < 0) {
					error(event, request, response);
					return;
				}
			} while (is.available() > 0);
			messageSender.send(request.getSession(true).getId().substring(0, 4), message.toString());       	    
        }

    }
	
	
	public class MessageSender implements Runnable {

		protected boolean running = true;
		protected ArrayList<String> messages = new ArrayList<String>();

		public MessageSender() {
		}

		public void stop() {
			running = false;
		}

		/**
		 * Add message for sending.
		 */
		
		public void send(String user, String message) {
			synchronized (messages) {
				messages.add("[" + user + "]: " + message);
				messages.notify();
			}
		}

		public void run() {

			while (running) {

				if (messages.size() == 0) {
					try {
						synchronized (messages) {
							messages.wait();
						}
					} catch (InterruptedException e) {
						// Ignore
					}
				}

				synchronized (connections) {
					String[] pendingMessages = null;
					synchronized (messages) {
						pendingMessages = messages.toArray(new String[0]);
						messages.clear();
					}
					// Send any pending message on all the open connections
					for (int i = 0; i < connections.size(); i++) {
						try {
							PrintWriter writer = connections.get(i).getWriter();
							for (int j = 0; j < pendingMessages.length; j++) {
								writer.println(pendingMessages[j]);
							}
							writer.flush();
							writer.close();	/* the response will not be sent until the writer is closed */
						} catch (IOException e) {
							log("IOExeption sending message", e);
						}
					}
				}

			}

		}

	}
	public void error(CometEvent event, HttpServletRequest request, 
			HttpServletResponse response) {
		System.out.printf("Error: %s, %s, %s\n", event.toString(), request.toString(), response.toString());

	}

}

