package com.github.jingshouyan.crud.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author jingshouyan
 * 12/3/18 2:56 PM
 */
@Getter
@Setter
@ToString
public class DeleteDTO implements Serializable {
    private static final long serialVersionUID = 6581866096552400706L;
    @NotNull
    private String bean;
    @NotNull
    private String type;

    private Object id;

    private List<?> ids;
}
