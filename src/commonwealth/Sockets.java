package commonwealth;

import java.io.IOException;
import java.net.Socket;

public class Sockets {
	
	private static Sockets instance;
	private Socket[] sockets;
	private static int SIZE;
	
    private Sockets(){}
    
    //static block initialization for exception handling
    static{
        try{
            instance = new Sockets();
        }catch(Exception e){
            throw new RuntimeException("Exception occured in creating singleton instance");
        }
    }
    
    public static Sockets getInstance(){
        return instance;
    }
	
	
	
	public void reset(int size) {
		this.sockets = new Socket[size];
		SIZE = sockets.length;
	}
		
	public int size() {
		return SIZE;
	}
	
	public synchronized void write(int i, byte[] data) {
		try {
			this.sockets[i].getOutputStream().write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized int read(int i, byte[] buffer) {
		try {
			return this.sockets[i].getInputStream().read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
