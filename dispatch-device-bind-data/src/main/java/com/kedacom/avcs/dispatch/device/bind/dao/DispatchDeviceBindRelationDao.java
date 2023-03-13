package com.kedacom.avcs.dispatch.device.bind.dao;

import com.kedacom.avcs.dispatch.device.bind.entity.AvcsDeviceBindRelation;
import com.kedacom.kidp.base.data.common.repository.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuyue
 * @date 2022/4/14 13:52
 */
@Repository
public interface DispatchDeviceBindRelationDao extends BaseRepository<AvcsDeviceBindRelation> {

    void deleteByTerminalIdIn(List<Long> terminalIds);

    void deleteAllByTerminalIdEquals(Long terminalId);

    List<AvcsDeviceBindRelation> findAllByTerminalIdEquals(Long terminalId);

    List<AvcsDeviceBindRelation> findAllByTerminalIdIn(List<Long> terminalIds);

}
