package vclibs.communication.javafx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.concurrent.Task;
import vclibs.communication.Inf;
import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;

public class Comunic extends Task<Integer> {

	public final String version = Inf.version;
	public final int NULL = Inf.NULL;// estado
	public final int WAITING = Inf.WAITING;// estado
	public final int CONNECTED = Inf.CONNECTED;// estado
	public final int CLIENT = Inf.CLIENT;// tcon
	public final int SERVER = Inf.SERVER;// tcon
	public final String EN_ESPERA = "EN_ESPERA";//{ 1 };
	public final String CONECTADO = "CONECTADO";//{ 2 };
	final String IO_EXCEPTION = "IO_EXCEPTION";//{ 3 };
	final String CONEXION_PERDIDA = "CONEXION PERDIDA";//{ 4 };
	public final String DATO_RECIBIDO = "DATO_RECIBIDO";//{ 7 };
	InetSocketAddress isa;
	int sPort = 2000;
	Socket socket;
	ServerSocket serverSocket;
	DataInputStream inputSt;
	DataOutputStream outputSt;
	public int tcon = NULL;
	boolean conectado = false;
	public int estado = NULL;
	
	public boolean debug = true;
	public boolean idebug = true;
	public boolean edebug = true;

	OnConnectionListener onConnListener;
	OnComunicationListener onCOMListener;

	public void setConnectionListener(OnConnectionListener connListener) {
		onConnListener = connListener;
	}
	public void setComunicationListener(OnComunicationListener comListener) {
		onCOMListener = comListener;
	}

	private void wlog(String text) {
		if(debug)
			Inf.println(tcon, text);
	}
	
	private void ilog(String text) {
		if(idebug)
			Inf.println(tcon, text);
	}

	public Comunic() {
		estado = NULL;
	}
	
	public Comunic(int port) {
		estado = NULL;
		tcon = SERVER;
		sPort = port;
		onPreExecute();
	}

	public Comunic(String ip, int port) {
		estado = NULL;
		tcon = CLIENT;
		isa = new InetSocketAddress(ip, port);
		onPreExecute();
	}

	public void enviar(String dato) {
		try {
			if (estado == CONNECTED)
				outputSt.writeBytes(dato);
		} catch (IOException e) {
			wlog(e.getMessage());
			if(edebug)
				e.printStackTrace();
		}
	}

	public void enviar(int dato) {
		try {
			if (estado == CONNECTED)
				outputSt.writeByte(dato);
		} catch (IOException e) {
			wlog(e.getMessage());
			if(edebug)
				e.printStackTrace();
		}
	}

	public void Cortar_Conexion() {
		try {	
			if (estado == CONNECTED && socket != null) {
				socket.close();
				cancel(true);// socket = null;
			}
		} catch (IOException e) {
			wlog(e.getMessage());
			if(edebug)
				e.printStackTrace();
		}
	}

	public void Detener_Espera() {
		try {
			if (estado == WAITING) {
				// cancel(true);
				if (serverSocket != null)
					serverSocket.close();
				ilog(Inf.ESPERA_DETENIDA);
			}
		} catch (IOException e) {
			wlog(e.getMessage());
			if(edebug)
				e.printStackTrace();
		}
	}
	
	public void Detener_Actividad() {
		Cortar_Conexion();
		Detener_Espera();
	}

	protected void onPreExecute() {
		estado = NULL;
		socket = null;
		serverSocket = null;
		conectado = false;
	}

	@Override
	protected Integer call() throws Exception {
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
					updateMessage(EN_ESPERA);
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
				updateMessage(CONECTADO);
				while (socket.isConnected() && conectado && !isCancelled()) {
					byte[] buffer = new byte[1024];
					int len = inputSt.read(buffer);
					if (len != -1) {
						String rcv = new String(buffer, 0, len);
						updateMessage(DATO_RECIBIDO);
						updateMessage(rcv);
					}else
						updateMessage(CONEXION_PERDIDA);
				}
				conectado = false;
				inputSt.close();
				outputSt.close();
				if (socket != null)
					socket.close();
			}
		} catch (IOException e) {
			wlog(Inf.IO_EXCEPTION);
			updateMessage(IO_EXCEPTION);
			wlog(e.getMessage());
			if(edebug)
				e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void updateMessage(String message) {
		if (message == EN_ESPERA) {
			estado = WAITING;
			ilog(Inf.EN_ESPERA);
		} else if (message == DATO_RECIBIDO) {
			
		} else if (message == CONECTADO) {
			estado = CONNECTED;
			if (onConnListener != null)
				onConnListener.onConnectionstablished();
			ilog(Inf.CONECTADO);
		} else if (message == IO_EXCEPTION) {
//			wlog(Inf.IO_EXCEPTION);
			estado = NULL;
		} else if (message == CONEXION_PERDIDA) {
			wlog(Inf.CONEXION_PERDIDA);
			Cortar_Conexion();
		} else {
			if (onCOMListener != null)
				onCOMListener.onDataReceived(message);
			wlog(Inf.DATO_RECIBIDOx + message);
		}
		super.updateMessage(message);
	}

	@Override
	protected void cancelled() {
		wlog(Inf.ON_CANCELLED);
		succeeded();
		super.cancelled();
	}
	
	@Override
	protected void succeeded() {
		estado = NULL;
		if (onConnListener != null)
			onConnListener.onConnectionfinished();
		ilog(Inf.ON_POSTEXEC);
		super.succeeded();
	}
}