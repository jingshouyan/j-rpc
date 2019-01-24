package com.github.jingshouyan.jrpc.crud.dql.method;

import com.github.jingshouyan.crud.bean.R;
import com.github.jingshouyan.crud.constant.CrudConstant;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JException;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.google.common.base.Preconditions;
import org.springframework.context.ApplicationContext;


/**
 * @author jingshouyan
 * 12/3/18 4:14 PM
 */
public class Retrieve implements Method<R,Object> ,CrudConstant {

    private ApplicationContext ctx;

    public static final int NOT_FUND_BY_ID = -301;
    static {
        Code.regCode(NOT_FUND_BY_ID,"not fund by id");
    }

    public Retrieve(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    private BaseDao<BaseDO> dao(String beanName){
        String daoImplName = beanName + "DaoImpl";
        BaseDao<BaseDO> dao = ctx.getBean(daoImplName,BaseDao.class);
        Preconditions.checkNotNull(dao, beanName + "DaoImpl not found.");
        return dao;
    }

    @Override
    public Object action(Token token,R r) {
        BaseDao<BaseDO> dao = dao(r.getBean());
        switch (r.getType()){
            case TYPE_SINGLE:
                return dao.findField(r.getId(),r.getFields()).orElseThrow(()-> new JException(NOT_FUND_BY_ID));
            case TYPE_MULTIPLE:
                return dao.findByIdsField(r.getIds(),r.getFields());
            case TYPE_LIST:
                return dao.queryField(r.getConditions(),r.getFields());
            case TYPE_LIMIT:
                return dao.queryFieldLimit(r.getConditions(), r.getPage(), r.getFields());
            case TYPE_PAGE:
                return dao.queryFieldPage(r.getConditions(), r.getPage(), r.getFields());
            default:
                throw new UnsupportedOperationException("unsupported retrieve type: "+r.getType());
        }
    }


}
