package com.jing.test.client;

import com.github.jingshouyan.crud.bean.R;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import com.github.jingshouyan.jdbc.comm.util.ConditionUtil;
import com.github.jingshouyan.jrpc.base.bean.Rsp;
import com.github.jingshouyan.jrpc.client.JrpcClient;
import com.github.jingshouyan.jrpc.client.Request;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author jingshouyan
 * 12/4/18 1:58 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class RetrieveTest {
    @Resource
    private JrpcClient jrpcClient;

    @Test
    public void single(){
        query(R.TYPE_SINGLE);
    }

    @Test
    public void multiple(){
        query(R.TYPE_MULTIPLE);
    }

    @Test
    public void page(){
        query(R.TYPE_PAGE);
    }

    @Test
    public void list(){
        query(R.TYPE_LIST);
    }

    @Test
    public void limit(){
        query(R.TYPE_LIMIT);
    }

    private void query(String type){
        R r = new R();
        r.setId("U18001");
        r.setIds(Lists.newArrayList("U18001","U20001"));
        r.setType(type);
        r.setPage(new Page());
        r.setConditions(ConditionUtil.newInstance()
//                .field("age").gt(20)
                .conditions());
        r.setBean("user");
        Rsp rsp = Request.newInstance()
                .setClient(jrpcClient)
                .setServer("test")
                .setMethod("retrieve")
                .setParamObj(r)
                .send();
        System.out.println(rsp);
    }
}
