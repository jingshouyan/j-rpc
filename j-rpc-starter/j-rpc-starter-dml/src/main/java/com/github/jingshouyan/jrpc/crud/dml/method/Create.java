package com.github.jingshouyan.jrpc.crud.dml.method;


import com.github.jingshouyan.crud.bean.CreateDTO;
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
public class Create extends BaseCrud implements Method<CreateDTO, Object> {


    public Create(ApplicationContext ctx, ManipulationProperties properties) {
        super(ctx);
        initAllows(properties.getCreate());
    }

    @Override
    public Object action(Token token, CreateDTO createDTO) {
        accessCheck(createDTO.getBean());
        BaseDao<BaseDO> dao = dao(createDTO.getBean());
        Class<BaseDO> clazz = dao.getClazz();
        switch (createDTO.getType()) {
            case TYPE_SINGLE:
                BaseDO bean = JsonUtil.toBean(createDTO.getData(), clazz);
                dao.insert(bean);
                return bean;
            case TYPE_MULTIPLE:
                List<BaseDO> list = JsonUtil.toList(createDTO.getData(), clazz);
                dao.batchInsert(list);
                return list;
            default:
                throw new UnsupportedOperationException("unsupported insert type: " + createDTO.getType());
        }
    }
}
