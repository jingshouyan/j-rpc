/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jingshouyan.hmily.interceptor;

import com.github.jingshouyan.jrpc.base.bean.Token;
import com.github.jingshouyan.jrpc.base.concurrent.threadlocal.TokenContextLocal;
import com.github.jingshouyan.jrpc.base.util.json.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.dromara.hmily.common.bean.context.HmilyTransactionContext;
import org.dromara.hmily.common.constant.CommonConstant;
import org.dromara.hmily.core.concurrent.threadlocal.HmilyTransactionContextLocal;
import org.dromara.hmily.core.interceptor.HmilyTransactionInterceptor;
import org.dromara.hmily.core.service.HmilyTransactionAspectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author jingshouyan
 * #date 2018/12/24 17:49
 */
@Component
public class JrpcHmilyTransactionInterceptor implements HmilyTransactionInterceptor {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JrpcHmilyTransactionInterceptor.class);

    private final HmilyTransactionAspectService hmilyTransactionAspectService;

    @Autowired
    public JrpcHmilyTransactionInterceptor(final HmilyTransactionAspectService hmilyTransactionAspectService) {
        this.hmilyTransactionAspectService = hmilyTransactionAspectService;
    }

    @Override
    public Object interceptor(final ProceedingJoinPoint pjp) throws Throwable {
        HmilyTransactionContext hmilyTransactionContext;
        Token token = TokenContextLocal.getInstance().get();
        String context = token == null ? null : token.get(CommonConstant.HMILY_TRANSACTION_CONTEXT);
        if (StringUtils.isNoneBlank(context)) {
            hmilyTransactionContext = JsonUtil.toBean(context, HmilyTransactionContext.class);
        } else {
            hmilyTransactionContext = HmilyTransactionContextLocal.getInstance().get();
        }
        return hmilyTransactionAspectService.invoke(hmilyTransactionContext, pjp);
    }

}
