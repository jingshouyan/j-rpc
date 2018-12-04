package com.github.jingshouyan.jrpc.crud.dml.method;


import com.github.jingshouyan.crud.bean.D;
import com.github.jingshouyan.jdbc.comm.bean.BaseBean;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.Method;
import org.springframework.context.ApplicationContext;

/**
 * @author jingshouyan
 * 12/3/18 5:10 PM
 */
public class Delete extends BaseCrud implements Method<D,Object> {

    public Delete(ApplicationContext ctx){
        super(ctx);
    }

    @Override
    public Object action(Token token,D d) {
        BaseDao<BaseBean> dao = dao(d.getBean());
        switch (d.getType()) {
            case TYPE_SINGLE:
                return dao.delete(d.getId());
            case TYPE_MULTIPLE:
                return dao.delete4List(d.getIds());
            default:
                throw new UnsupportedOperationException("unsupported delete type "+d.getType());
        }
    }
}