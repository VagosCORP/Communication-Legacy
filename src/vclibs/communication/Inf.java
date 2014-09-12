package vclibs.communication;

//Clase almacenadora de constantes y funciones globales
public class Inf {
	
	//Constantes de estado y tipo de Conexión
	public static final String version = "20140909";
	public static final int NULL = 0;//estado, Desocupado
	public static final int WAITING = 1;//estado, En espera
	public static final int CONNECTED = 2;//estado, Conectado
	public static final int CLIENT = 1;//Tipo de conexión(tcon), Cliente
	public static final int SERVER = 2;//Tipo de conexión(tcon), Servidor
	
	//Constantes de Texto para la impresión del estado actual
	public static final String EN_ESPERA = "En Espera";
	public static final String DATO_RECIBIDO = "Dato recibido";
	public static final String DATO_RECIBIDOx = "Dato recibido: ";
	public static final String CONECTADO = "Conexion establecida";
	public static final String IO_EXCEPTION = "IO Exception";
	public static final String CONEXION_PERDIDA = "Conexion Perdida";
	public static final String ESPERA_DETENIDA = "Espera detenida";
	public static final String ON_CANCELLED = "onCancelled";
	public static final String ON_POSTEXEC = "onPostexecute";
	 
	/**
	 * Función de impresión de información, depende de tcon y el texto
	 * @param tcon: tipo de conexión
	 * @param text: textp a imprimirse
	 */
	public static void println(int tcon, String text) {
		if(tcon == SERVER)
			System.out.println("Server - " + text);
		else if(tcon == CLIENT)
			System.out.println("Client - " + text);
	}
}