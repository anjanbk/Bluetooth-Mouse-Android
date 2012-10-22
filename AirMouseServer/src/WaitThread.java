import javax.bluetooth.*;
import javax.microedition.io.*;

public class WaitThread implements Runnable {

	@Override
	public void run() {
		waitForConnection();
		
	}
	
	private void waitForConnection() {
		LocalDevice local = null;
		StreamConnectionNotifier notifier;
		
		StreamConnection connection = null;
		
		try {
			local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);
			
			UUID uuid = new UUID(80087355);
			String url = "btspp://localhost:"+uuid.toString()+ ";name=RemoteBluetooth";
			notifier = (StreamConnectionNotifier)Connector.open(url);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		while (true) {
			try {
				System.out.println("Waiting for connection...");
				
				Thread processThread = new Thread(new ProcessConnectionThread(connection));
				processThread.start();
			} catch (Exception e) {
				System.out.println("Exception caught while waiting for connection");
				return;
			}
		}
	}

}
