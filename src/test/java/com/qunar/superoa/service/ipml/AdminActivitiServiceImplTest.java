package com.qunar.superoa.service.ipml;

import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Auther: lee.guo
 * @Date:Created in 2018/9/4_下午4:32
 * @Despriction:
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminActivitiServiceImplTest {

     final static Logger logger = LoggerFactory.getLogger(AdminActivitiServiceImplTest.class);

    @Autowired
     AdminActivitiServiceImpl adminActivitiService;

    @Test
    public void findProcessDefinition() {

        List<ProcessDefinition> list = adminActivitiService.findProcessDefinition();

        if(list != null && list.size()>0){
            for(ProcessDefinition processDefinition:list){
                logger.info("流程定义ID:"+processDefinition.getId());//流程定义的key+版本+随机生成数
                logger.info("流程定义名称:"+processDefinition.getName());//对应HelloWorld.bpmn文件中的name属性值
                logger.info("流程定义的key:"+processDefinition.getKey());//对应HelloWorld.bpmn文件中的id属性值
                logger.info("流程定义的版本:"+processDefinition.getVersion());//当流程定义的key值相同的情况下，版本升级，默认从1开始
                logger.info("资源名称bpmn文件:"+processDefinition.getResourceName());
                logger.info("资源名称png文件:"+processDefinition.getDiagramResourceName());
                logger.info("部署对象ID:"+processDefinition.getDeploymentId());
                logger.info("################################");
            }
        }
    }


    @Test
    public void viewPic() {
        try {
            InputStream in = adminActivitiService.viewPic("a0abbb33-b019-11e8-ba42-feac194325c3");
            //将图片生成到指定目录下
            File file = new File("/Users/lee.guo/activiti.png");
            //将输入流的图片写到磁盘下
            FileUtils.copyInputStreamToFile(in, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}