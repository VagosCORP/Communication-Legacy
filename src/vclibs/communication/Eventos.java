package vclibs.communication;

//Clase Organizada de Eventos, disponibles seg�n las necesidades
public class Eventos {
	
	public interface OnComunicationListener {
		//Evento lanzado al recibir informaci�n,
		//pasa los datos recibnidos como Texto en la variable dato
		public void onDataReceived(String dato);
		
	}
	
	public interface OnConnectionListener {
		//Eventos lanzados ante la Conexi�n o Desconexi�n a otro dispositivo
		public void onConnectionstablished();
		public void onConnectionfinished();
		
	}
	
	public interface OnTimeOutListener {
		//Eventos Lanzados seg�n la etapa de vida del TimeOut
		//ya sea Inicio, cancelaci�n o finalizaci�n
		public void onTimeOutEnabled();
		public void onTimeOutCancelled();
		public void onTimeOut();
		
	}
}