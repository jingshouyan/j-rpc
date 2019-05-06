package com.github.jingshouyan.jrpc.crud.dml.method;


import com.github.jingshouyan.crud.bean.DeleteDTO;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.crud.dml.ManipulationProperties;
import com.github.jingshouyan.jrpc.server.method.Method;
import org.springframework.context.ApplicationContext;

/**
 * @author jingshouyan
 * 12/3/18 5:10 PM
 */
public class Delete extends BaseCrud implements Method<DeleteDTO,Object> {

    public Delete(ApplicationContext ctx, ManipulationProperties properties){
        super(ctx);
        initAllows(properties.getDelete());
    }

    @Override
    public Object action(Token token, DeleteDTO deleteDTO) {
        accessCheck(deleteDTO.getBean());
        BaseDao<BaseDO> dao = dao(deleteDTO.getBean());
        switch (deleteDTO.getType()) {
            case TYPE_SINGLE:
                return dao.delete(deleteDTO.getId());
            case TYPE_MULTIPLE:
                return dao.delete4List(deleteDTO.getIds());
            default:
                throw new UnsupportedOperationException("unsupported delete type "+ deleteDTO.getType());
        }
    }
}
