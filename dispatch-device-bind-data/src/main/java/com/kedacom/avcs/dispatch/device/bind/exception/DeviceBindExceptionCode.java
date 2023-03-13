package com.kedacom.avcs.dispatch.device.bind.exception;

import com.kedacom.dispatch.common.data.exception.CodeMessage;

/**
 * @author liuyue
 * @date 2022/4/14 13:55
 */
public enum  DeviceBindExceptionCode implements CodeMessage {

    EDB001("EDB001","会议终端不能为空"),
    EDB002("EDB002", "至少要绑定一个网络摄像机"),
    EDB003("EDB003", "会议终端设备D{device}类型不正确"),
    EDB004("EDB004", "网络摄像机设备D{device}类型不正确")
    ;

    private String code;

    private String desc;

    DeviceBindExceptionCode(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return desc;
    }
}
