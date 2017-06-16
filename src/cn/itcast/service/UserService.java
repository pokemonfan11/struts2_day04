package cn.itcast.service;

import java.util.List;

import cn.itcast.dao.UserDao;
import cn.itcast.domain.User;

public class UserService {
	
	public User findUserForLogin(User user) {
		UserDao userDao = new UserDao();
		//调用dao层的方法
		User loginUser = userDao.findUserForLogin(user);
		return loginUser;
	}

	public void addUser(User user) {
		UserDao userDao = new UserDao();
		userDao.addUser(user);
		
	}

	public User checkByAjax(User user) {
		UserDao userDao = new UserDao();
		return userDao.checkByAjax(user);
	}

	public List<User> findUserByList() {
		UserDao userDao = new UserDao();
		
		return userDao.findUserByList();
	}

	public List<User> queryByCondition(User user) {
		UserDao userDao = new UserDao();
		return userDao.queryByCondition(user);
	}

	public void delteByUserId(int userID) {
		UserDao userDao = new UserDao();
		userDao.delteByUserId(userID);
		
	}

	public User queryUserByuserID(int userID) {
		UserDao userDao = new UserDao();
		return userDao.queryUserByuserID(userID);
	}

	public void updateUser(User user) {
		UserDao userDao = new UserDao();
		userDao.updateUser(user);
	}

	

}
