package com.github.jingshouyan.crud.bean;

import com.github.jingshouyan.crud.constant.CrudConstant;
import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jingshouyan
 * 12/3/18 2:56 PM
 */
@Getter
@Setter
@ToString
public class R {
    @NotNull
    private String bean;
    @NotNull
    private List<Condition> conditions = new ArrayList<>();
    @NotNull
    private Page page = new Page();
    @NotNull
    private String type = CrudConstant.TYPE_SINGLE;

    private Object id;

    private List<Object> ids;
}
