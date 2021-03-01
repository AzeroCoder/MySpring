package ioc;

import annotation.Component;
import annotation.Inject;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import ioc.definition.BeanDefinition;
import ioc.definition.BeanProperties;
import ioc.definition.ConstructorArg;
import lombok.Data;
import org.reflections.Reflections;
import util.Constant;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: zerocoder
 * @Description:
 * @Date: 2021/2/28 11:21
 */
@Data
public class BeanInitializer<T> {

    private BeanDefinitionRegistry registry;

    private BeanDefinitionReader reader;

    private BeanFactory factory;

    private String contextPath;

    private T contextObj;

    public BeanInitializer(T obj){
        this.registry = new BeanDefinitionRegistry();
        this.reader = new BeanDefinitionReader(this);
        this.contextObj = obj;
        this.factory = new BeanFactory(this);
        initRegistry();
    }

    private void initRegistry(){
        try {
            reader.loadContext();
            // 加载bean
            reader.loadBean();

            // 加载切面
            reader.loadAspect();
            registry.parsePointcut();
            registry.parseAspect();

            // 扫描注解并注入
            scanContext();
            initBean();
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

    private void scanContext(){
        Reflections reflections = new Reflections(contextPath);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Component.class);
        for (Class<?> clazz: classes)
            if (!clazz.isInterface()) {
                registerBean(clazz);
            }
    }

    private void registerBean(Class clazz){
        String id = clazz.getName();
        Component beanAnno = null;
        if (!clazz.isAnnotationPresent(Component.class)){
            Class[] interfaces = clazz.getInterfaces();
            if (interfaces.length == 0){
                return;
            }
            Optional<Class> interfaceFirst = Arrays.stream(interfaces).filter(aClass -> aClass.isAnnotationPresent(Component.class)).findFirst();
            if (interfaceFirst.isPresent()){
                Class interfaceClass = interfaceFirst.get();
                id = interfaceClass.getName();
                beanAnno = (Component) interfaceClass.getAnnotation(Component.class);
            }else {
                return;
            }
        }else {
            beanAnno = (Component) clazz.getAnnotation(Component.class);
        }
        if (StrUtil.isNotEmpty(beanAnno.name())) {
                id = beanAnno.name();
        }
        List<ConstructorArg> constructorArgList = new ArrayList<>();
        Constructor<?>[] constructors = ReflectUtil.getConstructors(clazz);
        for (int i = 0; i < constructors.length; i++) {
            Parameter[] parameters = constructors[i].getParameters();
            for (int j = 0; j < parameters.length; j++) {
                Parameter parameter = parameters[j];
                ConstructorArg constructorArg = new ConstructorArg();
                constructorArg.setName(parameter.getName());
                constructorArg.setIndex(j);
                constructorArg.setType(parameter.getType());
                constructorArgList.add(constructorArg);
//                    constructorArg.setValue(parameter.);
            }
        }
        List<BeanProperties> beanPropertiesList = new ArrayList<>();
        Field[] fields = ReflectUtil.getFields(clazz);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            BeanProperties beanProperties = new BeanProperties();
            String name = field.getName();
            beanProperties.setName(name);
            Class<?> type = field.getType();
            if (field.isAnnotationPresent(Inject.class)){
                String ref = field.getAnnotation(Inject.class).name();
                if (StrUtil.isEmpty(ref)){
                    ref = type.getName();
                }
                beanProperties.setRef(ref);
            }
            beanPropertiesList.add(beanProperties);
        }
        BeanDefinition beanDefinition = new BeanDefinition.Builder().className(clazz.getName())
                .constructorArgs(constructorArgList)
                .properties(beanPropertiesList)
                .build();
        registry.register(id, beanDefinition);
    }

    private void initBean() throws Exception {
        Set<String> ids = registry.getBeanDefinitionMap().keySet();
        for (String id: ids) {
            factory.getBean(id);
            factory.clearTags();
        }
        for (String id: ids) {
            factory.getBean(id);
            factory.clearTags();
        }
    }
}
