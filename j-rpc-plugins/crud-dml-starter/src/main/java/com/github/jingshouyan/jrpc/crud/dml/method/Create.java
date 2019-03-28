package com.github.jingshouyan.jrpc.crud.dml.method;


import com.github.jingshouyan.crud.bean.C;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import com.github.jingshouyan.jrpc.crud.dml.ManipulationProperties;
import com.github.jingshouyan.jrpc.server.method.Method;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author jingshouyan
 * 12/3/18 5:10 PM
 */
public class Create extends BaseCrud implements Method<C,Object> {



    public Create(ApplicationContext ctx, ManipulationProperties properties){
        super(ctx);
        initAllows(properties.getCreate());
    }

    @Override
    public Object action(Token token,C c) {
        accessCheck(c.getBean());
        BaseDao<BaseDO> dao = dao(c.getBean());
        Class<BaseDO> clazz = dao.getClazz();
        switch (c.getType()){
            case TYPE_SINGLE:
                BaseDO bean = JsonUtil.toBean(c.getData(),clazz);
                dao.insert(bean);
                return bean;
            case TYPE_MULTIPLE:
                List<BaseDO> list = JsonUtil.toList(c.getData(),clazz);
                dao.batchInsert(list);
                return list;
            default:
                throw new UnsupportedOperationException("unsupported insert type: "+c.getType());
        }
    }
}
