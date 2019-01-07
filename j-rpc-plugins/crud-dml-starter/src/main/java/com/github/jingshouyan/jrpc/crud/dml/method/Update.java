package com.github.jingshouyan.jrpc.crud.dml.method;


import com.github.jingshouyan.crud.bean.U;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.crud.dml.ManipulationProperties;
import com.github.jingshouyan.jrpc.server.method.Method;
import org.springframework.context.ApplicationContext;

/**
 * @author jingshouyan
 * 12/3/18 5:10 PM
 */
public class Update extends BaseCrud implements Method<U,Object> {

    public Update(ApplicationContext ctx, ManipulationProperties properties){
        super(ctx);
        initAllows(properties.getUpdate());
    }

    @Override
    public Object action(Token token,U u) {
        accessCheck(u.getBean());
        BaseDao<BaseDO> dao = dao(u.getBean());
        Class<BaseDO> clazz = dao.getClazz();
        switch (u.getType()){
            case TYPE_SINGLE:
                return dao.update(JsonUtil.toBean(u.getData(),clazz));
            case TYPE_MULTIPLE:
                return dao.batchUpdate(JsonUtil.toList(u.getData(),clazz));
            default:
                throw new UnsupportedOperationException("unsupported update type: " + u.getType());
        }
    }
}