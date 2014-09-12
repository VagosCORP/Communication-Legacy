package vclibs.communication.android;

import vclibs.communication.Eventos.OnTimeOutListener;
import android.os.AsyncTask;
import android.util.Log;

//Clase Temporizador, espera un tiempo en un hilo secundario
public class TimeOut extends AsyncTask<Long, Void, Integer> {
	
	//Variables para seleccionar qu� imprimir en la Consola
	public boolean idebug = true;
	public boolean edebug = true;
	
	OnTimeOutListener onTOListener;
	
	public void setTimeOutListener(OnTimeOutListener tOListener) {
		onTOListener = tOListener;
	}
	
	//Acciones anteriores al inicio del hilo de ejecusi�n secundario
	@Override
	protected void onPreExecute() {
		if(idebug)
			Log.d("TimeOut", "onPreExecute");
		if(onTOListener != null)
			onTOListener.onTimeOutEnabled();
		super.onPreExecute();
	}

	//Funci�n del hilo de ejecuci�n secundario
	@Override
	protected Integer doInBackground(Long... params) {
		if(idebug)
			Log.d("TimeOut", "doInBackground");
		long ms = params[0]/10;
		try {
			for(int i=0; !isCancelled() && i < ms; i++) {
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			if(edebug)
				e.printStackTrace();
		}
		return 1;
	}

	//Acciones ante cancelaci�n de Actividad del hilo
	@Override
	protected void onCancelled() {
		if(idebug)
			Log.d("TimeOut", "onCancelled");
		if(onTOListener != null)
			onTOListener.onTimeOutCancelled();
		super.onCancelled();
	}

	//Acciones ante la finalizaci�n de acciones del hilo
	@Override
	protected void onPostExecute(Integer result) {
		if(idebug)
			Log.d("TimeOut", "onPostExecute");
		if(onTOListener != null)
			onTOListener.onTimeOut();
		super.onPostExecute(result);
	}
}