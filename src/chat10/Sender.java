package chat10;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Scanner;

public class Sender extends Thread {
	
	Socket socket;
	PrintWriter out = null;
	String name;
	
	
	public Sender(Socket socket, String name) {
		this.socket = socket;
		try {
			out = new PrintWriter(this.socket.getOutputStream(), true);
			this.name = name;
		} 
		catch (Exception e) {
			System.out.println("예외>Sender>생성자:" + e);
		}
	}
	
	@Override
	public void run() {
		Scanner s = new Scanner(System.in);
		
		try {
			out.println(URLEncoder.encode(name, "UTF-8"));
			
			while(out != null) {
				try {
					String msg = s.nextLine();
					if(msg.equalsIgnoreCase("Q")) {
						break;
					}
					else {
						out.println(URLEncoder.encode(msg, "UTF-8"));
					}
				} 
				catch (Exception e) {
					System.out.println("예외>Sender>run1:" + e);
				}
			}
			out.close();
			socket.close();
		} 
		catch (UnsupportedEncodingException e) {
			System.out.println("인코딩 예외처리:" + e);
		}
		catch (Exception e) {
			System.out.println("예외>Sender>run2:" + e);
		}
	}
}




























