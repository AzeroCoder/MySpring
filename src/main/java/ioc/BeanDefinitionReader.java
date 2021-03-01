package ioc;

import aop.AspectDefinition;
import aop.AspectPointcut;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import ioc.definition.BeanDefinition;
import ioc.definition.BeanProperties;
import ioc.definition.ConstructorArg;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zerocoder
 * @Description: 读取XML中bean的构造信息，并注册
 * @Date: 2020/9/13 17:15
 */
@Data
public class BeanDefinitionReader {

    private final String configXmlPath = FileUtil.readUtf8String(Constant.CONFIG_XML);

    private Document configDocument = Jsoup.parse(configXmlPath);

    private final BeanInitializer initionalizer;
    /**
     * 注册bean
     */
    private final BeanDefinitionRegistry registry;

    public BeanDefinitionReader(BeanInitializer initionalizer) {
        this.initionalizer = initionalizer;
        this.registry = initionalizer.getRegistry();
    }

    public void loadContext(){
        Element elements = configDocument.selectFirst("context-component-scan");
        if (StrUtil.isNotEmpty(elements.text())){
            initionalizer.setContextPath(elements.text());
        }
    }

    /**
     * 加载bean
     * @throws ClassNotFoundException
     */
    public void loadBean() throws ClassNotFoundException {
        Elements elements = configDocument.select("bean");
        for (Element element: elements) {
            // key
            String id = element.attr("id");
            // 类
            String className = element.attr("class");
            // 构造器
            Elements constructorArgs = element.select("constructor-arg");
            List<ConstructorArg> constructorArgList = new ArrayList<>();
            if (!constructorArgs.isEmpty()){
                for (int i = 0; i < constructorArgs.size(); i++) {
                    Element constructorArg = constructorArgs.get(i);
                    String value = constructorArg.attr("value");
                    String type = constructorArg.attr("type");
                    String name = constructorArg.attr("name");
                    Class clazz = Class.forName(type);
                    ConstructorArg arg = new ConstructorArg();
                    arg.setValue(value);
                    arg.setIndex(i);
                    arg.setType(clazz);
                    arg.setName(name);
                    constructorArgList.add(arg);
                }
            }
            Elements propertyEles = element.select("property");
            List<BeanProperties> beanPropertiesList = new ArrayList<>();
            for (Element propertEle: propertyEles) {
                String value = propertEle.attr("value");
                String ref = propertEle.attr("ref");
                String name = propertEle.attr("name");
                BeanProperties beanProperties = new BeanProperties();
                beanProperties.setName(name);
                beanProperties.setValue(value);
                beanProperties.setRef(ref);
                beanPropertiesList.add(beanProperties);
            }
            BeanDefinition beanDefinition = new BeanDefinition.Builder().className(className)
                    .constructorArgs(constructorArgList)
                    .properties(beanPropertiesList)
                    .build();
            registry.register(id, beanDefinition);
        }
    }

    /**
     * aop加载
     */
    public void loadAspect() {
        Elements pointEles = configDocument.select("aop-config aop-pointcut");
        for (Element element: pointEles) {
            AspectPointcut aspectPointcut = new AspectPointcut();
            aspectPointcut.setExpression(element.attr("expression"));
            aspectPointcut.setId(element.attr("id"));
            registry.addPointcut(aspectPointcut);
        }
        Elements aspectEles = configDocument.select("aop-config aop-aspect");
        for (Element element: aspectEles) {
            String ref = element.attr("ref");
            Element beforeEle = element.selectFirst("aop-before");
            Element aroundEle = element.selectFirst("aop-around");
            Element afterEle = element.selectFirst("aop-after");
            AspectDefinition aspectDefinition = new AspectDefinition();
            aspectDefinition.setName(ref);
            if (beforeEle != null){
                aspectDefinition.setBeforeMethod(beforeEle.attr("method"));
                aspectDefinition.setBeforePoint(beforeEle.attr("pointcut-ref"));
            }
            if (aroundEle != null){
                aspectDefinition.setAroundMethod(aroundEle.attr("method"));
                aspectDefinition.setAroundPoint(aroundEle.attr("pointcut-ref"));
            }
            if (afterEle != null){
                aspectDefinition.setAfterMethod(afterEle.attr("method"));
                aspectDefinition.setAfterPoint(afterEle.attr("pointcut-ref"));
            }
            registry.addAspect(aspectDefinition);
        }
    }
}
