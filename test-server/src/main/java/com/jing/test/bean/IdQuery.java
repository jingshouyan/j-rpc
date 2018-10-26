package com.jing.test.bean;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2018/10/26 15:46
 */
@Data
public class IdQuery {

    @NotNull@Size(min = 1,max= 100)
    private List<String> ids;
}
