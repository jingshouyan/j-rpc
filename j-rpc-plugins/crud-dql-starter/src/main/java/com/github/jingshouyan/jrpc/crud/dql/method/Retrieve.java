package com.github.jingshouyan.jrpc.crud.dql.method;

import com.github.jingshouyan.crud.bean.RetrieveDTO;
import com.github.jingshouyan.crud.constant.CrudConstant;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.code.Code;
import com.github.jingshouyan.jrpc.base.exception.JrpcException;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.google.common.base.Preconditions;
import org.springframework.context.ApplicationContext;


/**
 * @author jingshouyan
 * 12/3/18 4:14 PM
 */
public class Retrieve implements Method<RetrieveDTO,Object> ,CrudConstant {

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
    public Object action(Token token, RetrieveDTO retrieveDTO) {
        BaseDao<BaseDO> dao = dao(retrieveDTO.getBean());
        switch (retrieveDTO.getType()){
            case TYPE_SINGLE:
                return dao.findField(retrieveDTO.getId(),retrieveDTO.getFields()).orElseThrow(()-> new JrpcException(NOT_FUND_BY_ID));
            case TYPE_MULTIPLE:
                return dao.findByIdsField(retrieveDTO.getIds(),retrieveDTO.getFields());
            case TYPE_LIST:
                return dao.queryField(retrieveDTO.getConditions(),retrieveDTO.getFields());
            case TYPE_LIMIT:
                return dao.queryFieldLimit(retrieveDTO.getConditions(), retrieveDTO.getPage(), retrieveDTO.getFields());
            case TYPE_PAGE:
                return dao.queryFieldPage(retrieveDTO.getConditions(), retrieveDTO.getPage(), retrieveDTO.getFields());
            default:
                throw new UnsupportedOperationException("unsupported retrieve type: "+retrieveDTO.getType());
        }
    }


}
