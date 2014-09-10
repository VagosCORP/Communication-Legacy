package vclibs.communication.deprecated;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import vclibs.communication.Eventos.OnComunicationListener;
import vclibs.communication.Eventos.OnConnectionListener;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Comunics extends AsyncTask<Void, byte[], Integer> {

	public final int NULL = 0;// estado
	public final int WAITING = 1;// estado
	public final int CONNECTED = 2;// estado
	public final int CLIENT = 1;// tcon
	public final int SERVER = 2;// tcon
	final byte[] EN_ESPERA = { 1 };
	final byte[] CONECTADO = { 2 };
	final byte[] IO_EXCEPTION = { 3 };
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

	OnConnectionListener onConnListener;
	OnComunicationListener onCOMListener;

	public void setConnectionListener(OnConnectionListener connListener) {
		onConnListener = connListener;
	}

	public void setComunicationListener(OnComunicationListener comListener) {
		onCOMListener = comListener;
	}

	private void makeToast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	private void wlog(String text) {
		if(tcon == SERVER)
			Log.d("Server",text);
		else if(tcon == CLIENT)
			Log.d("Client",text);
	}

	public Comunics() {
		estado = NULL;
	}

	public Comunics(String ip, int port, Context ui) {
		estado = NULL;
		tcon = CLIENT;
		context = ui;
		isa = new InetSocketAddress(ip, port);
	}

	public Comunics(int port, Context ui) {
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
			e.printStackTrace();
		}
	}

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
						Cortar_Conexion();
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
//			if(rcv.equals(conKiller)) {
//				Cortar_Conexion();
//			}else {
				if (onCOMListener != null)
					onCOMListener.onDataReceived(rcv);
				makeToast("Dato recibido");
//			}
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

	@Override
	protected void onCancelled() {
		// estado = NULL;
		wlog("onCancelled");
		onPostExecute(1);
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Integer result) {
		estado = NULL;
		if (onConnListener != null)
			onConnListener.onConnectionfinished();
		makeToast("onPostexecute");
		super.onPostExecute(result);
	}
}
