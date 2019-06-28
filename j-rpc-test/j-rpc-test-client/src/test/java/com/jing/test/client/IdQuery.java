package com.jing.test.client;

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

    @NotNull
    @Size(min = 4, max = 20)
    private String name;

    @Min(5)
    @Max(99)
    private int age = 10;

    @NotNull
    @Size(min = 1, max = 100)
    private List<String> ids;
}
