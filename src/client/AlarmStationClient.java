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

public class AlarmStationClient {

	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame("Alarm Station " + getSerial());
	JTextArea messageArea = new JTextArea(8, 40);
	Boolean enable = false;
	Boolean response = true;

	public AlarmStationClient() {

		// Layout GUI
		messageArea.setEditable(false);
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.pack();

	}

	/**
	 * Prompt for and return the address of the server.
	 */
	private String getServerAddress() {
		return "localhost";
	}

	/**
	 * Prompt for and return the desired screen name.
	 */
	private String getId() {
		return "ALARM_STATION";
	}

	private String getSerial() {
		return "989855758";
	}

	private void run() throws IOException {

		// Make connection and initialize streams
		String serverAddress = getServerAddress();
		Socket socket = new Socket(serverAddress, 9001);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		// Process all messages from server, according to the protocol.
		while (true) {
			String line = in.readLine();
			response= true;
			System.out.println(line);
			if (line.startsWith("CLIENT")) {
				String id = getId();
				out.println(id);
				String serial = getSerial();
				out.println(serial);
			} else if (line.startsWith("NAMEACCEPTED")) {
				enable = true;
			} else if (line.startsWith("MESSAGE")) {
				String serial = line.split(" ")[4];
				if (serial.equals(getSerial())) {
					messageArea.append(line.substring(8) + "\n");
					if(response) {
						out.println("RECEIVED!");
						response = false;
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		AlarmStationClient client = new AlarmStationClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}
}
