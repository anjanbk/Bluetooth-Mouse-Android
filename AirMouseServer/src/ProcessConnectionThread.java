import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;


public class ProcessConnectionThread implements Runnable {
	private StreamConnection mConnection;
	
	public ProcessConnectionThread(StreamConnection connection) {
		mConnection = connection;
	}
	@Override
	public void run() {
		try {
			InputStream inputStream = mConnection.openDataInputStream();
			System.out.println("Waiting for input");
			
			while (true) {
				int command = inputStream.read();
				
				if (command == -1) {
					System.out.println("Finish process");
					break;
				}
				processCommand(command);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processCommand(int command) {
		System.out.println("Command inputted: " + command);
	}

}
