package vclibs.communication.android;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;
import vclibs.communication.Inf;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Comunic extends AsyncTask<Void, byte[], Integer> {

	public final String version = Inf.version;
	public final int NULL = Inf.NULL;// estado
	public final int WAITING = Inf.WAITING;// estado
	public final int CONNECTED = Inf.CONNECTED;// estado
	public final int CLIENT = Inf.CLIENT;// tcon
	public final int SERVER = Inf.SERVER;// tcon
	final byte[] EN_ESPERA = { 1 };
	final byte[] CONECTADO = { 2 };
	final byte[] IO_EXCEPTION = { 3 };
	final byte[] CONEXION_PERDIDA = { 4 };
	final byte[] DATO_RECIBIDO = { 7 };
	InetSocketAddress isa;
	int sPort;
	Socket socket;
	ServerSocket serverSocket;
	DataInputStream inputSt;
	DataOutputStream outputSt;
	boolean timeOutEnabled = false;
	Context context;
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

	private void makeToast(String text) {
		if(idebug) {
//			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			if(tcon == SERVER)
				Log.i("Server",text);
			else if(tcon == CLIENT)
				Log.i("Client",text);
		}
	}

	private void wlog(String text) {
		if(debug) {
			if(tcon == SERVER)
				Log.d("Server",text);
			else if(tcon == CLIENT)
				Log.d("Client",text);
		}
	}

	public Comunic() {
		estado = NULL;
	}

	@Deprecated
	public Comunic(String ip, int port, Context ui) {
		estado = NULL;
		tcon = CLIENT;
		context = ui;
		isa = new InetSocketAddress(ip, port);
	}
	
	public Comunic(Context ui, String ip, int port) {
		estado = NULL;
		tcon = CLIENT;
		context = ui;
		isa = new InetSocketAddress(ip, port);
	}

	@Deprecated
	public Comunic(int port, Context ui) {
		estado = NULL;
		tcon = SERVER;
		context = ui;
		sPort = port;
	}
	
	public Comunic(Context ui, int port) {
		estado = NULL;
		tcon = SERVER;
		context = ui;
		sPort = port;
	}

	public void enviar(String dato) {
//		Log.d("Comunic", "Enviar String: " + dato);
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
//		Log.d("Comunic", "Enviar int: " + dato);
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
				makeToast(Inf.ESPERA_DETENIDA);
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

	@Override
	protected void onPreExecute() {
		estado = NULL;
		socket = null;
		serverSocket = null;
		conectado = false;
		super.onPreExecute();
	}

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
					}else
						publishProgress(CONEXION_PERDIDA);
				}
				conectado = false;
				inputSt.close();
				outputSt.close();
				if (socket != null)
					socket.close();
			}
		} catch (IOException e) {
			wlog(Inf.IO_EXCEPTION);
			publishProgress(IO_EXCEPTION);
			wlog(e.getMessage());
			if(edebug)
				e.printStackTrace();
		}
		return null;
	}

	public void EnTimeOut(final long ms) {
		if(!timeOutEnabled) {
			final int sender = tcon;
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(ms);
					} catch (InterruptedException e) {
						if(edebug)
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
	
	@Override
	protected void onProgressUpdate(byte[]... values) {
		byte[] orden = values[0];
		if (orden == EN_ESPERA) {
			estado = WAITING;
			makeToast(Inf.EN_ESPERA);
		} else if (orden == DATO_RECIBIDO) {
			int len = Integer.parseInt(new String(values[1]));
			byte[] buffer = values[2];
			String rcv = new String(buffer, 0, len);
			if (onCOMListener != null)
				onCOMListener.onDataReceived(rcv);
			makeToast(Inf.DATO_RECIBIDO);
			wlog(rcv);
		} else if (orden == CONECTADO) {
			estado = CONNECTED;
			if (onConnListener != null)
				onConnListener.onConnectionstablished();
			makeToast(Inf.CONECTADO);
		} else if (orden == IO_EXCEPTION) {
			// makeToast(Inf.IO_EXCEPTION);
			estado = NULL;
		} else if (orden == CONEXION_PERDIDA) {
			makeToast(Inf.CONEXION_PERDIDA);
			Cortar_Conexion();
		}
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		wlog(Inf.ON_CANCELLED);
		onPostExecute(1);
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Integer result) {
		estado = NULL;
		if (onConnListener != null)
			onConnListener.onConnectionfinished();
		makeToast(Inf.ON_POSTEXEC);
		super.onPostExecute(result);
	}
}