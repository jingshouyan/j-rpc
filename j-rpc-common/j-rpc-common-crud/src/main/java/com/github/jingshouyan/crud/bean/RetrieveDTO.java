package com.github.jingshouyan.crud.bean;

import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author jingshouyan
 * 12/3/18 2:56 PM
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveDTO implements Serializable {
    private static final long serialVersionUID = -5178882693003813578L;
    @NotNull
    private String bean;
    private List<Condition> conditions;
    private Page<?> page;
    @NotNull
    private String type;

    private Object id;

    private Collection<?> ids;

    private List<String> fields;
}
