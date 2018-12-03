package com.github.jingshouyan.jrpc.crud.dml.method;

import com.github.jingshouyan.crud.constant.CrudConstant;
import com.github.jingshouyan.jdbc.comm.bean.BaseBean;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author jingshouyan
 * 12/3/18 5:10 PM
 */
public abstract class BaseCrud implements CrudConstant {

    @Autowired
    private ApplicationContext ctx;

    public BaseCrud(ApplicationContext ctx){
        this.ctx = ctx;
    }

    protected BaseDao<BaseBean> dao(String beanName){
        String daoImplName = beanName + "DaoImpl";
        BaseDao<BaseBean> dao = ctx.getBean(daoImplName,BaseDao.class);
        Preconditions.checkNotNull(dao, beanName + "DaoImpl not found.");
        return dao;
    }
}
