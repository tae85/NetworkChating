package chat9;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Scanner;

public class Sender extends Thread {
	
	Socket socket;
	PrintWriter out = null;
	String name;
//	ObjectInputStream ois = null;	//test
//	ObjectOutputStream oos;	//test
	
	public Sender(Socket socket, String name) {
		this.socket = socket;
		try {
			out = new PrintWriter(this.socket.getOutputStream(), true);
//			oos = new ObjectOutputStream(this.socket.getOutputStream());
//			ois = new ObjectInputStream(this.socket.getInputStream());
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
//			System.out.println("센더1111");
			out.println(URLEncoder.encode(name, "UTF-8"));
			
			
			
//			if(ois != null) {
//				Map<String, PrintWriter> returnMap = (Map<String, PrintWriter>) ois.readObject();
//				for(String key : returnMap.keySet()) {
//					System.out.println("키값은???"+key);
//				}
//			}
//				ArrayList<BoardVO> retrunList = (ArrayList<BoardVO>) returnMap.get("list"); //test 추후 삭제
			
			while(out != null) {
				try {
//					s.next();
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
//			ois.close();	//test
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




























