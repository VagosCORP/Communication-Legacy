package vclibs.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;
import vclibs.communication.Eventos.OnDisConnectionListener;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * The Class Comunic: Recurso de Comunicacion para Android basado en Sockets
 */
public class Comunics extends AsyncTask<Void, byte[], Integer> {

	/** Constante de Estado o Tipo de Conexion: Nulo. */
	public final int NULL = 0;// estado
	
	/** Constante de Estado: En Espera. */
	public final int WAITING = 1;// estado
	
	/** Constante de Estado: Conectado. */
	public final int CONNECTED = 2;// estado

	/** Constante de Tipo de Conexion: Cliente. */
	public final int CLIENT = 1;// tcon
	
	/** Constante de Tipo de Conexion: Servidor. */
	public final int SERVER = 2;// tcon

	/** The en espera. */
	final byte[] EN_ESPERA = { 1 };
	
	/** The conectado. */
	final byte[] CONECTADO = { 2 };
	
	/** The io exception. */
	final byte[] IO_EXCEPTION = { 3 };
	
	/** The dato recivido. */
	final byte[] DATO_RECIBIDO = { 7 };
	
	/** The con killer. */
	public static final String conKiller = "cOrTaR-cOnExIoN";

	/** The isa. */
	InetSocketAddress isa;
	
	/** The s port. */
	int sPort;
	
	/** The socket. */
	Socket socket;
	
	/** The server socket. */
	ServerSocket serverSocket;
	
	/** The input st. */
	DataInputStream inputSt;
	
	/** The output st. */
	DataOutputStream outputSt;
	
	/** The time out enabled. */
	boolean timeOutEnabled = false;

	/** The context. */
	Context context;
	
	/** El Tipo de conexion Actual:
	 *  NULL, CLIENT o SERVER. */
	public int tcon = NULL;
	
	/** The conectado. */
	boolean conectado = false;
	
	/** El Estado de la Conexion Actual:
	 * NULL, WAITING o CONNECTED. */
	public int estado = NULL;

	// ///////////////Codigo para Listeners/////////////////
	/** The on conn listener. */
	OnConnectionListener onConnListener;
	
	/** The on dis conn listener. */
	OnDisConnectionListener onDisConnListener;
	
	/** The on com listener. */
	OnComunicationListener onCOMListener;

//	/**
//	 * The listener interface for receiving onComunication events.
//	 * The class that is interested in processing a onComunication
//	 * event implements this interface, and the object created
//	 * with that class is registered with a component using the
//	 * component's <code>addOnComunicationListener<code> method. When
//	 * the onComunication event occurs, that object's appropriate
//	 * method is invoked.
//	 *
//	 * @see OnComunicationEvent
//	 */
//	public interface OnComunicationListener {
//		
//		/**
//		 * On data received.
//		 *
//		 * @param dato the dato
//		 */
//		public void onDataReceived(String dato);
//	}
//
//	/**
//	 * The listener interface for receiving onConnection events.
//	 * The class that is interested in processing a onConnection
//	 * event implements this interface, and the object created
//	 * with that class is registered with a component using the
//	 * component's <code>addOnConnectionListener<code> method. When
//	 * the onConnection event occurs, that object's appropriate
//	 * method is invoked.
//	 *
//	 * @see OnConnectionEvent
//	 */
//	public interface OnConnectionListener {
//		
//		/**
//		 * On connectionstablished.
//		 */
//		public void onConnectionstablished();
//
//		/**
//		 * On connectionfinished.
//		 */
//		public void onConnectionfinished();
//	}
//
//	/**
//	 * The listener interface for receiving onDisConnection events.
//	 * The class that is interested in processing a onDisConnection
//	 * event implements this interface, and the object created
//	 * with that class is registered with a component using the
//	 * component's <code>addOnDisConnectionListener<code> method. When
//	 * the onDisConnection event occurs, that object's appropriate
//	 * method is invoked.
//	 *
//	 * @see OnDisConnectionEvent
//	 */
//	public interface OnDisConnectionListener {
//		
//		/**
//		 * On connectionfinished.
//		 */
//		public void onConnectionfinished();
//	}

	/**
	 * Sets the connection listener.
	 *
	 * @param connListener the new connection listener
	 */
	public void setConnectionListener(OnConnectionListener connListener) {
		onConnListener = connListener;
	}

	/**
	 * Sets the dis connection listener.
	 *
	 * @param disconnListener the new dis connection listener
	 */
	public void setDisConnectionListener(OnDisConnectionListener disconnListener) {
		onDisConnListener = disconnListener;
	}

	/**
	 * Sets the comunication listener.
	 *
	 * @param comListener the new comunication listener
	 */
	public void setComunicationListener(OnComunicationListener comListener) {
		onCOMListener = comListener;
	}

	// ///////////////Codigo para Listeners/////////////////

	/**
	 * Make toast.
	 *
	 * @param text the text
	 */
	private void makeToast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Wlog.
	 *
	 * @param text the text
	 */
	private void wlog(String text) {
		if(tcon == SERVER)
			Log.d("Server",text);
		else if(tcon == CLIENT)
			Log.d("Client",text);
	}

	/**
	 * Instantiates a new comunic.
	 */
	public Comunics() {
		estado = NULL;
	}

	/**
	 * Instantiates a new comunic.
	 *
	 * @param ip the ip
	 * @param port the port
	 * @param ui the ui
	 */
	public Comunics(String ip, int port, Context ui) {
		estado = NULL;
		tcon = CLIENT;
		context = ui;
		isa = new InetSocketAddress(ip, port);
	}

