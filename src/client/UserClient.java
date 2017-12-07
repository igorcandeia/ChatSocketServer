package client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserClient {

	BufferedReader in;
	PrintWriter out;
	String serverAddress;
	int serverPort;
	String serial;
	String id;

	public UserClient(String serverAddress, int serverPort, String serial, String id) throws UnknownHostException, IOException {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.serial = serial;
		this.id = id;
	    
	    Socket socket = new Socket(getServerAddress(), getServerPort());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
		String line = in.readLine();
		if (line.startsWith("CLIENT")) {
			out.println(id);
			out.println(serial);
			System.out.println("User "+id+ " connected to "+serial);
		}
		
//	    startReceivingData();
//	    startSendingData();
	}

	private void startSendingData() {
		new Thread(new Runnable() {
			public void run() {
				
				while(true) {
					try {
						Thread.sleep(2000);
					} catch(Exception e){
						System.err.println(e.getMessage());
					}
					if(out!=null) {
						out.println("MESSAGE_FROM_"+getClientId());
					}
				}
			}
		}).start();
	}
	
	private void sendMessage() {
		out.println("MESSAGE_FROM_"+getClientId());
		System.out.println("MESSAGE_FROM_"+getClientId());
	}

	private void connect() throws UnknownHostException, IOException {
		// Make connection and initialize streams
		Socket socket = new Socket(getServerAddress(), getServerPort());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
		String line = in.readLine();
		if (line.startsWith("CLIENT")) {
			String id = getClientId();
			out.println(id);
			String serial = getSerial();
			out.println(serial);
		}
	}

	private String getServerAddress() {
		return serverAddress;
	}
	
	private int getServerPort() {
		return serverPort;
	}
	
	private String getClientId() {
		return id;
	}
	
	private String getSerial() {
		return serial;
	}

	private void startReceivingData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					connect();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				while (true) {
					try {
						if (in!= null) {
							String line = in.readLine();
							if (line.startsWith("MESSAGE")) {
								String serial = line.split(" ")[4];
								String id = line.split(" ")[1];
								if (serial.equals(getSerial()) && id.equals("ALARM_STATION")) {
									System.out.println(line.substring(8) + "\n");
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static void main(String[] args) throws Exception {
//		String ip = args[0];
//		int initId = Integer.parseInt(args[1]);
//		int endId = initId+500;
		List<UserClient> clients = new ArrayList<>();
//		int cont =0;
		for(int i = 0; i<2500; i++) {
			for(int u = 0; u<5; u++) {
				UserClient client= new UserClient("localhost",9001, Integer.toString(i), Integer.toString(i) + Integer.toString(u));
//				System.out.println(cont++);
//				UserClient client= new UserClient("ec2-52-67-107-195.sa-east-1.compute.amazonaws.com",9001, Integer.toString(i), Integer.toString(i) + Integer.toString(u));
				clients.add(client);
			}
		}
		
		for (int i = 0; i<5; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(true) {
						UserClient randomClient = clients.get(new Random().nextInt(clients.size()));
						randomClient.sendMessage();
					}
					
				}
			}).start();
		}
		
	}
}
