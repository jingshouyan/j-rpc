package com.github.jingshouyan.jrpc.crud.dml.method;

import com.github.jingshouyan.crud.constant.CrudConstant;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jingshouyan
 * 12/3/18 5:10 PM
 */
public abstract class BaseCrud implements CrudConstant {

    public static final int NOT_ALLOWED = -302;
    public static final String ALL = "*";

    static {
        Code.regCode(NOT_ALLOWED, "not allowed");
    }

    @Autowired
    private ApplicationContext ctx;

    private boolean all = false;
    private Set<String> allows = new HashSet<>();

    public BaseCrud(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    protected BaseDao<BaseDO> dao(String beanName) {
        String daoImplName = beanName + "DaoImpl";
        BaseDao<BaseDO> dao = ctx.getBean(daoImplName, BaseDao.class);
        Preconditions.checkNotNull(dao, beanName + "DaoImpl not found.");
        return dao;
    }

    /**
     * init
     *
     * @param beanNames 允许的 bean
     */
    void initAllows(String beanNames) {
        if (ALL.equals(beanNames)) {
            all = true;
        } else {
            String[] ss = beanNames.split(",");
            allows = new HashSet<>(ss.length);
            allows.addAll(Arrays.asList(ss));
        }
    }

    /**
     * @param beanName 对象名
     */
    void accessCheck(String beanName) {
        if (all || allows.contains(beanName)) {
            return;
        }
        throw new JrpcException(NOT_ALLOWED);
    }
}
