package com.github.jingshouyan.jrpc.crud.dml.method;

import com.github.jingshouyan.crud.constant.CrudConstant;
import com.github.jingshouyan.jdbc.comm.bean.BaseBean;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JException;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jingshouyan
 * 12/3/18 5:10 PM
 */
public abstract class BaseCrud implements CrudConstant {

    public static final int NOT_ALLOWED = -302;
    static {
        Code.regCode(NOT_ALLOWED,"not allowed");
    }

    @Autowired
    private ApplicationContext ctx;

    private boolean all = false;
    private Set<String> allows = new HashSet<>();

    public BaseCrud(ApplicationContext ctx){
        this.ctx = ctx;
    }

    protected BaseDao<BaseBean> dao(String beanName){
        String daoImplName = beanName + "DaoImpl";
        BaseDao<BaseBean> dao = ctx.getBean(daoImplName,BaseDao.class);
        Preconditions.checkNotNull(dao, beanName + "DaoImpl not found.");
        return dao;
    }

    /**
     * init
     * @param beanNames 允许的 bean
     */
    void initAllows(String beanNames){
        if("*".equals(beanNames)){
            all = true;
        } else {
            String[] ss = beanNames.split(",");
            allows = new HashSet<>(ss.length);
            for (String bean : ss){
                allows.add(bean);
            }
        }
    }

    /**
     *
     * @param beanName
     */
    void accessCheck(String beanName) {
        if(all || allows.contains(beanName)){
            return;
        }
        throw new JException(NOT_ALLOWED);
    }
}
