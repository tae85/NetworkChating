package chat10_T;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;

public class Receiver extends Thread {
	
	Socket socket;
	BufferedReader in = null;
	
	//socket을 매개변수로 받는 생성자
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
			catch (NullPointerException e) {
				System.out.println("NullPointerException 발생. 리시버종료");
				break;
			}
			catch (Exception e) {
				System.out.println("예외>Receiver>run1:" + e);
				break;
			}
		}
		
		try {
			in.close();
			System.exit(0);	//test
		}
		catch (Exception e) {
			System.out.println("예외>Receiver>run2:" + e);
		}
	}
}





















