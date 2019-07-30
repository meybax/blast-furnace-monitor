// JFRAME MAIN GRAPHICS

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ControlPanel extends JFrame {
	
	// variables storing color data
	private static final Color RED_OFF = new Color(255, 0, 0, 50);
	private static final Color RED_ON = new Color(255, 0, 0, 255);
	private static final Color GREEN_OFF = new Color(0, 255, 0, 50);
	private static final Color GREEN_ON = new Color(0, 255, 0, 255);
	private static final Color YELLOW_OFF = new Color(255, 255, 0, 50);
	private static final Color YELLOW_ON = new Color(255, 255, 0, 255);
	private static final Map<Color, Color> ON_OFF = new HashMap<Color, Color>() {{
		put(RED_OFF, RED_ON);
		put(RED_ON, RED_OFF);
		put(GREEN_OFF, GREEN_ON);
		put(GREEN_ON, GREEN_OFF);
		put(YELLOW_OFF, YELLOW_ON);
		put(YELLOW_ON, YELLOW_OFF);
	}};
	
	// accessible variables to turn lights on or off
	public Light open;
	public Light close;
	public Light up;
	public Light down;
	public Light remote;
	public ButtonF insert;
	public ButtonF remove;
	public ButtonF pushPiston;
	public ButtonF pullPiston;
	public ButtonF openValve;
	public ButtonF closeValve;
	public ButtonF openScraper;
	public ButtonF closeScraper;

	// constructs main control panel
	public ControlPanel() {
		// adds components of panel
		Status st = new Status();
		getContentPane().add(st);

		Control con = new Control();
		getContentPane().add(con);

		// formats frame
		setTitle("Control Panel");
		setSize(1200, 700);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}

	// panel for status represented by lights
	public class Status extends JPanel {
		public Status() {
			// formats status panel
			setPreferredSize(new Dimension(1200,200));
			setLayout(null);
			setBounds(0, 0, 1200, 200);

			// creates lights 
			open = new Light(GREEN_OFF, 200, 100, false);
			close = new Light(RED_ON, 433, 100, true);
			up = new Light(GREEN_OFF, 666, 100, false);
			down = new Light(RED_ON, 900, 100, true);

			add(open);
			add(close);
			add(up);
			add(down);

			// creates labels for lights: LIGHT NAMES
			add(new LabelF("OPEN", 200, 50));
			add(new LabelF("CLOSE", 433, 50));
			add(new LabelF("UP", 666, 50));
			add(new LabelF("DOWN", 900, 50));
		}
	}

	// buttons for control
	public class Control extends JPanel {
		public Control() {
			// formats control
			setPreferredSize(new Dimension(1200,400));
			setLayout(null);
			setBounds(0, 200, 1200, 200);

			// creates light and label
			remote = new Light(YELLOW_OFF, 900, 300, false);
			add(remote);
			add(new LabelF("REMOTE", 900, 250));

			// creates buttons: BUTTON NAMES
			insert = new ButtonF("INSERT", 200, 310, false);
			remove = new ButtonF("REMOVE", 525, 310, false);
			pushPiston = new ButtonF("Push Piston", 200, 500, true);
			pullPiston = new ButtonF("Pull Piston", 340, 500, true);
			openValve = new ButtonF("Open Valve", 480, 500, true);
			closeValve = new ButtonF("Close Valve", 620, 500, true);
			openScraper = new ButtonF("Open Scraper", 760, 500, true);
			closeScraper = new ButtonF("Close Scraper", 900, 500, true);

			add(insert);
			add(remove);
			add(pushPiston);
			add(pullPiston);
			add(openValve);
			add(closeValve);
			add(openScraper);
			add(closeScraper);
		}
	}

	// panel for each light, represented by a circle
	public class Light extends JPanel {
		private Color c;
		private boolean on;

		public Light(Color c, int x, int y, boolean on) {
			// formats light
			setPreferredSize(new Dimension(100,100));
			setLayout(null);
			setBounds(x, y, 100, 100);
			this.c = c;
			this.on = on;
			repaint();
		}
		
		public void turnOn() {
			if (!on) {
				c = ON_OFF.get(c);
				repaint();
				on = true;
			}
		}
		public void turnOff() {
			if (on) {
				c = ON_OFF.get(c);
				repaint();
				on = false;
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(c);
			g.fillOval(0, 0, 100, 100);
			g.setColor(new Color(0, 0, 0));
			g.drawOval(0, 0, 100, 100);
		}
	}

	// label with formatting
	public class LabelF extends JLabel {
		public LabelF(String name, int x, int y) {
			super(name);
			setBounds(x, y, 100, 50);
			setHorizontalAlignment(SwingConstants.CENTER);
			setFont(new Font("Arial", Font.PLAIN, 20));
		}
	}

	// button with formatting
	public class ButtonF extends JButton {
		public ButtonF(String name, int x, int y, boolean small) {
			super(name);
			int fontSize, width;
			if (small) {
				fontSize = 15;
				width = 120;
			} else {
				fontSize = 20;
				width = 300;
			}
			setBounds(x, y, width, 75);
			setFont(new Font("Arial", Font.PLAIN, fontSize));
			setFocusPainted(false);
		}
	}

}