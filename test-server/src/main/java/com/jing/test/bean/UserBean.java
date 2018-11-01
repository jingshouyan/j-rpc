package com.jing.test.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jingshouyan
 * @date 2018/4/19 10:22
 */
@Getter
@Setter
@ToString(callSuper = true)
public class UserBean {
    private String id;
    private String username;
    private String nickname;
    private String icon;
    @JsonIgnore
    private String pwHash;
    private Integer userType;
}
