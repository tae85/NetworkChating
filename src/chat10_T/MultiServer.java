package chat10_T;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class MultiServer {
	//멤버변수
	static ServerSocket serverSocket = null;
	static Socket socket = null;
	static SQLinsert dbInsert = null; //static 삭제??
	
	//클라이언트 정보를 저장하기 위한 Map 컬렉션 생성
	Map<String, PrintWriter> clientMap;
	HashSet<String> blackList = new HashSet<String>();
	HashSet<String> pWords = new HashSet<String>();
	HashMap<String, String> tofixMap;
	HashMap<String, String> blockMap;
	
	String fixtoMsg = "";
	String fixtoFlag;
	int clientCnt=3;
	
	//생성자
	public MultiServer() {
		/* 클라이언트의 이름과 접속시 생성한 출력스트림을 저장할 HashMap 인스턴스 생성 */
		clientMap = new HashMap<String, PrintWriter>();
		
		/* HashMap 동기화 설정. 쓰레드가 사용자 정보에 동시접근하는 것을 차단한다. */
		Collections.synchronizedMap(clientMap);
		
		blackList.add("kkk");blackList.add("ttt");blackList.add("aaa");
		
		pWords.add("18");pWords.add("28");pWords.add("138");
	}
	
	//채팅 서버 초기화
	public void init() {
		try {
			//서버소켓 생성
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");
			
			dbInsert = new SQLinsert();	//test
			System.out.println("DB연결");
			
			/* 1명의 클라이언트가 접속할 때마다 허용해주고 동시에 쓰레드를 생성한다. */
			while(true) {
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress() + "(클라이언트)의 " + 
						socket.getPort() + "포트를 통해 " + 
						socket.getLocalAddress() + "(서버)의 " + 
						socket.getLocalPort() + "포트로 연결되었습니다.");
				
				//클라이언트 1명당 하나의 쓰레드가 생성되어 메세지 전송 및 수신을 담당한다.
				if(clientMap.size() >= clientCnt) {
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					out.println(URLEncoder.encode("정원(최대"+clientCnt+"명) 초과", "UTF-8"));
					break;
				}
				else {
					Thread mst = new MultiServerT(socket);	//위치 else 안에 있어도??
					mst.start();
				}
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				serverSocket.close();
//				socket.close();	//필요??
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/* 인스턴스 생성 후 초기화 메서드를 호출한다. */
	public static void main(String[] args) {
		MultiServer ms = new MultiServer();
		ms.init();
	}
	
	/* 접속된 모든 클라이언트 측으로 서버의 메세지를 Echo 해주는 역할을 수행한다.(이전 단계에서는 보낸 사람에게만
	Echo 되었다.)*/
	public void sendAllMsg(String name, String msg) {
		
		/* Map에 저장된 클라이언트의 key를 얻어온다. key에는 대화명이 저장되어 있다. */
		Iterator<String> it = clientMap.keySet().iterator();
		
		
		//앞에서 얻어온 대화명(key값)의 개수만큼 반복한다.
		while(it.hasNext()) {
			try {
				//각 클라이언트 PrintWriter 인스턴스를 추출한다.
				PrintWriter it_out = (PrintWriter)clientMap.get(it.next());
				
				//금칙어 검사
				msg = checkMsg(msg);
				
				/*
				클라이언트에게 메세지를 전달할 때 매개변수로 name이 있는 경우와 없는 경우를 구분해서 전달한다.
				 */
				if(name.equals("")) {
					
					/* 입장 혹은 퇴장에서 사용되는 부분 */
					it_out.println(URLEncoder.encode(msg, "UTF-8"));
				}
				else {
					/* 메세지를 보낼 때 사용되는 부분 */
					it_out.println("[" + name + "]" + msg);
					
				}
			} 
			catch (Exception e) {
				System.out.println("예외:" + e);
			}
		}
	}
	
	//귓속말 전송 : 발신자 대화명, 메세지, 수신자 대화명
	public void sendAllMsg(String name, String msg, String receiveName) {
		Iterator<String> it = clientMap.keySet().iterator();
		
		msg = checkMsg(msg);
		
		while(it.hasNext()) {
			try {
				//HashMap에는 Key로 대화명, Value로 PrintWriter 인스턴스가 저장되어 있다. 
				String clientName = it.next();
				PrintWriter it_out = (PrintWriter)clientMap.get(clientName);
				
				/* 해당 루프에서의 클라이언트 이름과 귓속말을 받을 사람의 대화명이 일치하는지 확인한다. */
				if(clientName.equals(receiveName)) {
					//일치하면 한 사람에게만 귓속말을 보낸다.
					it_out.println("[귓속말]" + name + " : " + msg);
				}
			} 
			catch (Exception e) {
				System.out.println("예외:" + e);
			}
		}
	}
	
	//아이디 체크
	public boolean checkId (String name) {
		Iterator<String> it = clientMap.keySet().iterator();
		try {
			Sender sender = new Sender(socket, name);
			
			//중복 체크
			while(it.hasNext()) {
				String key = it.next();
				if(name.equals(key)) {
					System.out.println("중복아이디-서버");
					sender.out.println("중복아이디입니다. 나가주세요.");
					sender.out.close();
					sender.socket.close();
					return false;
				}
				
			}
			
			//블랙리스트 체크
			for(String checkList : blackList) {
				if(name.equals(checkList)) {
					System.out.println("블랙리스트-서버");
					sender.out.println("블랙리스트이시네요^^*");
					sender.out.close();
					sender.socket.close();
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println("중복아이디 예외처리:" + e);
		}
		return true;
	}
	
	//접속자 제한
	public boolean checkCount (String name) {
		Sender sender = new Sender(socket, name);
		try {
			if(clientMap.size() >= 2) {
				System.out.println("접속자수제한-서버");
				sender.out.println("허용 접속자 초과입니다.");
				sender.out.close();
				sender.socket.close();
				return false;
			}
		}
		catch(Exception e) {
			System.out.println("접속자수 예외처리:" + e);
		}
		return true;
	}
	
	//금칙어 확인
	public String checkMsg (String msg) {
		for(String str : pWords) {
			String ast = "";
			for(int i = 0; i < str.length(); i++) {
				ast += "*";
			}
			msg = msg.replace(str, ast);
		}
		return msg;
	}
	
	//차단 목록 추가
	public void addBlock(String name, String blockName) {
		if(blockMap == null) {
			blockMap.put(name, blockName + " ");
		}
		else {
			if(blockMap.containsKey(name)) {
				blockMap.put(name, blockMap.get(name)+blockName+"|");
			}
			else {
				blockMap.put(name, blockName+"|");
			}
		}
	}
	
	//차단 목록 해제
	public void minBlock(String name, String blockName) {
		if(blockMap!=null && blockMap.containsKey(name)) {
			String newblockUser = blockMap.get(name).replace(blockName+"|", "");
			blockMap.put(name, newblockUser);
		}
	}
	
	///////////////////////////////////////////////////////////////////
	
	class MultiServerT extends Thread {
		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;
		
		public MultiServerT(Socket socket) {
			this.socket = socket;
			try {
				out = new PrintWriter(this.socket.getOutputStream(), true);
				in = new BufferedReader(
						new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
			} 
			catch (Exception e) {
				System.out.println("예외:" + e);
			}
		}
		
		@Override
		public void run() {
			String name = "";
			String s = "";
			
			boolean checkIdFlag = true;
			boolean checkCntFlag = true;
			boolean checkWhisperFlag = false;
			
			try {
				//첫번재 메세지는 대화명이므로 접속을 알린다
				name = in.readLine();
				name = URLDecoder.decode(name, "UTF-8");
				
				checkIdFlag = checkId(name);
				checkCntFlag = checkCount(name);
				
				if(checkIdFlag && checkCntFlag) {
					sendAllMsg("", name + "님이 입장하셨습니다.");
					clientMap.put(name, out);
					System.out.println(name + " 접속");
					System.out.println("현재 접속자 수는" + clientMap.size() + "명 입니다.");
				}
				else {
					in.close();
					out.close();
					socket.close();
					System.out.println("클로즈 돌입");
				}
				
				//두번째 메세지부터는 "대화내용"이다.
				while(in != null) {
					s = in.readLine();
					s = URLDecoder.decode(s, "UTF-8");
					if(s == null) {
						break;
					}
					//서버의 콘솔에는 메세지를 그대로 출력한다.
					System.out.println(name + " >> " + s);
					
					if(checkWhisperFlag == true) {
						s = fixtoMsg + s;
						System.out.println("fixtoMsg:" + s);
					}
					
					/*
					귓속말형식 => /to 수신자명 대화내용
					 */
					if(s.charAt(0)=='/') {
						
						
						//슬러쉬로 시작하면 명령어로 판다
						/* split()으로 문자열을 분리한다. 여기서 사용하는 구분자는 스페이스이다. */
						String[] strArr = s.split(" ");
						
						/*
						문자열을 스페이스로 분리하면 0번 인덱스는 명령어, 1번 인덱스는 수신자 대화명이 되고
						2번 인덱스부터 끝까지는 대화내용이 되므로 아래와 같이 문자열 처리를 해야 한다.
						 */
						String msgContent = "";
						for(int i = 2; i < strArr.length; i++) {
							msgContent += strArr[i]+" ";
						}
						
						
						/* 명령어가 /to가 맞는지 확인한다. 명령어에 대한 오타가 있을 수도 있고, 다른 명령어
						일 수도 있기 때문이다.
						 */
						if(checkWhisperFlag == true) {
							if(strArr[2].equals("/unfixto")) {
								//귓속말을 보낸다.
								fixtoMsg = "";
								checkWhisperFlag = false;
							}
						}
						if(strArr[0].equals("/to")) {
							//귓속말을 보낸다.
							/* 기존의 메서드를 오버로딩해서 추가 정의한다. 매개변수는 발신대화명, 메세지,
							수신대화명 형태로 작성한다. */
							sendAllMsg(name, msgContent, strArr[1]);
							dbInsert.dbExecute(name, msgContent);
						}
						else if(strArr[0].equals("/fixto")) {
							//귓속말을 보낸다.
							sendAllMsg(name, msgContent, strArr[1]);
							dbInsert.dbExecute(name, msgContent);
							fixtoMsg = "/to " + strArr[1] + " ";
							checkWhisperFlag = true;
						}
						else if(strArr[0].equals("/block")) {
							addBlock(name, strArr[1]);
							out.println(URLEncoder.encode(strArr[1]+"님이 차단되었습니다.", "UTF-8"));
						}
						else if(strArr[0].equals("/unblock")) {
							minBlock(name, strArr[1]);
							out.println(URLEncoder.encode(strArr[1]+"님이 차단 해제되었습니다.", "UTF-8"));
						}
						else if(strArr[0].equals("/list")) {
							Iterator<String> it = clientMap.keySet().iterator();
							String it_str = "";
							while(it.hasNext()) {
								it_str += "\"" + it.next() + "\"님 ";
							}
							out.println("접속자: " + it_str);
//							out.println(URLEncoder.encode(strArr[1]+"님이 차단 해제되었습니다.", "UTF-8"));
						}
					}
					else {
						//슬러쉬가 없다면 일반 대화내용
						sendAllMsg(name, s);
						dbInsert.dbExecute(name, s);
					}
				}
			} 
			catch (Exception e) {
				System.out.println("예외:" + e);
			}
			finally {
				if(checkIdFlag && checkCntFlag) {
					clientMap.remove(name);
					sendAllMsg("", name + "님이 퇴장하셨습니다.");
					System.out.println(name + " [" + 
							Thread.currentThread().getName() + "] 퇴장");
					System.out.println("현재 접속자 수는 " + clientMap.size() + "명 입니다.");
				}
				try {
					in.close();
					out.close();
					socket.close();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}




























