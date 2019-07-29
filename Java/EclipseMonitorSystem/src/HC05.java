// non-functional for Mac OS 10.14.5

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/* 
   add to CLASSPATH:
      Libraries/bluecove-2.1.0/bluecove-2.1.0.jar
   for BlueCove (http://www.bluecove.org/)
 */
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class HC05 {

	boolean scanFinished = false;
	RemoteDevice hc05device;
	String hc05Url;

	public static void main(String[] args) {
		try {
			new HC05().go();
		} catch (Exception ex) {
			Logger.getLogger(HC05.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void go() throws Exception {
		//scan for all devices:
		scanFinished = false;
		LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
			@Override
			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
				try {
					String name = btDevice.getFriendlyName(false);
					System.out.format("%s (%s)\n", name, btDevice.getBluetoothAddress());
					if (name.matches("HC.*")) {
						hc05device = btDevice;
						System.out.println("got it!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void inquiryCompleted(int discType) {
				scanFinished = true;
			}

			@Override
			public void serviceSearchCompleted(int transID, int respCode) {
			}

			@Override
			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			}
		});
		while (!scanFinished) {
			//this is easier to understand (for me) as the thread stuff examples from bluecove
			Thread.sleep(500);
		}

		//search for services:
		UUID uuid = new UUID(0x1101); //scan for btspp://... services (as HC-05 offers it)
		UUID[] searchUuidSet = new UUID[]{uuid};
		int[] attrIDs = new int[]{
				0x0100 // service name
		};
		scanFinished = false;
		LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet,
				hc05device, new DiscoveryListener() {
			@Override
			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			}

			@Override
			public void inquiryCompleted(int discType) {
			}

			@Override
			public void serviceSearchCompleted(int transID, int respCode) {
				scanFinished = true;
			}

			@Override
			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
				for (int i = 0; i < servRecord.length; i++) {
					hc05Url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
					if (hc05Url != null) {
						break; //take the first one
					}
				}
			}
		});

		while (!scanFinished) {
			Thread.sleep(500);
		}

		System.out.println(hc05device.getBluetoothAddress());
		System.out.println(hc05Url);

		//if you know your hc05Url this is all you need:
		StreamConnection streamConnection = (StreamConnection) Connector.open(hc05Url);
		OutputStream os = streamConnection.openOutputStream();
		InputStream is = streamConnection.openInputStream();

		os.write("1".getBytes()); //just send '1' to the device
		os.close();
		is.close();
		streamConnection.close();
	}
}