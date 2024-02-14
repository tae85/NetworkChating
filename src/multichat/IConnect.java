package multichat;

//JDBC프로그램의 최상위 부모로 사용할 인터페이스 생성
public interface IConnect {
	/*
	interface에 선언하는 모든 변수는 public static final이 추가되어 어디서든 접근할 수 있는
	정적상수가 된다.	
	 */
	//오라클 드라이버 지정
	String OACLE_DRIVER = "oracle.jdbc.OracleDriver";
	
	//오라클 연결을 위한 커넥션URL 지정
	String OACLE_URL = "jdbc:oracle:thin:@localhost:1521:xe";
	
	/*
	interface에 선언하는 모든 메서드는 public abstract가 추가되어 추상메서드가 된다.
	 */
	//CRUD작업 실행을 위한 메서드
	void dbExecute(String name, String msg);
	
	//자원 반납을 위한 메서드
	void dbClose();
	
	//사용자로부터 입력을 받기 위한 메서드
	String inputValue(String title);
}
