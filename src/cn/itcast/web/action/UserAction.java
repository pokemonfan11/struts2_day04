package cn.itcast.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import sun.awt.geom.AreaOp.AddOp;

import cn.itcast.domain.User;
import cn.itcast.service.UserService;
import cn.itcast.utils.SysContent;
import cn.itcast.utils.UploadUtils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

public class UserAction extends ActionSupport implements ModelDriven<User>{
	private User user = new User();
	
	private File upload;
	private String uploadContentType;
	private String uploadFileName;
	
	
	
	
	public String login() throws Exception {
		System.out.println("登录。。。");
		UserService userService = new UserService();
		//根据user到service层去找这个用户是否存在，返回user对象
		User loginUser = userService.findUserForLogin(user);
		//判断用户是否存在
		if(loginUser!=null){
			//把用户存入session
			ServletActionContext.getRequest().getSession().setAttribute("user", loginUser);
			return "loginSuccess";
		}else{
			//添加登录失败消息
			addFieldError("error", "用户名或密码错误...");
			return INPUT;
		}
		
	}
	
	/**
	 * 添加用户
	 * You can also use InputConfig annotation to change result name returned when validation errors occurred.
	 */
	//添加验证出错后跳转的结果视图
	@InputConfig(resultName="addInput")
	public String add() throws Exception {
		System.out.println("addUser");
		
		//判断是否上传附件
		if(upload!=null){
			//1.生成随机文件夹
			String randomDir = UploadUtils.generateRandomDir(uploadFileName);
			//2.生成随机文件名
			String randomFileName = UploadUtils.generateRandonFileName(uploadFileName);
			//3.保存文件真实路径
			String realPath = SysContent.BASE_DIR+randomDir+"/"+randomFileName;
			//保存文件
			FileUtils.copyFile(upload, new File(realPath));
			user.setPath(randomDir+"/"+randomFileName);
			user.setFilename(uploadFileName);//保存真实文件名
		}
		//1.没有附件
		UserService userService = new UserService();
		userService.addUser(user);
		
		
		return "addSuccess";
	}
	
	/**
	 * 使用ajax判断登录名是否存在
	 * @return
	 * @throws Exception
	 */
	public String checkByAjax() throws Exception {
		UserService userService = new UserService();
		User ajaxUser = userService.checkByAjax(user);
		//存放是否存在用户的一个容器，返回给页面
		Map<String, Object> map = new HashMap<String, Object>();
		if(ajaxUser == null){
			map.put("ifExist", false); 
		}else{
			map.put("ifExist", true);
		}
		
		
		
		//1.struts中使用一个json插件来返回json数据
		//需要把数据放入值栈中
		//ActionContext.getContext().getValueStack().push(map);
		//配置struts.xml中json结果集
		//<result name="json" type="json"></result>
		//2.可以把json数据放入map栈,结果集中的配置与上面不一样
		ActionContext.getContext().put("resultMap", map);
		
		return "json";
	}
	
	
	/**
	 * 获取所有员工
	 * d.add(3,2,'用户管理','${pageContext.request.contextPath}/user_list.action','','mainFrame');
	 * <action name="user_*" class="cn.itcast.web.action.UserAction" method="{1}">
	 */
	public String list() throws Exception {
		int a = 1/0;
		
		UserService userService = new UserService();
		//把所有返回的user放入list中
//		List<User> users = userService.findUserByList();
		
		List<User> queryUsers = userService.queryByCondition(user);
		//判断user是否为null
		System.out.println("user:"+user);
		
		//把数据放入map栈
		ActionContext.getContext().put("list", queryUsers);
		
		return "listSuccess";
	}
	
	
	/**
	 * 根据条件查询
	 */
	public String listCondition() throws Exception {
		UserService userService = new UserService();
		List<User> queryUsers = userService.queryByCondition(user);
		ActionContext.getContext().put("list", queryUsers);
		return "listSuccess";
	}
	
	/**
	 * 根据id来删除
	 */
	public String delete() throws Exception {
		UserService userService = new UserService();
		userService.delteByUserId(user.getUserID());
		return "deleteSuccess";
	}
	
	/**
	 * 根据userID查询一个用户
	 */
	public String view() throws Exception {
		UserService userService = new UserService();
		User queryUser = userService.queryUserByuserID(user.getUserID());
		//把查询出来的信息放入值栈
		ActionContext.getContext().getValueStack().push(queryUser);
		
		return "viewSuccess";
	}
	
	/**
	 * 下载简历
	 */
	public String download() throws Exception {
		//contentType  文件类型 根据 mime-type来获取
		//contentDisposition 附件打开方式  attachment;filename="document.pdf"
		//inputStream   输入流
		//1.根据id去查找用户，直接使用查询用户的方法
		UserService userService = new UserService();
		User queryUser = userService.queryUserByuserID(user.getUserID());
		String filename = queryUser.getFilename();
		//2.下载
		String contentType = ServletActionContext.getServletContext().getMimeType(filename);
		//文件名中文
		//获取浏览器类型
		String agent = ServletActionContext.getRequest().getHeader("user-agent");
		filename = cn.itcast.utils.FileUtils.encodeDownloadFilename(filename, agent);
		String contentDisposition ="attachment;filename="+filename;
		//输入流
		String realPath = SysContent.BASE_DIR+queryUser.getPath();
		InputStream inputStream = new FileInputStream(realPath);
		
		ActionContext.getContext().put("contentType", contentType);
		ActionContext.getContext().put("contentDisposition", contentDisposition);
		ActionContext.getContext().put("inputStream", inputStream);
		
		return "downloadSuccess";
	}
	
	
	/**
	 * 编辑方法，获取一条编辑数据
	 */
	
	public String edit() throws Exception {
		UserService userService = new UserService();
		User queryUser = userService.queryUserByuserID(user.getUserID());
		//把数据放入值栈
		ActionContext.getContext().getValueStack().push(queryUser);
		return "editSuccess";
	}
	
	/**
	 * 编辑
	 */
	@InputConfig(resultName="eidtInput")
	public String toedit() throws Exception {
		//A.如果密码框为空，不需要修改密码
		//B.如果上传了文件，需要把原先的文件先删除，后上传
		
		//1.根据id获取编辑用户，获取原先文件的路径
		UserService userService = new UserService();
		User oldUser = userService.queryUserByuserID(user.getUserID());
		
		//如果密码为空，把老密码传给user
		if(StringUtils.isBlank(user.getLoginPwd())){
			user.setLoginPwd(oldUser.getLoginPwd());
		}
			
		
		//2.如果没有上传文件
		if(upload == null){
			//把老的文件路径和文件名赋给新的user
			user.setPath(oldUser.getPath());
			user.setFilename(oldUser.getFilename());
		}else{
			//上传了文件
			//判断是否有老文件，如果有，干掉，没有直接上传
			if(oldUser.getPath() != null){
				String oldPath = SysContent.BASE_DIR+oldUser.getPath();
				new File(oldPath).delete();
			}
			//上传文件
			String randomDir = UploadUtils.generateRandomDir(uploadFileName);
			String randomFile = UploadUtils.generateRandonFileName(uploadFileName);
			String newPath = SysContent.BASE_DIR+randomDir+"/"+randomFile;
			//拷贝文件
			FileUtils.copyFile(upload, new File(newPath));
			//设置新文件的路径和名字
			user.setPath(randomDir+"/"+randomFile);
			user.setFilename(uploadFileName);
		}
		//保存数据
		userService.updateUser(user);
		return "toEditSuccess";
	} 
	

	@Override
	public User getModel() {
		return user;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
}
