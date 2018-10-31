package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.jing.test.bean.IdQuery;
import com.jing.test.bean.UserBean;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Null;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/26 15:43
 */
@Component
public class GetUserInfo implements Method<IdQuery,List<UserBean>> {

    @Override
    public List<UserBean> action(Token token,IdQuery idQuery) {
        return null;
    }
}
