package com.kedacom.avcs.dispatch.device.bind.entity;

import com.kedacom.avcs.dispatch.device.bind.vo.DeviceInfoVO;
import com.kedacom.kidp.base.data.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * @author liuyue
 * @date 2022/4/14 13:12
 */
@Entity
@Data
@Table(name = "avcs_device_bind_relation")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class AvcsDeviceBindRelation extends BaseEntity {

    @Basic
    private Long terminalId;

    @Basic
    private String memberId;

    @Basic
    private String memberType;

    public AvcsDeviceBindRelation(DeviceInfoVO deviceInfoVO, Long terminalId){
        this.terminalId = terminalId;
        this.memberId = deviceInfoVO.getMemberId();
        this.memberType = deviceInfoVO.getMemberType();
    }
}
