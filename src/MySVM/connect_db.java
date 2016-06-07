package MySVM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import love.cq.domain.Value;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class connect_db {
	
	Connection conn;
	private String connURL = "";
	private String username = "";
	private String password = "";
	Statement sql;
	ResultSet res;
		
	public connect_db(){         
		String[] array = {"DB-IP:port","DB-name","id","passwd"};
		this.connURL = 
			"jdbc:mysql://"+array[0]+"/"+array[1]+"?useUnicode=true&characterEncoding=UTF-8";
		this.username = array[2];
		this.password = array[3];
	}
	
	public Connection getConnections() {
		String driver = "com.mysql.jdbc.Driver";
		try {
			Class.forName(driver);//.newInstance();
			conn = DriverManager.getConnection(
				this.connURL, this.username, this.password);
		} catch (Exception e) {
			e.printStackTrace();
		} 	            
		return conn;
	}
	
	public void closeConnections(){
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean  Insert_product(Vector<String> prod) throws SQLException{
		PreparedStatement pstmt = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		try {
			if ((conn==null)||conn.isClosed()){
	             // Connect to the database.
	            //conn = DriverManager.getConnection( this.dbString, this.props );
				getConnections();      
	           }
			String product_name = prod.get(0);
			String product_no = prod.get(1);
			String brand = prod.get(2);
			String market_time = prod.get(3);
			String weight = prod.get(4);
			String production_address = prod.get(5);
			String best_distance = prod.get(6);
			String video = prod.get(7);
			String type = prod.get(8);
			String size = prod.get(9);
			String is_3D = prod.get(10);
			String from_web = prod.get(11);
			String datainput_time = sdf.format(new java.util.Date().getTime()); //prod.get(12);//sdf.format(new java.util.Date().getTime());
			String col1 = prod.get(12);
			String col2 = prod.get(13);
			String col3 = prod.get(14);
			
			String sql = "INSERT INTO product VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setString(1, product_name);
			pstmt.setString(2, product_no);
			pstmt.setString(3,brand);
			pstmt.setString(4, market_time);
			pstmt.setString(5, weight);
			pstmt.setString(6,production_address);
			pstmt.setString(7,best_distance);
			pstmt.setString(8,video);
			pstmt.setString(9,type);
			pstmt.setString(10,size);
			pstmt.setString(11,is_3D);
			pstmt.setString(12, from_web);
			pstmt.setString(13, datainput_time);
			pstmt.setString(14,col1);
			pstmt.setString(15,col2);
			pstmt.setString(16,col3);
			
			pstmt.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.print("Insert_product_error");
			return false;
		} finally {
            // Close the Statement and the connection objects.
            if (pstmt!=null) pstmt.close();
            if (conn!=null) conn.close();         
      } 
		return true;
	}
	
	public boolean insert_comment(Vector<String> prod) throws SQLException{
		PreparedStatement pstmt = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if ((conn==null)||conn.isClosed()){
	             // Connect to the database.
	            //conn = DriverManager.getConnection( this.dbString, this.props );
				getConnections();      
	           }
			String product_no = prod.get(0);
			String commentator = prod.get(1);
			String nickname = prod.get(2);
			String location = prod.get(3);
			String commentator_grade = prod.get(4);
			String comment_time = prod.get(5);
			String purchase_time = prod.get(6);
			String comment = prod.get(7);
			String Product_Version = prod.get(8);
			String tag1 = prod.get(9);
			String tag2 = prod.get(10);
			String tag3 = prod.get(11);
			String tag4 = prod.get(12);
			String tag5 = prod.get(13);
			String tag6 = prod.get(14);
			String tag7 = prod.get(15);
			String tag8 = prod.get(16);
			String tag9 = prod.get(17);
			String tag10 = prod.get(18);
			String customer_satisfy= prod.get(19);
			String from_web = prod.get(20);
			String datainput_time = sdf.format(new java.util.Date().getTime()); //prod.get(12);//sdf.format(new java.util.Date().getTime());

			
			String sql = "INSERT INTO pro_comment VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			pstmt.setString(1, product_no);
			pstmt.setInt(2, 0);
			pstmt.setString(3,commentator);
			pstmt.setString(4, nickname);
			pstmt.setString(5, location);
			pstmt.setString(6,commentator_grade);
			pstmt.setString(7,comment_time);
			pstmt.setString(8,purchase_time);
			pstmt.setString(9,comment);
			pstmt.setString(10,Product_Version);
			pstmt.setString(11,tag1);
			pstmt.setString(12,tag2);
			pstmt.setString(13,tag3);
			pstmt.setString(14,tag4);
			pstmt.setString(15,tag5);
			pstmt.setString(16,tag6);
			pstmt.setString(17,tag7);
			pstmt.setString(18,tag8);
			pstmt.setString(19,tag9);
			pstmt.setString(20,tag10);
			pstmt.setString(21,customer_satisfy);
			pstmt.setString(22,from_web);
			pstmt.setString(23,datainput_time);
			
			pstmt.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.print("Insert_pro_comment_error");
			return false;
		}finally {
            // Close the Statement and the connection objects.
            if (pstmt!=null) pstmt.close();
            if (conn!=null) conn.close();         
      } 
		return true;
	}
	
//	public boolean check_table(String table_name,Vector<String> v_col_name) throws SQLException{
//		int col_count = v_col_name.size();
//		String w = "";
//		boolean b = false;
//		for(int i = 0;i < col_count;i++){
//			w = w + v_col_name.get(i)+ " and ";
//		}
//		w = w.substring(0,w.length()-4);
//		String v_sql = "select 1 from "+ table_name + " where " + w;
//		
//		try {
//			if ((conn==null)||conn.isClosed()){
//				sql = (Statement) getConnections().createStatement();      
//	           }
//			res	= sql.executeQuery("select 1 from "+ table_name + " where " + w);
//			while(res.next()){
//				int product_no = res.getInt(1);
//				if (product_no==1) {
//					b = true;
//					break;
//				}
//			}
//			return b;
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			return false;
//		}finally {
//            // Close the Statement and the connection objects.
//            conn.close();  
//		}
//	}
	
	public boolean check_table(String table_name,Vector<String> v_col_name) throws SQLException{
		int col_count = v_col_name.size();
		ResultSet r;
		PreparedStatement npjs = null;
		String w = "";
		boolean b = false;
		for(int i = 0;i < col_count;i++){
			w = w + v_col_name.get(i)+ " and ";
		}
		w = w.substring(0,w.length()-4);
		String v_sql = "select 1 as product_no from "+ table_name + " where " + w;
		
		try {
			npjs = (PreparedStatement) conn.prepareStatement(v_sql);    		
			r = npjs.executeQuery();
			//r = sql.executeQuery("select 1 as product_no from "+ table_name + " where " + w);
			while(r.next()){
				int product_no = r.getInt(1);
				if (product_no==1) {
					b = true;
					break;
				}
			}
			return b;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public void Insert_comment_feature(Vector<String> prod) throws SQLException{
		PreparedStatement pstmt = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

		Vector<String>w = new Vector<String>();
		boolean m;
		try {
			if ((conn==null)||conn.isClosed()){
				getConnections();      
	           }
			w.removeAllElements();
			w.add(0,"comment_id = "+prod.get(0));
			w.add(1,"feature = "+"'"+prod.get(1)+"'");
			w.add(2,"adj = "+"'"+prod.get(2)+"'");
			w.add(3,"opinion = "+"'"+prod.get(3)+"'");
			w.add(4,"original_feature = "+"'"+prod.get(4)+"'");
			m = this.check_table("comment_feature", w);
			if(!m){
				String datainput_time = sdf.format(new java.util.Date().getTime());
				
				String sql = "INSERT INTO comment_feature VALUES(?,?,?,?,?,?)";
				pstmt = (PreparedStatement) conn.prepareStatement(sql);
				pstmt.setInt(1,Integer.valueOf(prod.get(0)));
				pstmt.setString(2,prod.get(1));
				pstmt.setString(3,prod.get(2));
				pstmt.setString(4,prod.get(3));
				pstmt.setString(5,datainput_time);
				pstmt.setString(6,prod.get(4));

				pstmt.executeUpdate();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.print("Insert_comment_feature_error");
		} finally {
            // Close the Statement and the connection objects.
            if (pstmt!=null) pstmt.close();
            if (conn!=null) conn.close();         
      } 
	}
	
	public void Insert_comment_sentiment(Vector<String> prod) throws SQLException{
		PreparedStatement pstmt = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		Vector<String>w = new Vector<String>();
		boolean m;
		try {
			if ((conn==null)||conn.isClosed()){
				getConnections();      
	           }
			w.removeAllElements();
			w.add(0,"comment_id = "+prod.get(0));
			w.add(1,"sentiment = "+prod.get(1));
			m = this.check_table("comment_sentiment", w);
			if(!m){
				String datainput_time = sdf.format(new java.util.Date().getTime());
				
				String sql = "INSERT INTO comment_sentiment VALUES(?,?,?)";
				pstmt = (PreparedStatement) conn.prepareStatement(sql);
				pstmt.setInt(1,Integer.valueOf(prod.get(0)));
				pstmt.setString(2,prod.get(1));
				pstmt.setString(3,datainput_time);

				pstmt.executeUpdate();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.print("Insert_comment_sentiment_error");
		} finally {
            // Close the Statement and the connection objects.
            if (pstmt!=null) pstmt.close();
            if (conn!=null) conn.close();         
      } 
	}
		
	public void update_table_post_class_id_tianfu(Vector<String> prod) throws SQLException{
		PreparedStatement pstmt = null;

		try {
			if ((conn==null)||conn.isClosed()){
				getConnections();      
	           }
				String sql = "UPDATE blog SET sentiment_output_twoclass = ? WHERE post_id = ?";
				pstmt = (PreparedStatement) conn.prepareStatement(sql);
				pstmt.setString(1,prod.get(1));
				pstmt.setString(2,prod.get(0));

				pstmt.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.print("update_table_post_class_id_tianfu");
		} finally {
            // Close the Statement and the connection objects.
            if (pstmt!=null) pstmt.close();
            //if (conn!=null) conn.close();         
      } 
	}

	
//	   public static void  main(String[] args) throws Exception   
//	   {
//		   connect_db c = new connect_db();
//		   Vector<String>w = new Vector<String>();
//		   w.add(0,"product_no = 1468154");
//		   w.add(1,"from_web = '����'");
//		   boolean m = c.check_table("pro_comment",w);
////		   if (!m) {
////			   Vector<String>v = new Vector<String>();
////			   v.add(0, "����55N1");
////			   v.add(1, "1468154");
////			   v.add(2, "����");
////			   v.add(3, "2015-04-21 19:49:29");
////			   v.add(4, "19");
////			   v.add(5, "�й���½");
////			   v.add(6, "3.0-3.5��");
////			   v.add(7, "ȫ����(1920x1080)");
////			   v.add(8, "�����ܵ���");
////			   v.add(9, "55");
////			   v.add(10, "��֧��");
////			   v.add(11, "����");
////			   v.add(12, "");
////			   v.add(13, "");
////			   v.add(14, "");
////			   c.Insert_product(v);	
////			   v.removeAllElements();
////		}
//		   System.out.println(m);
//	   }	
}