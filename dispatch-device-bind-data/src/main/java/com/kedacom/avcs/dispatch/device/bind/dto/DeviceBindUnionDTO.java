package com.kedacom.avcs.dispatch.device.bind.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuyue
 * @date 2022/4/20 19:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceBindUnionDTO {

    private Long id;

    private String terminalMemberId;

    private String terminalMemberType;

    private String ipcMemberId;

    private String ipcMemberType;

}
