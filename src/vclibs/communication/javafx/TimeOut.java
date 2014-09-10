package vclibs.communication.javafx;

import vclibs.communication.Eventos.OnTimeOutListener;
import javafx.concurrent.Task;

public class TimeOut extends Task<Integer> {
	
	long time = 0;
	public boolean idebug = true;
	public boolean edebug = true;
	
	OnTimeOutListener onTOListener;
	
	public void setTimeOutListener(OnTimeOutListener tOListener) {
		onTOListener = tOListener;
	}
	
	public TimeOut(long ms) {
		time = ms;
		onPreExecute();
	}
	
	protected void onPreExecute() {
		if(idebug)
			System.out.println("TimeOut - "+"onPreExecute");
		if(onTOListener != null)
			onTOListener.onTimeOutEnabled();
	}

	@Override
	protected Integer call() throws Exception {
		if(idebug)
			System.out.println("TimeOut - "+"doInBackground");
		long ms = time/10;
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

	@Override
	protected void cancelled() {
		if(idebug)
			System.out.println("TimeOut - "+"onCancelled");
		if(onTOListener != null)
			onTOListener.onTimeOutCancelled();
	}

	@Override
	protected void succeeded() {
		if(idebug)
			System.out.println("TimeOut - "+"onPostExecute");
		if(onTOListener != null)
			onTOListener.onTimeOut();
		super.done();
	}
}
