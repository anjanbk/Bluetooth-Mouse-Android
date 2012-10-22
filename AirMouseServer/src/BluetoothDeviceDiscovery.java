import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.*;

public class BluetoothDeviceDiscovery {
	public static final Vector<RemoteDevice> devicesDiscovered = new Vector<RemoteDevice>();
	
	public static void main(String[] args) throws BluetoothStateException, InterruptedException {
		final Object inquiryCompletedEvent = new Object();
		
		devicesDiscovered.clear();
		
		DiscoveryListener listener = new DiscoveryListener() {
			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
				System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
				devicesDiscovered.addElement(btDevice);
				
				try {
					System.out.println("\t\t name " + btDevice.getFriendlyName(false));
				} catch (IOException cantGetDeviceName) {
					cantGetDeviceName.printStackTrace();
				}
			}
			
			public void inquiryCompleted(int discType) {
				System.out.println("Inquiry Completed");
				synchronized(inquiryCompletedEvent) {
					inquiryCompletedEvent.notifyAll();
				}
			}

			@Override
			public void serviceSearchCompleted(int arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void servicesDiscovered(int arg0, ServiceRecord[] arg1) {
				// TODO Auto-generated method stub
				
			}
		};
		
		synchronized(inquiryCompletedEvent) {
			boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
			if (started) {
				System.out.println("Waiting for device search to complete...");
				inquiryCompletedEvent.wait();
				System.out.println(devicesDiscovered.size() + " device(s) found");
			}
		}
	}
}
