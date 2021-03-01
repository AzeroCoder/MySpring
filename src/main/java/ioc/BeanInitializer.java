package ioc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import lombok.Data;
import util.Constant;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 11:21
 */
@Data
public class BeanInitializer {

    private BeanDefinitionRegistry registry;

    private BeanDefinitionReader reader;

    private BeanFactory factory;

    private String contextPath;

    public BeanInitializer(){
        this.registry = new BeanDefinitionRegistry();
        this.reader = new BeanDefinitionReader(this);
        this.factory = new BeanFactory(registry);
        initRegistry();
    }

    private void initRegistry(){
        try {
            reader.loadBean();
            reader.loadAspect();
            reader.loadContext();
            registry.parsePointcut();
            registry.parseAspect();
        } catch (Exception e){
            LogFactory.get().error("[init BeanLoader] initRegistry error!", e);
        }
    }

    public <T> T getBean(String id){
        try {
            T bean = factory.getBean(id);
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
//
//    private void scanContext(String path){
//        if (StrUtil.isEmpty(path)){
//            path = contextPath;
//        }
//        File contextFile = FileUtil.file(path);
//        List<File> fileList = FileUtil.loopFiles(contextFile);
//        for (File file: fileList) {
//            if (file.isDirectory()){
//                scanContext(file.getAbsolutePath());
//            }
//            else {
//
//            }
//        }
//
//    }
}
