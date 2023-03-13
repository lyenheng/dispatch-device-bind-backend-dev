package com.kedacom.avcs.dispatch.device.bind.service.impl;

import com.kedacom.avcs.dispatch.device.bind.dao.DispatchDeviceBindTerminalDao;
import com.kedacom.avcs.dispatch.device.bind.dto.DeviceBindResponseDTO;
import com.kedacom.avcs.dispatch.device.bind.dto.DeviceBindUnionDTO;
import com.kedacom.avcs.dispatch.device.bind.dto.DeviceInfoDTO;
import com.kedacom.avcs.dispatch.device.bind.service.DeviceBindApiService;
import com.kedacom.avcs.dispatch.device.bind.vo.DeviceInfoVO;
import com.kedacom.dispatch.common.data.dto.mix.IdTypeQueryDTO;
import com.kedacom.dispatch.common.data.dto.mix.MixDeviceDTO;
import com.kedacom.dispatch.common.data.dto.mix.MixResultDTO;
import com.kedacom.dispatch.common.mix.service.AvcsMixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author liuyue
 * @date 2022/4/18 14:02
 */
@Service
public class DeviceBindApiServiceImpl implements DeviceBindApiService {

    @Autowired
    private DispatchDeviceBindTerminalDao terminalDao;

    @Autowired
    AvcsMixService avcsMixService;

    public List<DeviceBindResponseDTO> getDeviceBind(List<DeviceInfoVO> terminals) {

        if (terminals.size() < 1){
            return null;
        }

        List<DeviceBindResponseDTO> deviceBindResponseDTOS = new ArrayList<>();

        // 所有的会议终端memberId
        List<String> terminalMemberIds = terminals.stream().map(DeviceInfoVO::getMemberId).collect(Collectors.toList());

        List<DeviceBindUnionDTO> results = terminalDao.findAllDeviceBindUnion(terminalMemberIds).getResults();

        // 查询网络摄像机名称
        List<IdTypeQueryDTO> idTypeQueryDTOS = results.stream().map(deviceBindUnionDTO -> {
            IdTypeQueryDTO idTypeQueryDTO = new IdTypeQueryDTO();
            idTypeQueryDTO.setMemberId(deviceBindUnionDTO.getIpcMemberId());
            idTypeQueryDTO.setMemberType(deviceBindUnionDTO.getIpcMemberType());
            return idTypeQueryDTO;
        }).collect(Collectors.toList());

        // 保存memberId和MixDeviceDTO的映射关系
        Map<String, MixDeviceDTO> memberIdToMixDevice = new HashMap<>();

        if (idTypeQueryDTOS.size() > 0){
            MixResultDTO mixResultDTO = avcsMixService.queryNodeByIdType(idTypeQueryDTOS);
            if (mixResultDTO != null && mixResultDTO.getDevices().size() > 0){
                for (MixDeviceDTO mixDeviceDTO : mixResultDTO.getDevices()) {
                    memberIdToMixDevice.put(mixDeviceDTO.getMemberId(), mixDeviceDTO);
                }
            }
        }

        Map<String, List<DeviceBindUnionDTO>> terminalToDeviceBind = results.stream().collect(Collectors.groupingBy(DeviceBindUnionDTO::getTerminalMemberId));

        terminalToDeviceBind.forEach((terminalMemberId, deviceBindUnionDTOS) -> {

            List<DeviceInfoDTO> ipcDeviceList = deviceBindUnionDTOS.stream().map(deviceBindUnionDTO ->
                new DeviceInfoDTO(deviceBindUnionDTO.getIpcMemberId(),
                        deviceBindUnionDTO.getIpcMemberType(),
                        memberIdToMixDevice.get(deviceBindUnionDTO.getIpcMemberId()).getMemberName(),
                        memberIdToMixDevice.get(deviceBindUnionDTO.getIpcMemberId()).getGbid()))
                    .collect(Collectors.toList());

            DeviceBindResponseDTO deviceBindResponseDTO = new DeviceBindResponseDTO();
            deviceBindResponseDTO.setMemberId(terminalMemberId);
            deviceBindResponseDTO.setMemberType(deviceBindUnionDTOS.get(0).getTerminalMemberType());
            deviceBindResponseDTO.setBindDevices(ipcDeviceList);

            deviceBindResponseDTOS.add(deviceBindResponseDTO);

        });

        return deviceBindResponseDTOS;
    }

}
