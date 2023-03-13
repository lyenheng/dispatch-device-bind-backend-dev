package com.kedacom.avcs.dispatch.device.bind.service;

import com.kedacom.avcs.dispatch.device.bind.dto.DeviceBindDTO;
import com.kedacom.avcs.dispatch.device.bind.dto.DeviceBindResponseDTO;
import com.kedacom.avcs.dispatch.device.bind.dto.DeviceInfoDTO;
import com.kedacom.avcs.dispatch.device.bind.vo.DeviceBindVO;
import com.kedacom.avcs.dispatch.device.bind.vo.DeviceInfoVO;
import com.kedacom.avcs.dispatch.device.bind.vo.SearchVO;
import com.kedacom.ctsp.web.entity.page.PagerResult;

import java.util.List;

/**
 * @author liuyue
 * @date 2022/4/14 14:03
 */
public interface DispatchDeviceBindService {

    /**
     * 新增设备绑定
     * @param deviceBindVO  deviceBindVO
     */
    void addDeviceBind(DeviceBindVO deviceBindVO);

    /**
     * 查询设备绑定列表
     * @param searchVO
     * @return
     */
    PagerResult<DeviceBindDTO> queryDeviceBindList(SearchVO searchVO);

    /**
     * 查询单个会议终端绑定的设备
     * @param id
     * @return
     */
    DeviceBindDTO queryDeviceBind(Long id);

    /**
     * 编辑设备绑定
     * @param terminalId
     * @param ipcDeviceListVO
     */
    void updateDeviceBind(Long terminalId, List<DeviceInfoVO> ipcDeviceListVO);

    /**
     * 删除设备绑定
     * @param terminalIds
     */
    void deleteDeviceBind(List<Long> terminalIds);

    /**
     * 通过会议终端设备id和type查询绑定的设备
     * @param terminal
     * @return
     */
    List<DeviceInfoDTO> queryDeviceBindByTerminal(DeviceInfoVO terminal);

}
