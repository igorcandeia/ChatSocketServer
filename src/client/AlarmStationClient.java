package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class AlarmStationClient extends Thread{

	BufferedReader in;
	PrintWriter out;
	
	String serverAddress;
	int serverPort;
	String serial;

	public AlarmStationClient(String serverAddress, int serverPort, String serial) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.serial = serial;
		
		Socket socket;
		try {
			socket = new Socket(serverAddress, getServerPort());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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

		try {
//			String serverAddress = getServerAddress();
//			Socket socket = new Socket(serverAddress, getServerPort());
//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			out = new PrintWriter(socket.getOutputStream(), true);

			while (true) {
				String line = in.readLine();
//				System.out.println(line);
				if (line.startsWith("CLIENT")) {
					String id = getClientId();
					out.println(id);
					String serial = getSerial();
					out.println(serial);
//					System.out.println("Alarm Station "+ serial + " connected");
				} else if (line.startsWith("MESSAGE")) {
					String serial = line.split(" ")[4];
					if (serial.equals(getSerial())) {
						if(!line.contains("RECEIVED!")) {
							System.out.println(line.substring(8)+ "\n");
							out.println("RECEIVED!");
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Alarm Station "+ serial + " error: "+e.getMessage());
		}
		
	}

	public static void main(String[] args) throws Exception {
//		String ip = args[0];
//		int initId = Integer.parseInt(args[1]);
//		int endId = initId+500;
		for(int i = 0; i<2500; i++) {
//			AlarmStationClient client = new AlarmStationClient("ec2-52-67-107-195.sa-east-1.compute.amazonaws.com", 9001, Integer.toString(i));
			AlarmStationClient client = new AlarmStationClient("localhost", 9001, Integer.toString(i));
			client.start();
		}
	}
}
