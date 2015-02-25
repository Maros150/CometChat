import java.util.Date;

public class Zegar {
		private static Date date;
	   private static Zegar instance = null;
	   protected Zegar() {
	      // Exists only to defeat instantiation.
	   }
	   public static Zegar getInstance() {
	      if(instance == null) {
	         instance = new Zegar();
	         date = new Date() ;
	      }
	      return instance;
	   }
	   public String getTime(){
		   return date.toString();
	   }
	
	}