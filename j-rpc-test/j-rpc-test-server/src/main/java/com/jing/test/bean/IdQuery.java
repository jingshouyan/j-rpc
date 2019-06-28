package com.jing.test.bean;

import com.github.jingshouyan.jrpc.base.constant.BaseConstant;
import com.jing.test.constant.TestCode;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/26 15:46
 */
@Data
public class IdQuery {
    /**
     * 基于 validation 的注解,如果验证不通过会返回 Code.PARAM_INVALID 错误码,如果想返回自定义的错误码,message 设置如下
     */
    @NotNull(message = BaseConstant.INVALID_CODE_PREFIX + TestCode.NAME_IS_NULL)
    @Size(min = 4, max = 20)
    private String name;

    @Min(5)
    @Max(99)
    private int age = 10;

    @NotNull
    @Size(min = 1, max = 100)
    private List<String> ids;
}
