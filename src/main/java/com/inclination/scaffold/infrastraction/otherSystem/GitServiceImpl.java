package com.inclination.scaffold.infrastraction.otherSystem;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.inclination.scaffold.application.project.ProjectInformationDto;
import com.inclination.scaffold.application.users.UserDto;
import com.inclination.scaffold.constant.config.OtherSystemProperties;
import com.inclination.scaffold.constant.exception.TErrorCode;
import com.inclination.scaffold.constant.exception.TException;
import com.inclination.scaffold.infrastraction.otherSystem.git.GitService;
import com.inclination.scaffold.utils.CMDExecuteUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

@Service
public class GitServiceImpl implements GitService {


    /**
     * 配置信息
     */
    @Autowired
    private OtherSystemProperties projectProperties;

    private RestTemplate restTemplate = new RestTemplate();


    /**
     * 创建git仓库
     * @param artifactId
     * @param dto
     * @return
     */
    public boolean createGitRepository(String artifactId, UserDto dto) {
        // TODO Auto-generated method stub
        String orgModel=dto.getUserName()+"-org";
        String username=dto.getUserName();
        String password=dto.getUserPassword();
        String gitUrl=projectProperties.getGitUrl();
        String userMsg=username+":"+password;
        String base64userMsg= Base64.encodeBase64String(userMsg.getBytes());
        HttpHeaders header=new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
        header.set("Authorization", "Basic "+base64userMsg);
        Map<String,Object> param=new HashMap<>();
        param.put("auto_init", false);
        param.put("description", "");
        param.put("gitignores", "");
        param.put("license", "");
        param.put("name", artifactId);
        param.put("private", false);
        param.put("readme", "README.md");
        String paramStr= JSONObject.toJSONString(param);
        HttpEntity<String> entity=new HttpEntity<>(paramStr,header);
        try{
            ResponseEntity<String> resEntity=this.restTemplate.exchange(gitUrl+"api/v1/org/"+orgModel+"/repos", HttpMethod.POST,entity,String.class);
            String responseStr=resEntity.getBody();
            System.out.println("仓库创建成功...");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("仓库创建失败...");
            return false;
        }
        return true;
    }

    /**
     * 创建git项目
     * @param projectDto
     * @param dto
     * @return
     */
    public boolean crateGitProject(ProjectInformationDto projectDto, UserDto dto){
        String artifactId = projectDto.getArtifactId();
        String packageName = projectDto.getArtifactId();
        String protectPath = System.getProperty("user.dir") + "/project-temp/" + artifactId;
        String gitUrl = projectProperties.getGitUrl() + "" + dto.getUserName() + "-org/" + artifactId + ".git";
        File file = new File(protectPath);
        try {
            FileUtils.deleteDirectory(file.getParentFile());
            Git git = Git.cloneRepository()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(dto.getUserName(), dto.getUserPassword()))
                    .setURI(gitUrl)
                    .setDirectory(file)
                    .call();
            String cmd=projectProperties.getScript();
            if (Strings.isNullOrEmpty(cmd)){
                throw new TException(TErrorCode.ERROR_NO_MODEL_MAVEN_CODE,TErrorCode.ERROR_NO_MODEL_MAVEN_MSG);
            }
            cmd= MessageFormat.format(cmd, projectDto.getGroupId(), projectDto.getArtifactId(),projectDto.getVersion(),projectDto.getGroupId()+"."+projectDto.getArtifactId());
            String output = CMDExecuteUtil.executeCommand(cmd, file.getParentFile());
            System.out.println(output);
            git.add().addFilepattern(".").call();
            git.commit().setMessage("上传到仓库..").call();
            git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(dto.getUserName(), dto.getUserPassword())).call();
            git.close();
        } catch (GitAPIException | IOException | TException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /***
     * 创建git勾子
     * @param username
     * @param password
     * @param jenkinsUrl
     * @param gitUrl
     * @param orgModel
     * @param jobName
     * @param env
     */
    public void createInvoke(String username, String password, String jenkinsUrl, String gitUrl, String orgModel,String jobName, String env) {
        // TODO Auto-generated method stu
        //创建git钩子
        RestTemplate restTemplate=new RestTemplate();
        String userMsg=projectProperties.getGitAdmin()+":"+projectProperties.getGitPassword();
        String base64userMsg=Base64.encodeBase64String(userMsg.getBytes());
        HttpHeaders header=new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
        header.set("Authorization", "Basic "+base64userMsg);
        Map<String,Object> param=new HashMap<>();
        param.put("active", true);
        Map<String,Object> config=new HashMap<>();
        config.put("url", jenkinsUrl+"gogs-webhook/?job="+jobName+"-"+env);
        config.put("content_type", "json");
        param.put("config", config);
        List<String> events=new ArrayList<>();
        events.add("push");
        param.put("events", events);
        param.put("type", "gogs");
        String paramStr=JSONObject.toJSONString(param);
        System.out.println(paramStr);
        HttpEntity<String> entity=new HttpEntity<>(paramStr,header);
        String api=projectProperties.getGitUrl()+"api/v1/repos/"+orgModel+"/"+jobName.replace("-service","")+"/hooks";
        ResponseEntity<String> resEntity=restTemplate.exchange(api,HttpMethod.POST,entity,String.class);
        String responseStr=resEntity.getBody();
        System.out.println(responseStr);
    }
}