package com.github.jingshouyan.jrpc.crud.dql.method;

import com.github.jingshouyan.crud.bean.R;
import com.github.jingshouyan.crud.constant.CrudConstant;
import com.github.jingshouyan.jdbc.comm.bean.BaseBean;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.google.common.base.Preconditions;
import org.springframework.context.ApplicationContext;


/**
 * @author jingshouyan
 * 12/3/18 4:14 PM
 */
public class Retrieve implements Method<R,Object> ,CrudConstant {

    private ApplicationContext ctx;

    public Retrieve(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    private BaseDao<BaseBean> dao(String beanName){
        String daoImplName = beanName + "DaoImpl";
        BaseDao<BaseBean> dao = ctx.getBean(daoImplName,BaseDao.class);
        Preconditions.checkNotNull(dao, beanName + "DaoImpl not found.");
        return dao;
    }

    @Override
    public Object action(Token token,R r) {
        BaseDao<BaseBean> dao = dao(r.getBean());
        switch (r.getType()){
            case TYPE_SINGLE:
                return dao.find(r.getId()).orElse(null);
            case TYPE_MULTIPLE:
                return dao.findByIds(r.getIds());
            case TYPE_LIST:
                return dao.query(r.getConditions());
            case TYPE_LIMIT:
                return dao.queryLimit(r.getConditions(), r.getPage());
            case TYPE_PAGE:
                return dao.queryPage(r.getConditions(), r.getPage());
            default:
                throw new UnsupportedOperationException("unsupported retrieve type: "+r.getType());
        }
    }


}
