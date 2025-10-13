package com.cn.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public enum RoleEnum {

    ADMIN("ADMIN"),

    USER("USER");

    private String desc;
}
