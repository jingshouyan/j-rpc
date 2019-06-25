package com.github.jingshouyan.crud.bean;

import com.github.jingshouyan.crud.constant.CrudConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author jingshouyan
 * 12/3/18 2:39 PM
 */
@Getter
@Setter
@ToString
public class CreateDTO implements CrudConstant {
    @NotNull
    private String bean;
    @NotNull
    private String type = TYPE_SINGLE;
    @NotNull
    private String data;
}
