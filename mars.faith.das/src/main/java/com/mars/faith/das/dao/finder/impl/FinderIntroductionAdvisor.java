package com.mars.faith.das.dao.finder.impl;

import org.springframework.aop.support.DefaultIntroductionAdvisor;

import com.mars.faith.das.dao.impl.GenericDaoImpl;

/**
 * 
 * @author kriswang
 *
 * @param <T>
 */
public class FinderIntroductionAdvisor<T> extends DefaultIntroductionAdvisor {

    private static final long serialVersionUID = -2232695352601606218L;

    public FinderIntroductionAdvisor() {
        super(new FinderIntroductionInterceptor<T>());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean matches(Class clazz) {
        return GenericDaoImpl.class.isAssignableFrom(clazz);
    }

}