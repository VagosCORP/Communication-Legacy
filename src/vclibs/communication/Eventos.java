package vclibs.communication;

public class Eventos {

	/**
	 * The listener interface for receiving onComunication events.
	 * The class that is interested in processing a onComunication
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnComunicationListener<code> method. When
	 * the onComunication event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnComunicationEvent
	 */
	public interface OnComunicationListener {
		
		/**
		 * On data received.
		 *
		 * @param dato the dato
		 */
		public void onDataReceived(String dato);
	}
	
	/**
	 * The listener interface for receiving onConnection events.
	 * The class that is interested in processing a onConnection
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnConnectionListener<code> method. When
	 * the onConnection event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnConnectionEvent
	 */
	public interface OnConnectionListener {
		
		/**
		 * On connectionstablished.
		 */
		public void onConnectionstablished();

		/**
		 * On connectionfinished.
		 */
		public void onConnectionfinished();
	}

	/**
	 * The listener interface for receiving onDisConnection events.
	 * The class that is interested in processing a onDisConnection
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnDisConnectionListener<code> method. When
	 * the onDisConnection event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnDisConnectionEvent
	 */
	public interface OnDisConnectionListener {
		
		/**
		 * On connectionfinished.
		 */
		public void onConnectionfinished();
	}
	
	/**
	 * The listener interface for receiving onTimeOut events.
	 * The class that is interested in processing a onTimeOut
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnTimeOutListener<code> method. When
	 * the onTimeOut event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnTimeOutEvent
	 */
	public interface OnTimeOutListener {
		
		/**
		 * On time out enabled.
		 */
		public void onTimeOutEnabled();
		
		/**
		 * On time out cancelled.
		 */
		public void onTimeOutCancelled();
		
		/**
		 * On time out.
		 */
		public void onTimeOut();
	}
	
}
