package cn.itcast.web.interceptor;

import org.apache.struts2.ServletActionContext;

import cn.itcast.domain.User;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

public class LoginIntreceptor extends MethodFilterInterceptor{

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		//判断用户是否登录
		//ServletActionContext.getRequest().getSession().setAttribute("user", loginUser);
		User user = (User) ServletActionContext.getRequest().getSession().getAttribute("user");
		if(user == null){
			//跳转到登录页面
			//如何在登录页面显示信息
			ActionSupport support = (ActionSupport) invocation.getAction();
			support.addFieldError("error", "请登录...");
			return "no_login";
		}else{
			//执行
			return invocation.invoke();
		}
		
	}

}
