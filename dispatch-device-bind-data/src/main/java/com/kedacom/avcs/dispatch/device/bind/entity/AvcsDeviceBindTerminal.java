package com.kedacom.avcs.dispatch.device.bind.entity;

import com.kedacom.kidp.base.data.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * @author liuyue
 * @date 2022/4/14 10:32
 */
@Entity
@Data
@Table(name = "avcs_device_bind_terminal")
@EntityListeners(AuditingEntityListener.class)
public class AvcsDeviceBindTerminal extends BaseEntity {

    @Basic
    private String memberId;

    @Basic
    private String memberType;
}
