package com.github.jingshouyan.jrpc.crud.dml.method;


import com.github.jingshouyan.crud.bean.UpdateDTO;
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
public class Update extends BaseCrud implements Method<UpdateDTO,Object> {

    public Update(ApplicationContext ctx, ManipulationProperties properties){
        super(ctx);
        initAllows(properties.getUpdate());
    }

    @Override
    public Object action(Token token, UpdateDTO updateDTO) {
        accessCheck(updateDTO.getBean());
        BaseDao<BaseDO> dao = dao(updateDTO.getBean());
        Class<BaseDO> clazz = dao.getClazz();
        switch (updateDTO.getType()){
            case TYPE_SINGLE:
                return dao.update(JsonUtil.toBean(updateDTO.getData(),clazz));
            case TYPE_MULTIPLE:
                return dao.batchUpdate(JsonUtil.toList(updateDTO.getData(),clazz));
            default:
                throw new UnsupportedOperationException("unsupported update type: " + updateDTO.getType());
        }
    }
}