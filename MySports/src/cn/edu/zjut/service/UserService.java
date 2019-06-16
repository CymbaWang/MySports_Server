package cn.edu.zjut.service;

import java.sql.SQLException;

import cn.edu.zjut.dao.UserDao;
import cn.edu.zjut.net.ChatSocket;
import cn.edu.zjut.pojo.User;
import cn.edu.zjut.util.OnMap;

public class UserService {
	public User Login(User user) {
		UserDao userDao = new UserDao();
		User loginUser = userDao.searchUser(user);
		if(loginUser!=null) {
			return loginUser;
		}
		else{
			return null;
		}
	}
	public String Register(User user) throws SQLException {
		UserDao userDao = new UserDao();
		if(userDao.searchAccount(user)) {
			return "accountTwice";
		}
		else if(userDao.searchName(user)) {
			return "nameTwice";
		}
		else if(userDao.insertUser(user)){
			return "success";
		}else {
			return "fail";
		}
	}




}
