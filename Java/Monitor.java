// MAIN PROGRAM

import java.awt.event.*;
import java.util.*;

/*
   add to CLASSPATH:
      Libraries/jfreechart-1.0.19/lib/jfreechart-1.0.19.jar
      Libraries/jfreechart-1.0.19/lib/jcommon-1.0.23.jar
   for JFreeChart (http://www.jfree.org/jfreechart/)
*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;


public class Monitor implements SerialPortEventListener {

	SerialPort serialPort;
	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 9600;

	public static void main(String[] args) {
		new Monitor().run(args);
	}

	public void run(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public synchronized void run() {
				if (serialPort != null) {
					serialPort.removeEventListener();
					serialPort.close();
				}
			}
		}, "Shutdown-thread"));
      
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
		   if (currPortId.getName().startsWith("/dev/tty.usbmodem")) {
				portId = currPortId;
				break;
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
      
		ControlPanel cp = new ControlPanel();
		// adds button events
   		cp.b1.addActionListener(new ActionListener() {
   			@Override
   			public void actionPerformed(ActionEvent arg0) {
   				cp.light1.changeColor();				
			}          
   		});
   		cp.b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cp.light2.changeColor();				
			}          
   		});
   		cp.b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cp.remote.changeColor();				
			}          
   		});
      
   		// MAIN LOOP
   		while (true) {
      
   		}
	}
   
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = input.readLine();
				System.out.println(inputLine);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
   
}