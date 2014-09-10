package vclibs.communication;

public class Eventos {

	public interface OnComunicationListener {
		
		public void onDataReceived(String dato);
		
	}
	
	public interface OnConnectionListener {
		
		public void onConnectionstablished();
		public void onConnectionfinished();
		
	}

	@Deprecated
	public interface OnDisConnectionListener {
		
		public void onConnectionfinished();
		
	}
	
	public interface OnTimeOutListener {
		
		public void onTimeOutEnabled();
		public void onTimeOutCancelled();
		public void onTimeOut();
		
	}
}
