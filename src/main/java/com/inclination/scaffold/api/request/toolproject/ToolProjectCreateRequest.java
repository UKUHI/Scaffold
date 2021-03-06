package com.inclination.scaffold.api.request.toolproject;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ToolProjectCreateRequest {

	@ApiModelProperty(name = "name", value = "外部工具软件的名称")
	@NotBlank(message = "外部工具软件的名称不能为空")
	private String name;

	@ApiModelProperty(name = "url", value = "外部工具软件地址")
	@NotBlank(message = "外部工具软件的地址不能为空")
	private String url;

	@ApiModelProperty(name = "name", value = "工具软件的用户名")
	private String userName;
	
	@ApiModelProperty(name = "url", value = "工具软件的密码")
	private String userPassword;

}
