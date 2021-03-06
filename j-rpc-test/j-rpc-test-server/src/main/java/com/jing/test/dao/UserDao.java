package com.jing.test.dao;

import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.jing.test.bean.UserBean;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author jingshouyan
 * 11/29/18 5:26 PM
 */
public interface UserDao extends BaseDao<UserBean> {
    /**
     * 测试
     *
     * @return 测试
     */
    SqlRowSet test();
}
