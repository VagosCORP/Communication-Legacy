package vclibs.communication;

//Clase Organizada de Eventos, disponibles según las necesidades
public class Eventos {
	
	public interface OnComunicationListener {
		//Evento lanzado al recibir información,
		//pasa los datos recibnidos como Texto en la variable dato
		public void onDataReceived(String dato);
		
	}
	
	public interface OnConnectionListener {
		//Eventos lanzados ante la Conexión o Desconexión a otro dispositivo
		public void onConnectionstablished();
		public void onConnectionfinished();
		
	}
	
	public interface OnTimeOutListener {
		//Eventos Lanzados según la etapa de vida del TimeOut
		//ya sea Inicio, cancelación o finalización
		public void onTimeOutEnabled();
		public void onTimeOutCancelled();
		public void onTimeOut();
		
	}
}