package com.kedacom.avcs.dispatch.device.bind.dto;

import com.kedacom.avcs.dispatch.device.bind.entity.AvcsDeviceBindRelation;
import com.kedacom.avcs.dispatch.device.bind.entity.AvcsDeviceBindTerminal;
import com.kedacom.dispatch.common.data.dto.mix.MixDeviceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuyue
 * @date 2022/4/14 16:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfoDTO {

    private String memberId;

    private String memberType;

    /**
     * 设备名称
     */
    private String memberName;

    /**
     * 国标id
     */
    private String gbId;

    public DeviceInfoDTO(MixDeviceDTO mixDeviceDTO){
        this.memberId = mixDeviceDTO.getMemberId();
        this.gbId = mixDeviceDTO.getGbid();
        this.memberName = mixDeviceDTO.getMemberName();
        this.memberType = mixDeviceDTO.getMemberType();
    }

    public DeviceInfoDTO(AvcsDeviceBindRelation deviceBind){
        this.memberId = deviceBind.getMemberId();
        this.memberType = deviceBind.getMemberType();
    }

    public DeviceInfoDTO(AvcsDeviceBindTerminal terminal){
        this.memberId = terminal.getMemberId();
        this.memberType = terminal.getMemberType();
    }

    public DeviceInfoDTO(DeviceBindTerminalDTO terminalDTO){
        this.memberId = terminalDTO.getMemberId();
        this.memberType = terminalDTO.getMemberType();
    }

    public DeviceInfoDTO(String memberId, String memberType){
        this.memberId = memberId;
        this.memberType = memberType;
    }

}
