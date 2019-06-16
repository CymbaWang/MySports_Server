package cn.edu.zjut.dao;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.sql.*;
import javax.naming.*;
import cn.edu.zjut.pojo.User;

public class UserDao{
	public UserDao( ){ }
	// 根据账号密码查看用户信息
	public User searchUser(User user){
		String GET_ONE_SQL =
				"SELECT * FROM usertable WHERE userAccount=? AND userPassword=?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rst=null;
		User loginUser = new User();
		try{
			DBConnect dbConnect = new DBConnect();
			conn = dbConnect.getConnection();
			pstmt = conn.prepareStatement(GET_ONE_SQL);
			pstmt.setString(1, user.getUserAccount());
			pstmt.setString(2, user.getUserPassword());
			rst = pstmt.executeQuery();
			if(rst.next()){
				loginUser.setUserId(rst.getInt("userId"));
				loginUser.setUserAccount(rst.getString("userAccount"));
				loginUser.setUserPassword(rst.getString("userPassword"));
				loginUser.setUserSex(rst.getBoolean("userSex"));
				loginUser.setUserName(rst.getString("userName"));
				loginUser.setUserPhone(rst.getString("userPhone"));
				loginUser.setUserEmail(rst.getString("userEmail"));
				loginUser.setUserRegtime(new java.util.Date(rst.getDate("userRegtime").getTime()));
				return loginUser;
			}
		}catch(SQLException se){
			se.printStackTrace();
			System.out.println("����2");
			return null;
		}finally{
			try{
				pstmt.close();
				conn.close();
			}catch(SQLException se){ se.printStackTrace(); }
		}
		return null;
	}
	//查看注册账号是否重复
	public boolean searchAccount(User user) {
		String searchAccount =
				"SELECT * FROM usertable WHERE userAccount=?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rst=null;
		try{
			DBConnect dbConnect = new DBConnect();
			conn = dbConnect.getConnection();
			pstmt = conn.prepareStatement(searchAccount);
			pstmt.setString(1, user.getUserAccount());
			rst = pstmt.executeQuery();
			if(rst.next()){
				return true;
			}
		}catch(SQLException se){
			se.printStackTrace();
			System.out.println("����2");
			return false;
		}finally{
			try{
				pstmt.close();
				conn.close();
			}catch(SQLException se){ se.printStackTrace(); }
		}
		return false;
	}
	//查看注册昵称是否重复
	public boolean searchName(User user) {
		String searchName =
				"SELECT * FROM usertable WHERE userName=?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rst=null;
		try{
			DBConnect dbConnect = new DBConnect();
			conn = dbConnect.getConnection();
			pstmt = conn.prepareStatement(searchName);
			pstmt.setString(1, user.getUserName());
			rst = pstmt.executeQuery();
			if(rst.next()){
				return true;
			}
		}catch(SQLException se){
			se.printStackTrace();
			System.out.println("����2");
			return false;
		}finally{
			try{
				pstmt.close();
				conn.close();
			}catch(SQLException se){ se.printStackTrace(); }
		}
		return false;
	}
	public boolean insertUser(User user) throws SQLException {
		String sql = "INSERT INTO usertable " +
	            " (userAccount,userPassword,userName,userSex,userPhone,userEmail,userRegtime)VALUES(?,?,?,?,?,?,?)";
		 Connection conn;
		DBConnect dbConnect = new DBConnect();
		conn = dbConnect.getConnection();
		 PreparedStatement pstmt = null;
		 try{
			 pstmt = conn.prepareStatement(sql);
		     pstmt.setString(1,user.getUserAccount() );
		     pstmt.setString(2,user.getUserPassword());
		     pstmt.setString(3,user.getUserName());
		     pstmt.setBoolean(4,user.getUserSex());
		     pstmt.setString(5,user.getUserPhone());
		     pstmt.setString(6,user.getUserEmail());
		     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		     java.util.Date date = sdf.parse(sdf.format(new Date(System.currentTimeMillis())));
		     pstmt.setDate(7,new java.sql.Date(date.getTime()));
		     pstmt.executeUpdate();
		     return true;
		 }catch(SQLException se){
		     se.printStackTrace();
		     return false;
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}finally {
			 try{
					pstmt.close();
					conn.close();
				}catch(SQLException se){ se.printStackTrace(); }
		 }
	}
}