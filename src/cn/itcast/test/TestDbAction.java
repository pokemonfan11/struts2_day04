package cn.itcast.test;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.opensymphony.xwork2.ActionSupport;

import cn.itcast.utils.JDBCUtils;

public class TestDbAction extends ActionSupport{
	@Override
	public String execute() throws Exception {
		//测试连接数据库
		Connection conn = JDBCUtils.getConnection();
		System.out.println(conn);
		return NONE;
	}
}
