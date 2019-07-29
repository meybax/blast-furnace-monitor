// MAIN PROGRAM

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*	In Eclipse,
 * 		Go to Project -> Properties -> Java Build Path -> Libraries -> Classpath
 * 		Add external Jar RXTXcomm.jar in "Java/Libraries/rxtx-2.1"
 * 		Add Native Library path "Java/Libraries/rxtx-2.1"
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;

public class Monitor implements SerialPortEventListener {
	
	// variables for serial port communication
	SerialPort serialPort;
	private BufferedReader input;
	private OutputStream output;
	private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 9600;
	
	// variables for UI
	private ControlPanel cp;
	private LineChart tempChart;
	private LineChart pressChart;
	
	// variables defining byte code for communication (Integer from 0-255)
	private static final int TEMP_SIGN = 2;
	private static final int PRESS_SIGN = 3;
	private static final int LED_SIGN = 4;

	public static void main(String[] args) {
		new Monitor().run(args);
	}

	public void run(String[] args) {
		// closes serial port at end of program runtime
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.out.println("END");
				if (serialPort != null) {
					serialPort.removeEventListener();
					serialPort.close();
				}
			}
		}, "Shutdown-thread"));
		findPort();

		cp = new ControlPanel();
		// adds button events
		cp.b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (output != null) {
					try {
						output.write(TEMP_SIGN);
					} catch (IOException e) {
						System.err.println(e.toString());
					}
				}				
			}          
		});
		cp.b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (output != null) {
					try {
						output.write(PRESS_SIGN);
					} catch (IOException e) {
						System.err.println(e.toString());
					}
				}				
			}          
		});
		cp.b3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (output != null) {
					try {
						output.write(LED_SIGN);
					} catch (IOException e) {
						System.err.println(e.toString());
					}
				}
			}          
		});
		
		// creates charts
		tempChart = new LineChart("Temperature vs. Time", "Temperature (C)");
		pressChart = new LineChart("Pressure vs. Time", "Pressure (mPa)");
	}

	public void findPort() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// find an instance of serial port
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
			// open serial port, and use class name for the appName
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
	}

	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				// detects and reads serial input
				String inputLine = input.readLine();
				System.out.println("Java Input: " + inputLine);
				if (inputLine.startsWith("START")) {
					tempChart.reset();
					pressChart.reset();
				} else if (inputLine.startsWith("REMOTE")) {
					cp.remote.changeColor();
				} else if (inputLine.startsWith("TEMP:")) {
					String line = inputLine.substring(5);
					String[] vals = line.split(":");
					tempChart.add(Integer.parseInt(vals[0]), Double.parseDouble(vals[1]));
				} else if (inputLine.startsWith("PRESS:")) {
					String line = inputLine.substring(6);
					String[] vals = line.split(":");
					pressChart.add(Integer.parseInt(vals[0]), Double.parseDouble(vals[1]) / 10);
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

}