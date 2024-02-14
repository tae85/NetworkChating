package chat9.copy;

import java.sql.SQLException;

public class SQLinsert extends MyConnection{
	
	public SQLinsert() {
		super("study", "1234");
	}
	
	//멤버변수
	String query;
		
	int result;		
	
	
	@Override
	public void dbExecute(String name, String msg) {
		try {
			
			query = "insert into chat_talking "
						+ " values (seq_msg_num.nextval, ?, ?, sysdate)";
			
			//동적쿼리 실행을 위한 preparedStatement 인스턴스 생성
			psmt = con.prepareStatement(query);
			
			/*
			동적쿼리문의 ?부분(인파라미터)을 사용자의 입력값으로 채워준다. DB에서는 인덱스가 1부터 시작이므로
			?의 개수만큼 순서대로 값을 설정하면 된다.
			 */
			psmt.setString(1, name);
			psmt.setString(2, msg);
			
			//쿼리문 실행 및 결과 반환
			result = psmt.executeUpdate();
			
			//insert 쿼리문이므로 성공시 1, 실패시 0이 반환된다.
//			System.out.println("[psmt]" + result + "행 입력됨");
		} 
		catch (SQLException e) {
			System.out.println("쿼리실행 중 예외발생");
			e.printStackTrace();
		}
	}
}


























