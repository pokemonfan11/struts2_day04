package cn.itcast.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;

import cn.itcast.domain.User;
import cn.itcast.utils.JDBCUtils;

public class UserDao {
	/**
	 * 判断登录
	 * @param user
	 * @return
	 * dbutils工具
	 */
	public User findUserForLogin(User user) {
		//1.创建QueryRunner
		QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());
		//2.sql语句 String
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ");
		sb.append("s_user ");
		sb.append("where loginName =? ");
		sb.append("and loginPwd =?");
		//3.查询
		User loginUser;
		try {
			loginUser = qr.query(sb.toString(), new BeanHandler<User>(User.class), 
					user.getLoginName(),user.getLoginPwd());
		} catch (SQLException e) {
			e.printStackTrace();
			//把异常抛给struts2去处理
			throw new RuntimeException("登录查询出错...");
		}
		
		return loginUser;
	}

	public void addUser(User user) {
		//1.queryrunner
		QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());
		StringBuffer sb = new StringBuffer();
		sb.append("insert into s_user values (null,?,?,?,?,?,?,?,?,?,?,?)");
		try {
			qr.update(sb.toString(), user.getUserName(),user.getLoginName(),user.getLoginPwd(),
					user.getSex(),user.getBirthday(),user.getEducation(),user.getTelephone(),
					user.getInterest(),user.getPath(),user.getFilename(),user.getRemark()
					);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("添加用户失败");
		}
		
	}

	public User checkByAjax(User user) {
		QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());
		StringBuffer sb = new StringBuffer();
		sb.append("select * from s_user where loginName = ?");
		User ajaxUser;
		try {
			ajaxUser = qr.query(sb.toString(), new BeanHandler<User>(User.class), user.getLoginName());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("检查用户失败");
		}
		return ajaxUser;
	}

	public List<User> findUserByList() {
		QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());
		StringBuffer sb = new StringBuffer();
		sb.append("select * from s_user");
		List<User> users;
		try {
			 users = qr.query(sb.toString(), new BeanListHandler<User>(User.class));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询所有用户失败...");
		}
		return users;
	}

	public List<User> queryByCondition(User user) {
		QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());
		//编写一个拼接的sql语句，StringBuffer
		StringBuffer sql = new StringBuffer();
		//以后的条件都从and或其他开始，不要从where开始
		sql.append("select * from s_user where 1=1 ");
		//判断用户名是否为空,在拼接sql语句时，语句前加空格
		if(!StringUtils.isBlank(user.getUserName())){
			sql.append(" and userName like '%"+user.getUserName()+"%'");
		}
		
		//判断性别
		if(!StringUtils.isBlank(user.getSex())){
			sql.append(" and sex ='"+user.getSex()+"'");
		}
		//学历
		if(!StringUtils.isBlank(user.getEducation())){
			sql.append(" and education ='"+user.getEducation()+"'");
		}
		//<s:select list="#{'1':'有','2':'无'}"  name="isUpload" id="isUpload"
		//1.如果isUpload  ，不用判断
		if(user.getIsUpload()!= null){
			if(user.getIsUpload().equals("1")){
				//不能用and path ！=''
				//数据库中判断空值需要用is not null
				sql.append(" and path is not null");
			}else if(user.getIsUpload().equals("2")){
				sql.append(" and path is null");
			}
		}
			
		System.out.println(sql.toString()+"-->"+user.getIsUpload());
		
		  try {
			  return qr.query(sql.toString(), new BeanListHandler<User>(User.class));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("条件查询出错...");
		}
		
		
	}

	public void delteByUserId(int userID) {
		QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());
		StringBuffer sql = new StringBuffer();
		sql.append("delete from s_user where userID = ?");
		try {
			qr.update(sql.toString(), userID);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("删除用户失败...");
		}
	}

	public User queryUserByuserID(int userID) {
		QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());
		StringBuffer sql = new StringBuffer();
		sql.append("select * from s_user where userID = ?");
		
		try {
			return qr.query(sql.toString(), new BeanHandler<User>(User.class), userID);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("查询用户详情失败...");
		}
	}
	
	/**
	 * private int	userID   ; //主键ID
	private String	userName ; //用户姓名
	private String	loginName;  //登录名
	private String	loginPwd ;   //密码//
	private String	sex      ;  //性别（例如：男，女）
	private String	birthday ;   //出生日期
	private String	education;   //学历（例如：研究生、本科、专科、高中）
	private String	telephone; //电话 
	private String	interest ;   //兴趣爱好（例如：体育、旅游、逛街）
	private String	path     ; //上传路径（path路径）
	private String	filename ;  //上传文件名称（文件名）
	private String	remark   ;//备注
	 * @param user
	 */

	public void updateUser(User user) {
		QueryRunner qr = new QueryRunner(JDBCUtils.getDataSource());
		StringBuffer sql = new StringBuffer();
		sql.append("update s_user set userName = ?, loginName=?,loginPwd=?,sex=?," +
				"birthday=?,education=?,telephone=?,interest=?,path=?,filename=?,remark=? ");
		sql.append(" where userID = ?");
		Object[] args ={user.getUserName(),user.getLoginName(),user.getLoginPwd(),
				user.getSex(),user.getBirthday(),user.getEducation(),user.getTelephone(),
				user.getInterest(),user.getPath(),user.getFilename(),user.getRemark(),
				user.getUserID()};
		try {
			qr.update(sql.toString(), args);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("更新失败...");
		}
		
	}

}
