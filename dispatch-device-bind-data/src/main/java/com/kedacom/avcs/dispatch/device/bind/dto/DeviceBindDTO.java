package com.kedacom.avcs.dispatch.device.bind.dto;

import lombok.Data;

import java.util.List;

/**
 * @author liuyue
 * @date 2022/4/14 16:30
 */
@Data
public class DeviceBindDTO {

    private Long id;

    private DeviceInfoDTO terminalDeviceVO;

    private List<DeviceInfoDTO> ipcDeviceList;
}
