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
	private static final int INSERT_SIGN = 5;
	private static final int REMOVE_SIGN = 6;
	private static final int PUSH_SIGN = 7;
	private static final int PULL_SIGN = 8;
	private static final int OPEN_V_SIGN = 9;
	private static final int CLOSE_V_SIGN = 10;
	private static final int OPEN_S_SIGN = 11;
	private static final int CLOSE_S_SIGN = 12;
	
	// booleans for status
	private boolean isMoving = false;
	private boolean remoteControl = false;
	
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
		// adds button events: CHANGE BEHAVIOR
		cp.insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputSignal(INSERT_SIGN);			
			}          
		});
		cp.remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputSignal(REMOVE_SIGN);				
			}          
		});
		cp.pushPiston.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputSignal(PUSH_SIGN);
			}          
		});
		cp.pullPiston.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputSignal(PULL_SIGN);
			}          
		});
		cp.openValve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputSignal(OPEN_V_SIGN);
			}          
		});
		cp.closeValve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputSignal(CLOSE_V_SIGN);
			}          
		});
		cp.openScraper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputSignal(OPEN_S_SIGN);
			}          
		});
		cp.closeScraper.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outputSignal(CLOSE_S_SIGN);
			}          
		});
		
		// creates charts
		tempChart = new LineChart("Temperature vs. Time", "Temperature (C)");
		pressChart = new LineChart("Pressure vs. Time", "Pressure (MPa)");
	}

	public void findPort() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// find an instance of serial port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if (currPortId.getName().startsWith("/dev/tty.usbmodem")) { // device for Mac, change for other OS
				portId = currPortId;									// for Windows: "COM"
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
				// detects and reads serial input: CHANGE BEHAVIOR
				String inputLine = input.readLine();
				System.out.println("Java Input: " + inputLine);
				if (inputLine.startsWith("START")) {
					tempChart.reset();
					pressChart.reset();
				
				// data processing
				} else if (inputLine.startsWith("TEMP:")) {
					String line = inputLine.substring(5);
					String[] vals = line.split(":");
					tempChart.add(Integer.parseInt(vals[0]), Double.parseDouble(vals[1]));
				} else if (inputLine.startsWith("PRESS:")) {
					String line = inputLine.substring(6);
					String[] vals = line.split(":");
					pressChart.add(Integer.parseInt(vals[0]), Double.parseDouble(vals[1]));
				} else if (inputLine.startsWith("PUSH:")) {
					// store push time-stamp data
				} else if (inputLine.startsWith("PULL:")) {
					// store pull time-stamp data
				
				// signals
				} else if (inputLine.startsWith("MOVE")) {
					if (inputLine.contains(":START")) {
						isMoving = true;
					} else if (inputLine.contains(":END")) {
						isMoving = false;
					}
				} else if (inputLine.startsWith("REMOTE")) {
					if (inputLine.contains(":ON")) {
						remoteControl = true;
						cp.remote.turnOn();
					} else if (inputLine.contains(":OFF")) {
						remoteControl = false;
						cp.remote.turnOff();
					}
				} else if (inputLine.startsWith("PISTON")) {
					if (inputLine.contains(":UP")) {
						if (inputLine.contains(":ON")) {
							cp.up.turnOn();
						} else if (inputLine.contains(":OFF")) {
							cp.up.turnOff();
						}
					} else if (inputLine.contains(":DOWN")) {
						if (inputLine.contains(":ON")) {
							cp.down.turnOn();
						} else if (inputLine.contains(":OFF")) {
							cp.down.turnOff();
						}
					}
				} else if (inputLine.startsWith("VALVE")) {
					if (inputLine.contains(":OPEN")) {
						if (inputLine.contains(":ON")) {
							cp.open.turnOn();
						} else if (inputLine.contains(":OFF")) {
							cp.open.turnOff();
						}
					} else if (inputLine.contains("CLOSED")) {
						if (inputLine.contains(":ON")) {
							cp.close.turnOn();
						} else if (inputLine.contains(":OFF")) {
							cp.close.turnOff();
						}
					}
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}
	
	public void outputSignal(int signal) {
		if (output != null && remoteControl && !isMoving) {
			try {
				output.write(signal);
			} catch (IOException e) {
				System.err.println(e.toString());
			}
		}	
	}

}