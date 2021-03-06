package com.inclination.scaffold.api.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.inclination.scaffold.api.request.user.*;
import com.inclination.scaffold.constant.exception.TErrorCode;
import com.inclination.scaffold.utils.ViewData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.inclination.scaffold.api.response.user.UserManageLoginResponse;
import com.inclination.scaffold.api.response.user.UserManagerQryResponse;
import com.inclination.scaffold.application.users.UserDto;
import com.inclination.scaffold.application.users.UserService;
import com.inclination.scaffold.constant.exception.TException;
import com.inclination.scaffold.utils.ModelMapUtils;
import com.inclination.scaffold.utils.ViewDataOld;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * @version 1.0
 * @author tianjingle All right protected time:2019:6:2
 *
 */
@RestController
@Api
public class UserManageApi {

	/**
	 * 注入操作用户的类
	 */
	@Autowired
	private UserService userManageServiceImp;
	/**
	 * 添加用户
	 * @param user 用户
	 * @throws Exception 
	 */
	@PostMapping(value="/user-add",consumes="application/json",produces = "application/json;charset=utf-8")
	@ApiOperation(value="创建用户",notes="创建用户")
	public void userAdd(@Valid @RequestBody UserManageAddRequest user,@SessionAttribute("CurrentUser") UserDto userdto) throws Exception{
		UserDto dto=ModelMapUtils.map(user, UserDto.class);
		userManageServiceImp.createUser(dto,userdto);
	}
	/**
	 * 查询所有用户
	 * @return 返回查询的结果
	 */
	@GetMapping(value="/user-find",produces = "application/json;charset=utf-8")
	@ApiOperation(value="查询用户",notes="查询用户")
	public ViewData userQry(@ModelAttribute UserQryByPages request,@SessionAttribute("CurrentUser") UserDto userdto){
		return userManageServiceImp.userFind(request,userdto);
	}


	@DeleteMapping(value = "/users-delete")
	@ApiOperation(value = "批量刪除用戶",notes = "批量刪除用戶")
	public ViewData batchDelete(String userIds){
		return userManageServiceImp.batchRemove(userIds);
	}
	/**
	 * 删除用户
	 * @throws TException 
	 */
	@DeleteMapping(value="/user-delete/{id}",produces="application/json;charset=utf-8")
	@ApiOperation(value="删除用户",notes="删除用户")
	public void userDelete(@PathVariable String id) throws TException{
		UserDto dto=new UserDto();
		dto.setId(Integer.parseInt(id));
		userManageServiceImp.deleteUser(dto);
	}
	/**
	 * 修改用户
	 * @throws TException 
	 */
	@PatchMapping(value="/user-update",produces="application/json;charset=utf-8")
	@ApiOperation(value="修改用户",notes="修改用户")
	public void userModify(@RequestBody UserManageModifyRequest request,HttpSession session) throws TException{
		UserDto dto=ModelMapUtils.map(request, UserDto.class);
		userManageServiceImp.modifyUser(dto,session);
	}
	
	@GetMapping(value="/user-time-out",produces="application/json;charset=utf-8")
	@ApiOperation(value="是否超时",notes="判断用户是否超时")
	public ViewData timeOut(HttpServletRequest request, HttpServletResponse response, HttpSession session){
		ViewData vd=new ViewData();
        if(null!=session.getAttribute("CurrentUser")&&!"".equals(session.getAttribute("1"))){
        	vd.setSuccess(true);
        }else{
        	vd.setSuccess(true);
        }
        return vd;
	}
	@PostMapping(value="/users-login")
	public ViewData userLogin(@Valid @RequestBody UserManageLoginRequest request,HttpServletRequest req) throws TException {
		UserDto dto=ModelMapUtils.map(request, UserDto.class);
		UserDto loginDto=userManageServiceImp.usersLogin(dto);
		UserManageLoginResponse response=ModelMapUtils.map(loginDto, UserManageLoginResponse.class);
		if(response!=null){
			req.getSession().setAttribute("CurrentUser", loginDto);
		}else{
			throw new TException(TErrorCode.ERROR_LOGIN_USER_CODE,TErrorCode.ERROR_LOGIN_USER_MSG);
		}
		return ViewData.success(response);
	}
}
