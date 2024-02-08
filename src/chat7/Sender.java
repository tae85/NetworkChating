package chat7;

import java.io.PrintWriter;
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
			out.println(name);
			
			while(out != null) {
				try {
					String s2 = s.nextLine();
					if(s2.equalsIgnoreCase("Q")) {
						break;
					}
					else {
//						out.println(s2);
//						out.println(URLEncoder.en)
					}
				} 
				catch (Exception e) {
					System.out.println("예외>Sender>run1:" + e);
				}
			}
			out.close();
			socket.close();
		} 
		catch (Exception e) {
			System.out.println("예외>Sender>run2:" + e);
		}
	}
}




























