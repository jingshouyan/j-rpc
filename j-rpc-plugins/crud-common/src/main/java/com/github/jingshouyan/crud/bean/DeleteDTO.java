package com.github.jingshouyan.crud.bean;

import com.github.jingshouyan.crud.constant.CrudConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jingshouyan
 * 12/3/18 2:56 PM
 */
@Getter
@Setter
@ToString
public class DeleteDTO implements CrudConstant{
    @NotNull
    private String bean;
    @NotNull
    private String type = TYPE_SINGLE;

    private Object id;

    private List<?> ids;
}
