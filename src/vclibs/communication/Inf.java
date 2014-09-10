package vclibs.communication;

public class Inf {
	public static final String version = "20140909";
	public static final int NULL = 0;// estado
	public static final int WAITING = 1;// estado
	public static final int CONNECTED = 2;// estado
	public static final int CLIENT = 1;// tcon
	public static final int SERVER = 2;// tcon
	
	public static final String EN_ESPERA = "En Espera";
	public static final String DATO_RECIBIDO = "Dato recibido";
	public static final String DATO_RECIBIDOx = "Dato recibido: ";
	public static final String CONECTADO = "Conexion establecida";
	public static final String IO_EXCEPTION = "IO Exception";
	public static final String CONEXION_PERDIDA = "Conexion Perdida";
	public static final String ESPERA_DETENIDA = "Espera detenida";
	public static final String ON_CANCELLED = "onCancelled";
	public static final String ON_POSTEXEC = "onPostexecute";
	
	public static void println(int tcon, String text) {
		if(tcon == SERVER)
			System.out.println("Server - " + text);
		else if(tcon == CLIENT)
			System.out.println("Client - " + text);
	}
}
