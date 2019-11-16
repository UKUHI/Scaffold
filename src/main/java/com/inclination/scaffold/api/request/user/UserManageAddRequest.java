package com.inclination.scaffold.api.request.user;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserManageAddRequest {
	
	@ApiModelProperty(name="userName",value="用户名称")
	@NotBlank(message="不能为空")
	private String userName;
	
	@ApiModelProperty(name="loginid",value="手机号")
	@NotBlank(message="不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号码格式错误")
	private String loginId;
	
	@ApiModelProperty(name="userpassword",value="用户密码")
	@NotBlank(message="不能为空")
	private String userPassword;
	
	@ApiModelProperty(name="useremil",value="邮箱")
	@NotBlank(message="不能为空")
	private String userEmil;
	
	@ApiModelProperty(name="roid",value="权限")
	@NotNull(message="不能为空")
    private Integer roId;
	
}
