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
	public Light light1;
	public Light light2;
	public Light light3;
	public Light light4;
	public Light remote;
	public ButtonF b1;
	public ButtonF b2;
	public ButtonF b3;

	// constructs main control panel
	public ControlPanel() {
		// adds components of panel
		Status st = new Status();
		getContentPane().add(st);

		Control con = new Control();
		getContentPane().add(con);

		// formats frame
		setTitle("Control Panel");
		setSize(1200, 500);
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
			light1 = new Light(RED_OFF, 200, 100);
			light2 = new Light(GREEN_OFF, 433, 100);
			light3 = new Light(RED_OFF, 666, 100);
			light4 = new Light(GREEN_OFF, 900, 100);

			add(light1);
			add(light2);
			add(light3);
			add(light4);

			// creates labels for lights: LIGHT NAMES
			add(new LabelF("Light 1", 200, 50));
			add(new LabelF("Light 2", 433, 50));
			add(new LabelF("Light 3", 666, 50));
			add(new LabelF("Light 4", 900, 50));
		}
	}

	// buttons for control
	public class Control extends JPanel {
		public Control() {
			// formats control
			setPreferredSize(new Dimension(1200,200));
			setLayout(null);
			setBounds(0, 200, 1200, 200);

			// creates light and label
			remote = new Light(YELLOW_ON, 900, 300);
			add(remote);
			add(new LabelF("REMOTE", 900, 250));

			// creates buttons: BUTTON NAMES
			b1 = new ButtonF("Button 1", 200, 300);
			b2 = new ButtonF("Button 2", 433, 300);
			b3 = new ButtonF("Button 3", 666, 300);

			add(b1);
			add(b2);
			add(b3);
		}
	}

	// panel for each light, represented by a circle
	public class Light extends JPanel {
		private Color c;

		public Light(Color c, int x, int y) {
			// formats light
			setPreferredSize(new Dimension(100,100));
			setLayout(null);
			setBounds(x, y, 100, 100);
			this.c = c;
			repaint();
		}

		// recolors light
		public void changeColor() {
			c = ON_OFF.get(c);
			repaint();
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
		public ButtonF(String name, int x, int y) {
			super(name);
			setBounds(x, y, 100, 75);
			setFont(new Font("Arial", Font.PLAIN, 20));
			setFocusPainted(false);
		}
	}

}