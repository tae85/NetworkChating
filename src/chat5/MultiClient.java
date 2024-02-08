package chat5;

import java.util.Scanner;
import java.net.Socket;

public class MultiClient {
	
	public static void main(String[] args) {
		
		System.out.println("이름을 입력하세요:");
		Scanner scanner = new Scanner(System.in);
		String s_name = scanner.nextLine();
		
		/*
		메세지 송수신을 위한 클래스를 별도로 만들었으므로 해당 멤버변수는 필요없음
		 */
		//PrintWriter out = null;
		//BufferedReader in = null;
		
		try {
			//서버에 접속 요청
			String ServerIP = "localhost";
			if(args.length > 0) {
				ServerIP = args[0];
			}
			Socket socket = new Socket(ServerIP, 9999);
			System.out.println("서버와 연결되었습니다...");
			
			/*
			서버가 Echo해준 메세지를 지속적으로 받기 위한 리시버 쓰레드 인스턴스 생성 및 시작
			 */
			Thread receiver = new Receiver(socket);
			receiver.start();
			
			/*
			서버로 메세지를 전송할 센더 쓰레드 인스턴스 생성 및 시작
			 */
			Thread sender = new Sender(socket, s_name);
			sender.start();
		} 
		catch (Exception e) {
			System.out.println("예외발생[MultiClient]" + e);
		}
	}
}

























