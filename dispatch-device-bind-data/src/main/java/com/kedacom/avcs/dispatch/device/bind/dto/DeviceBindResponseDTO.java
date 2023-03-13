package com.kedacom.avcs.dispatch.device.bind.dto;

import lombok.Data;

import java.util.List;

/**
 * @author liuyue
 * @date 2022/4/19 15:59
 */
@Data
public class DeviceBindResponseDTO {

    private String memberId;

    private String memberType;

    /**
     * 绑定的设备
     */
    private List<DeviceInfoDTO> bindDevices;
}
