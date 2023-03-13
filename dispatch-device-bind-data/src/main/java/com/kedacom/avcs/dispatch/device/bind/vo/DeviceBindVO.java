package com.kedacom.avcs.dispatch.device.bind.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author liuyue
 * @date 2022/4/14 13:40
 */
@Data
public class DeviceBindVO {

    /**
     * 会议终端
     */
    @ApiModelProperty(value="会议终端",name="terminalDeviceVO")
    private DeviceInfoVO terminalDeviceInfoVO;

    /**
     * 网络摄像机
     */
    @ApiModelProperty(value="网络摄像机",name="IPCDeviceList",required = true)
    private List<DeviceInfoVO> ipcDeviceListVO;
}
