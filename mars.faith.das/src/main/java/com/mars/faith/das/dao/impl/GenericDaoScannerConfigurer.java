/*********************************************************************************
 * <p>
 * Perforce File Stats:
 * 
 * <pre>
 * $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/impl/GenericDaoScannerConfigurer.java#1 $
 * $DateTime: 2013/01/30 06:17:51 $
 * $Change: 6865744 $
 * </pre>
 * 
 * </p>
 * 
 * @author $Author: wdong $
 * @version $Revision: #1 $ Copyright Notice This file contains proprietary
 * information of Amazon.com. Copying or reproduction without prior written
 * approval is prohibited. Copyright (c) 2012 Amazon.com. All rights reserved.
 *********************************************************************************/
package com.mars.faith.das.dao.impl;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.mars.faith.das.dao.GenericDao;
import com.mars.faith.das.dao.exception.DataAccessDaoException;

/**
 * DAO definitions auto scanner.
 * @author wdong
 *
 */
public class GenericDaoScannerConfigurer implements
        BeanDefinitionRegistryPostProcessor, InitializingBean,
        ApplicationContextAware, BeanNameAware {

    private String beanName;
    private List<String> packageList;

    private ApplicationContext applicationContext;

    public void setPackageList(List<String> packageList) {
        this.packageList = packageList;
    }

    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void afterPropertiesSet() throws Exception {

    }

    public void postProcessBeanDefinitionRegistry(
            BeanDefinitionRegistry beanDefinitionRegistry)
            throws BeansException {
        Scanner scanner = new Scanner(beanDefinitionRegistry);
        scanner.setResourceLoader(this.applicationContext);
        String[] packages = new String[packageList.size()];
        scanner.scan(packageList.toArray(packages));
    }

    private final class Scanner extends ClassPathBeanDefinitionScanner {

        public Scanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        @Override
        protected boolean isCandidateComponent(
                AnnotatedBeanDefinition beanDefinition) {
            List<String> interfaces = Arrays.asList(beanDefinition.getMetadata().getInterfaceNames());
            return (beanDefinition.getMetadata().isInterface() && interfaces.contains(GenericDao.class.getName()));
        }

        @Override
        protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
            return true;
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages){
            Assert.notEmpty(basePackages, "At least one base package must be specified");
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
            if(beanDefinitions.isEmpty()) {
                logger.warn("");
            } else {
                for(BeanDefinitionHolder holder : beanDefinitions) {
                    GenericBeanDefinition definition = (GenericBeanDefinition)holder.getBeanDefinition();
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.childBeanDefinition("abstractDao");
                    builder.addPropertyValue("proxyInterfaces", definition.getBeanClassName());
                    String domainObjectClassName = "";
                    try{
                        //use ClassLoader to find the DAO interface and its parameterized types.
                        Class<?> clazz = builder.getClass().getClassLoader().loadClass(definition.getBeanClassName());
                        Type[] params = ((ParameterizedType)clazz.getGenericInterfaces()[0]).getActualTypeArguments();
                        if(params.length > 0) {
                            for(Type type : params) {
                                Class<?> c = (Class<?>)type;
                                /*if(AbstractDomainObject.class.isAssignableFrom(c)){
                                    domainObjectClassName = c.getName();
                                    break;
                                }*/
                                domainObjectClassName = c.getName();
                            }
                        }
                        if(domainObjectClassName.equals("")) {
                            throw new DataAccessDaoException(beanName + ", The DAO interface [" + definition.getBeanClassName() + "] does not contain Parameterized Type that extends AbstractDomainObject. ");
                        }
                    }catch (ClassNotFoundException ex) {
                         throw new DataAccessDaoException(ex);
                    }

                    builder.addPropertyValue("target",createAbstractDaoTarget(domainObjectClassName));
                    getRegistry().registerBeanDefinition(this.getDaoName(definition.getBeanClassName()),builder.getBeanDefinition());
                }
            }
            return beanDefinitions;
        }

        private BeanDefinition createAbstractDaoTarget(final String type) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.childBeanDefinition("abstractDaoTarget");
            builder.addConstructorArgValue(type);
            return builder.getBeanDefinition();
        }

        private String getDaoName(String beanName) {
            return StringUtils.uncapitalize(beanName.split("\\.")[beanName.split("\\.").length - 1]);
        }
    }

}