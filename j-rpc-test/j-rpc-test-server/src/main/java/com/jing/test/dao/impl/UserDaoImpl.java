package com.jing.test.dao.impl;

import com.github.jingshouyan.jdbc.core.dao.impl.BaseDaoImpl;
import com.jing.test.bean.UserBean;
import com.jing.test.dao.UserDao;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

/**
 * @author jingshouyan
 * 11/29/18 5:27 PM
 */
@Repository
public class UserDaoImpl extends BaseDaoImpl<UserBean> implements UserDao {


    @Override
    public SqlRowSet test() {
        SqlRowSet rowSet = template.queryForRowSet("select * from UserBean where 1=2", new HashMap<>(0));

        return rowSet;
    }
}
