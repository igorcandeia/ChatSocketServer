package commonwealth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ThreadPooling {

	private int SIZE = 0;
	private List<Thread> threadPooling;

	public ThreadPooling(int SIZE, Sockets sockets) {
		this.SIZE = SIZE;
		this.threadPooling = Collections.synchronizedList(new ArrayList<Thread>());
		for(int i = 0; i < SIZE; i++) {
			Thread thread = new CounterThread(String.valueOf(i), sockets);
			this.threadPooling.add(i, thread);
		}
	}
	
	public int size() {
		return SIZE;
	}

	class CounterThread extends Thread {
		private int counter = 0;
		Sockets sockets;
		Random random = new Random();
		
		public CounterThread(String name, Sockets sockets) {
			super(name);
			this.sockets = sockets;
		}
		public void run() {
			int index = random.nextInt(sockets.size());
			String data = "socket:" + index + "::"+ counter;
			sockets.write(index, data.getBytes());
			System.out.println("Socket " + index + " is sending " + counter + " ...");
			counter++;
		}
	}

}
