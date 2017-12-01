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

public class UserClient {

	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame("Client " + getId() + " to "+ getSerial());
	JTextField textField = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);

	public UserClient() {

		// Layout GUI
		textField.setEditable(false);
		messageArea.setEditable(false);
		frame.getContentPane().add(textField, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.pack();

		// Add Listeners
		textField.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				out.println(textField.getText());
				textField.setText("");
			}
		});
	}

	private String getServerAddress() {
		return "localhost";
	}

	private String getId() {
		return "02";
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
			System.out.println(line);
			if (line.startsWith("CLIENT")) {
				String id = getId();
				out.println(id);
				String serial = getSerial();
				out.println(serial);
			} else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
			} else if (line.startsWith("MESSAGE")) {
				String serial = line.split(" ")[4];
				if (serial.equals(getSerial())) {
					messageArea.append(line.substring(8) + "\n");
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		UserClient client = new UserClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}
}
