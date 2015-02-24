
public class Message {
	String nadawca;
	String adresat;
	String wiadomosc;
	
	public Message(String nadawca, String adresat, String wiadomosc){
		this.adresat = adresat;
		this.nadawca = nadawca;
		this.wiadomosc = wiadomosc;
	}
	public String getAdresat(){
		return adresat;
	}
	public String getNadawca(){
		return nadawca;
	}
	public String getWiadomosc(){
		return wiadomosc;
	}
}
