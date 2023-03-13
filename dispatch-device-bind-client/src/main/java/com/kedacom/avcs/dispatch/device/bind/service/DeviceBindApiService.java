package com.kedacom.avcs.dispatch.device.bind.service;

import com.kedacom.avcs.dispatch.device.bind.dto.DeviceBindResponseDTO;
import com.kedacom.avcs.dispatch.device.bind.vo.DeviceInfoVO;

import java.util.List;

/**
 * @author liuyue
 * @date 2022/4/18 11:40
 */
public interface DeviceBindApiService {

    /**
     * 根据会议终端查询绑定设备
     * @param terminals
     * @return
     */
    List<DeviceBindResponseDTO> getDeviceBind(List<DeviceInfoVO> terminals);
}
