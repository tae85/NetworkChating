package chat10;

import java.util.Scanner;
import java.net.Socket;

public class MultiClient {
	
	public static void main(String[] args) {
		
		System.out.println("이름을 입력하세요:");
		Scanner scanner = new Scanner(System.in);
		String s_name = scanner.nextLine();
		
		//PrintWriter out = null;
		//BufferedReader in = null;
		
		try {
			String ServerIP = "localhost";
			if(args.length > 0) {
				ServerIP = args[0];
			}
			
			//소켓 객체 생성
			Socket socket = new Socket(ServerIP, 9999);
			System.out.println("서버와 연결되었습니다...");
			
			//서버에서 보내는 메시지를 사용자의 콘솔에 출력하는 쓰레드
			Thread receiver = new Receiver(socket);
			receiver.start();
			
			//클라이언트가 입력한 메세지를 서버로 전송해주는 쓰레드
			Thread sender = new Sender(socket, s_name);
			sender.start();
		} 
		catch (Exception e) {
			System.out.println("예외발생[MultiClient]" + e);
		}
	}
}
//cd \02Workspaces\01Java\NetworkChating\bin
//java multichat.MultiClient




































