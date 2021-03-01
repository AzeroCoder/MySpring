package ioc.definition;

import enums.BeanScope;
import lombok.Data;

import java.util.List;

/**
 * @Author: zerocoder
 * @Description: 定义
 * @Date: 2020/9/13 16:22
 */
@Data
public class BeanDefinition {
    // 基于className构建Bean
    private String className;
    // 基于构造函数构建Bean
    private List<ConstructorArg> constructorArgs;
    // 基于静态工厂
    private String factoryName;
    // 基于工厂

    private String scop;
    private Boolean isLazy;
    private List<BeanProperties> properties;

    private BeanDefinition(){
        this.scop = BeanScope.SINGLTON.getDesc();
    }

    public static class Builder{
        private BeanDefinition beanDefinition = new BeanDefinition();

        public Builder className(String className){
            beanDefinition.setClassName(className);
            return this;
        }

        public Builder scope(String scope){
            beanDefinition.setScop(scope);
            return this;
        }

        public Builder constructorArgs(List<ConstructorArg> constructorArgs){
            beanDefinition.setConstructorArgs(constructorArgs);
            return this;
        }

        public Builder properties(List<BeanProperties> beanProperties){
            beanDefinition.setProperties(beanProperties);
            return this;
        }

        public BeanDefinition build(){
            return beanDefinition;
        }

    }
}
