package chat8;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;

public class Receiver extends Thread{
	
	Socket socket;
	BufferedReader in = null;
	
	public Receiver(Socket socket) {
		this.socket = socket;
		
		try {
			in = new BufferedReader(
					new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
		} 
		catch (Exception e) {
			System.out.println("예외>Receiver>생성자:" + e);
		}
	}
	
	@Override
	public void run() {
		while(in != null) {
			try {
				System.out.println("Thread Receive : " + 
								URLDecoder.decode(in.readLine(), "UTF-8"));
			} 
			catch(SocketException ne) {
				System.out.println("SocketException 발생됨. 루프탈출");
				break;
			}
			catch (Exception e) {
				System.out.println("예외>Receiver>run1:" + e);
			}
		}
		
		try {
			in.close();
		}
		catch (Exception e) {
			System.out.println("예외>Receiver>run2:" + e);
		}
	}
}





















