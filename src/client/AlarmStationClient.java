package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AlarmStationClient extends Thread{

	BufferedReader in;
	PrintWriter out;
//	JFrame frame = new JFrame("Alarm Station " + getSerial());
//	JTextArea messageArea = new JTextArea(8, 40);
//	Boolean enable = false;
	
	String serverAddress;
	int serverPort;
	String serial;

	public AlarmStationClient(String serverAddress, int serverPort, String serial) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.serial = serial;
		System.out.println("Alarm Station "+ serial + " connected");
		// Layout GUI
//		messageArea.setEditable(false);
//		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
//		frame.pack();

	}
	
	private int getServerPort() {
		return serverPort;
	}

	/**
	 * Prompt for and return the address of the server.
	 */
	private String getServerAddress() {
		return serverAddress;
	}

	/**
	 * Prompt for and return the desired screen name.
	 */
	private String getClientId() {
		return "ALARM_STATION";
	}

	private String getSerial() {
		return serial;
	}

	@Override
	public void run() {

		// Make connection and initialize streams
		try {
			String serverAddress = getServerAddress();
			Socket socket = new Socket(serverAddress, getServerPort());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);

			// Process all messages from server, according to the protocol.
			while (true) {
				String line = in.readLine();
				System.out.println(line);
				if (line.startsWith("CLIENT")) {
					String id = getClientId();
					out.println(id);
					String serial = getSerial();
					out.println(serial);
				} else if (line.startsWith("NAMEACCEPTED")) {
//					enable = true;
				} else if (line.startsWith("MESSAGE")) {
					String serial = line.split(" ")[4];
					if (serial.equals(getSerial())) {
						if(!line.contains("RECEIVED!")) {
//							messageArea.append(line.substring(8) + "\n");
							System.out.println(line.substring(8)+ "\n");
							out.println("RECEIVED!");
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
	}

	public static void main(String[] args) throws Exception {
		for(int i = 0; i<500; i++) {
			AlarmStationClient client = new AlarmStationClient(/*"ec2-52-67-107-195.sa-east-1.compute.amazonaws.com"*/"localhost", 9001, Integer.toString(i));
			client.start();
		}
//		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		client.frame.setVisible(true);
	}
}
