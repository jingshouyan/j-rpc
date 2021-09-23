package com.github.jingshouyan.crud.bean;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jingshouyan
 * 12/3/18 2:39 PM
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDTO implements Serializable {
    private static final long serialVersionUID = -630829069141549723L;
    @NotNull
    private String bean;
    @NotNull
    private String type;
    @NotNull
    private String data;
}
