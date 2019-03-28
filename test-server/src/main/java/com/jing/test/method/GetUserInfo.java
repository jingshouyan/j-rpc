package com.jing.test.method;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.server.method.Method;
import com.jing.test.bean.IdQuery;
import com.jing.test.bean.UserBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jingshouyan
 * #date 2018/10/26 15:43
 */
@Component("getUserInfo")
public class GetUserInfo implements Method<IdQuery,List<UserBean>> {

    // 本方法只会在 idQuery 校验成功执行
    @Override
    public List<UserBean> action(Token token,IdQuery idQuery) {
        // throw new JException(TestCode.JUST_ERROR);  //通过异常返回错误码
//         throw new JException(TestCode.JUST_ERROR,idQuery);  //通过异常返回错误码,并返回一些数据
        return idQuery.getIds().stream().map(id -> {
            UserBean userBean = new UserBean();
            userBean.setId(id);
            userBean.setAge(idQuery.getAge());
            userBean.setName(idQuery.getName());
            return userBean;
        }).collect(Collectors.toList());
    }
}
