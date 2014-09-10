package vclibs.communication.deprecated;

import vclibs.communication.Eventos.OnTimeOutListener;
import android.os.AsyncTask;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class TimeOut.
 */
public class TimeOuts extends AsyncTask<Long, Void, Integer> {
	
	/** The on to listener. */
	OnTimeOutListener onTOListener;
	
//	/**
//	 * The listener interface for receiving onTimeOut events.
//	 * The class that is interested in processing a onTimeOut
//	 * event implements this interface, and the object created
//	 * with that class is registered with a component using the
//	 * component's <code>addOnTimeOutListener<code> method. When
//	 * the onTimeOut event occurs, that object's appropriate
//	 * method is invoked.
//	 *
//	 * @see OnTimeOutEvent
//	 */
//	public interface OnTimeOutListener {
//		
//		/**
//		 * On time out enabled.
//		 */
//		public void onTimeOutEnabled();
//		
//		/**
//		 * On time out cancelled.
//		 */
//		public void onTimeOutCancelled();
//		
//		/**
//		 * On time out.
//		 */
//		public void onTimeOut();
//	}
	
	/**
	 * Sets the time out listener.
	 *
	 * @param tOListener the new time out listener
	 */
	public void setTimeOutListener(OnTimeOutListener tOListener) {
		onTOListener = tOListener;
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		Log.d("TimeOut", "onPreExecute");
		if(onTOListener != null)
			onTOListener.onTimeOutEnabled();
		super.onPreExecute();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
	 */
	@Override
	protected Integer doInBackground(Long... params) {
		Log.d("TimeOut", "doInBackground");
		long ms = params[0]/10;
		try {
			for(int i=0; !isCancelled() && i < ms; i++) {
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		Log.d("TimeOut", "onCancelled");
		if(onTOListener != null)
			onTOListener.onTimeOutCancelled();
		super.onCancelled();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Integer result) {
		Log.d("TimeOut", "onPostExecute");
		if(onTOListener != null)
			onTOListener.onTimeOut();
		super.onPostExecute(result);
	}
	
}
