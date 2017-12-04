package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class ChatServer {

	private static final int PORT = 9001;

	private static HashSet<String> alarmStations = new HashSet<String>();

	private static HashMap<String, String> clients = new HashMap<String, String>();
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running.");
		ServerSocket listener = new ServerSocket(PORT);
		try {
			while (true) {
				new Handler(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}

	private static class Handler extends Thread {
		private static final String ALARM_STATION = "ALARM_STATION";
		private String id;
		private String serial;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		public Handler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {

				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				while (true) {
					out.println("CLIENT");
					id = in.readLine();
					serial = in.readLine();
					if (serial == null) {
						return;
					}
					if (id.equals(ALARM_STATION)) {
						synchronized (alarmStations) {
							if (!alarmStations.contains(serial)) {
								alarmStations.add(serial);
								break;
							}
						}

					} else {
						synchronized (clients) {
							if (!clients.containsKey(id)) {
								clients.put(id, serial);
								break;
							}
						}
					}
				}

				out.println("NAMEACCEPTED");
				writers.add(out);

				while (true) {
					String input = in.readLine();
					System.out.println(input);
					System.out.println("id = " + id + "; serial = " + serial);
					if (input == null) {
						return;
					}
					for (PrintWriter writer : writers) {
						writer.println("MESSAGE " + id + " sent to " + serial + " : " + input);
					}
				}
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				if (serial != null && id == ALARM_STATION) {
					alarmStations.remove(serial);
				}

				if (id != null && id != ALARM_STATION) {
					clients.remove(id);
				}

				if (out != null) {
					writers.remove(out);
				}
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
