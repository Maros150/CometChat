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


//-------------------------------------------------kompozyt-----------------------------------------------------------\\
abstract class Hierarchy {
	public abstract void SendMessege();
	public abstract void AddFriend(Hierarchy a, int b);
	public abstract int Minimum();
	public abstract void Read();
}

class Friend extends Hierarchy{
	String message;
	int from;
	int to;
	public Friend(String message2, int from2, int to2){
		message=message2;
		from = from2;
		to=to2;
	}
	@Override
	public void SendMessege() {
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/users","admin","admin");
			String query = "select max(idWiadomosci)+1 from wiadomosci";
			java.sql.PreparedStatement statement = con.prepareStatement(query);
			ResultSet  result = statement.executeQuery(); 
			result.next();
			int id = result.getInt(1);
			
			query = "insert into wiadomosci(idWiadomosci,idNadawcy,tresc,idOdbiorcy) values (?, ?, ?, ?)";
			statement = con.prepareStatement(query);
			statement.setInt(1, id);
			statement.setInt(2, from);
			statement.setString(3, message);
			statement.setInt(4, to);
			statement.executeUpdate(); 
			}
		catch(Exception e) {System.out.println(e.getMessage());}
		}
	@Override
	public void AddFriend(Hierarchy a,int b) {}
	@Override
	public int Minimum() {
		return 3;
	}
	@Override
	public void Read() {
		System.out.println("wiadomosc: " + message + " id grupy: " + to);
	}	
}

class Group extends Hierarchy{
	int Id;
	public Group(int id){
		Id=id;
		if(id-1>0)
			users.add(new Group(id-1));
	}
	public ArrayList<Hierarchy> users = new ArrayList<Hierarchy>();
	@Override
	public void SendMessege() {
		for(Iterator<Hierarchy> i = users.iterator(); i.hasNext(); ) {
			i.next().SendMessege();
		}
	}
	
	public void AddFriend(Hierarchy f,int Gid){
		if(Gid == Id)
			users.add(f);
		else
			for(Iterator<Hierarchy> i = users.iterator(); i.hasNext(); ) {
				i.next().AddFriend(f,Gid);
			}
	}
	public int Minimum() { //zwraca Id najniższego poziomu
		int min=3;
		if(users.size()==0)
			return Id;
		else{
			for(Iterator<Hierarchy> i = users.iterator(); i.hasNext(); ) {
				if(i.next().Minimum()<min)
					min=i.next().Minimum();
			}
			return min;
		}
	}

	@Override
	public void Read() {
		for(Iterator<Hierarchy> i = users.iterator(); i.hasNext(); ) {
				i.next().Read();
		}
		
	}
	
}
//-------------------------------------------------kompozyt-----------------------------------------------------------\\


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
		Request request = new Request(event.getHttpServletRequest());
        HttpServletResponse response = event.getHttpServletResponse();
        if (event.getEventType() == CometEvent.EventType.BEGIN) {
        	System.out.println("BEGIN");
            request.setAttribute("org.apache.tomcat.comet.timeout", TIMEOUT);
            
            log("Begin for session: " + request.getSessionId());
			synchronized(connections) {
				connections.add(response);
			}
        } else if (event.getEventType() == CometEvent.EventType.ERROR) {
        	System.out.println("ERROR");
        	log("Error for session: " + request.getSessionId());
			synchronized(connections) {
				connections.remove(response);
			}
            event.close();
        } else if (event.getEventType() == CometEvent.EventType.END) {
        	System.out.println("END");
        	log("End for session: " + request.getSessionId());
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
					+ " for session: " + request.getSessionId());
				} else if (n < 0) {
					error(event, request.getRequest(), response);
					return;
				}
			} while (is.available() > 0);
			messageSender.send(new Message(request.getNadawca(message.toString()), request.getAdresat(message.toString()), request.getMessage(message.toString())));       	    
        }

    }
	
	
	public class MessageSender implements Runnable {

		protected boolean running = true;
		protected ArrayList<Message> messages = new ArrayList<Message>();

		public MessageSender() {
		}

		public void stop() {
			running = false;
		}

		/**
		 * Add message for sending.
		 */
		
		public void send(Message wiadomosc) {
			synchronized (messages) {
				messages.add(wiadomosc);
				messages.notify();
			}
		}

		//to będzie używało kompozytu
		public  void GenerateTree(int idUser, int idGroup,String message){
			root = new Group(3);
			try{
			for(int grupa=3;grupa>=idGroup;grupa--){ //dla wszystkich grup o podanym id i ważniejszych
				//pobieramy wszytskich użytkowników z danej grupy
					Class.forName("com.mysql.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/users","admin","admin");
					String query = "select idZnajomego from znajomi where MojeId=? and idGrupy = ?";
					java.sql.PreparedStatement statement = con.prepareStatement(query);
					statement.setInt(1, idUser);
					statement.setInt(2, grupa);
					ResultSet  result = statement.executeQuery();
					//wstawiamy do drzewa --- w drzewie będą id znajomych z danej grupy
					while(result.next())
					{
						root.AddFriend(new Friend(message,idUser,result.getInt(1)),grupa);
					}					
				}
			root.SendMessege();
			}
			catch(Exception e) {System.out.println(e.getMessage());}
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
					String pendingMessages = "";
					synchronized (messages) {
						for (int j = 0; j < messages.size(); j++) {
							pendingMessages += "["+messages.get(j).nadawca +"]: "
											+messages.get(j).wiadomosc +"\n";
						}
						messages.clear();
					}
					// Send any pending message on all the open connections
					for (int i = 0; i < connections.size(); i++) {
						try {
							PrintWriter writer = connections.get(i).getWriter();
							for (int j = 0; j < pendingMessages.length(); j++) {
								writer.print(pendingMessages.charAt(j));
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