	/**
	 * Instantiates a new comunic.
	 *
	 * @param port the port
	 * @param ui the ui
	 */
	public Comunics(int port, Context ui) {
		estado = NULL;
		tcon = SERVER;
		context = ui;
		sPort = port;
	}

	/**
	 * Enviar.
	 *
	 * @param dato the dato
	 */
	public void enviar(String dato) {
//		Log.d("Comunic", "Enviar String: " + dato);
		try {
			if (estado == CONNECTED)
				outputSt.writeBytes(dato);
		} catch (IOException e) {
			wlog(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Enviar.
	 *
	 * @param dato the dato
	 */
	public void enviar(int dato) {
//		Log.d("Comunic", "Enviar int: " + dato);
		try {
			if (estado == CONNECTED)
				outputSt.writeByte(dato);
		} catch (IOException e) {
			wlog(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Cortar_ conexion.
	 */
	public void Cortar_Conexion() {
		enviar(conKiller);
		try {
			if (estado == CONNECTED && socket != null) {
				socket.close();
				cancel(true);// socket = null;
			}
		} catch (IOException e) {
			wlog(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Detener_ espera.
	 */
	public void Detener_Espera() {
		try {
			if (estado == WAITING) {
				// cancel(true);
				if (serverSocket != null)
					serverSocket.close();
				makeToast("Espera detenida");
			}
		} catch (IOException e) {
			wlog(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Detener_ actividad.
	 */
	public void Detener_Actividad() {
		Cortar_Conexion();
		Detener_Espera();
//		try {
//			enviar(conKiller);
//			if (estado == CONNECTED && socket != null) {
//				socket.close();
//				cancel(true);// socket = null;
//			}
//			if (estado == EN_SPERA) {
//				if (serverSocket != null)
//					serverSocket.close();
//				wlog("Espera detenida");
//			}
//		}catch (IOException e) {
//			wlog(e.getMessage());
//			e.printStackTrace();
//		}
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		estado = NULL;
		socket = null;
		serverSocket = null;
		conectado = false;
		super.onPreExecute();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Integer doInBackground(Void... params) {
		try {
			if (tcon == CLIENT) {
				socket = new Socket();
				if (socket != null) {
					socket.connect(isa,7000);
				} else
					socket = null;
			} else if (tcon == SERVER) {
				serverSocket = new ServerSocket(sPort);
				if (serverSocket != null) {
					publishProgress(EN_ESPERA);
					socket = serverSocket.accept();
					serverSocket.close();
					serverSocket = null;
				} else
					socket = null;
			}
			if (socket != null && socket.isConnected()) {
				inputSt = new DataInputStream(socket.getInputStream());
				outputSt = new DataOutputStream(socket.getOutputStream());
				conectado = true;
				publishProgress(CONECTADO);
				while (socket.isConnected() && conectado && !isCancelled()) {
					byte[] buffer = new byte[1024];
					int len = inputSt.read(buffer);
					if (len != -1) {
						byte[] blen = (len + "").getBytes();
						publishProgress(DATO_RECIBIDO, blen, buffer);
					}
				}
				conectado = false;
				inputSt.close();
				outputSt.close();
				if (socket != null)
					socket.close();
			}
		} catch (IOException e) {
			wlog("IO Exception");
			publishProgress(IO_EXCEPTION);
			wlog(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * En time out.
	 *
	 * @param ms the ms
	 */
	public void EnTimeOut(final long ms) {
		if(!timeOutEnabled) {
			final int sender = tcon;
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(ms);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}

				@Override
				protected void finalize() throws Throwable {
					if (timeOutEnabled && estado == CONNECTED && tcon == sender) {
						Cortar_Conexion();
					}
					timeOutEnabled = false;
					super.finalize();
				}
			}).run();
		}
	}
	
//	public void DisTimeOut() {
//		if(timeOutEnabled) {
//			tothread.interrupt();
//			timeOutEnabled = false;
//		}
//	}

	/* (non-Javadoc)
 * @see android.os.AsyncTask#onProgressUpdate(java.lang.Object[])
 */
@Override
	protected void onProgressUpdate(byte[]... values) {
		byte[] orden = values[0];
		if (orden == EN_ESPERA) {
			estado = WAITING;
			makeToast("En Espera");
		} else if (orden == DATO_RECIBIDO) {
			int len = Integer.parseInt(new String(values[1]));
			byte[] buffer = values[2];
			String rcv = new String(buffer, 0, len);
			if(rcv.equals(conKiller)) {
				Cortar_Conexion();
			}else {
				if (onCOMListener != null)
					onCOMListener.onDataReceived(rcv);
				makeToast("Dato recibido");
			}
		} else if (orden == CONECTADO) {
			estado = CONNECTED;
			if (onConnListener != null)
				onConnListener.onConnectionstablished();
			makeToast("Conexion establecida");
		} else if (orden == IO_EXCEPTION) {
			// makeToast("IO Exception");
			estado = NULL;
		}
		super.onProgressUpdate(values);
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		// estado = NULL;
		wlog("onCancelled");
		onPostExecute(1);
		super.onCancelled();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Integer result) {
		estado = NULL;
		if (onConnListener != null)
			onConnListener.onConnectionfinished();
		if (onDisConnListener != null)
			onDisConnListener.onConnectionfinished();
		makeToast("onPostexecute");
		super.onPostExecute(result);
	}
}
