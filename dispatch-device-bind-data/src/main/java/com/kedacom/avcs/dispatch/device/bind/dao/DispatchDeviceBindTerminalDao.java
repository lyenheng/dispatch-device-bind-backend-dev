package com.kedacom.avcs.dispatch.device.bind.dao;

import com.kedacom.avcs.dispatch.device.bind.dto.DeviceBindTerminalDTO;
import com.kedacom.avcs.dispatch.device.bind.dto.DeviceBindUnionDTO;
import com.kedacom.avcs.dispatch.device.bind.entity.AvcsDeviceBindTerminal;
import com.kedacom.avcs.dispatch.device.bind.entity.QAvcsDeviceBindRelation;
import com.kedacom.avcs.dispatch.device.bind.entity.QAvcsDeviceBindTerminal;
import com.kedacom.kidp.base.data.common.repository.BaseRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author liuyue
 * @date 2022/4/14 13:52
 */
@Repository
public interface DispatchDeviceBindTerminalDao extends BaseRepository<AvcsDeviceBindTerminal> {

    void deleteByIdIn(List<Long> ids);

    List<AvcsDeviceBindTerminal> findAllByMemberId(String memberId);

    default QueryResults<DeviceBindTerminalDTO> findDeviceBindTerminal(Pageable pageable){
        QAvcsDeviceBindTerminal qTerminal = QAvcsDeviceBindTerminal.avcsDeviceBindTerminal;
        JPAQuery<DeviceBindTerminalDTO> jpaQuery = new JPAQueryFactory(entityManager())
                .select(Projections.fields(
                        DeviceBindTerminalDTO.class,
                        qTerminal.id,
                        qTerminal.memberId,
                        qTerminal.memberType
                )).from(qTerminal);

        // 按照创建时间倒序
        jpaQuery.orderBy(qTerminal.createdTime.desc());

        jpaQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());

        return jpaQuery.fetchResults();
    }

    default QueryResults<DeviceBindUnionDTO> findAllDeviceBindUnion(List<String> memberIds){
        QAvcsDeviceBindTerminal qTerminal = QAvcsDeviceBindTerminal.avcsDeviceBindTerminal;
        QAvcsDeviceBindRelation qRelation = QAvcsDeviceBindRelation.avcsDeviceBindRelation;

        BooleanBuilder builder = new BooleanBuilder();
        if (memberIds.size() > 0)
            builder.and(qTerminal.memberId.in(memberIds));

        JPAQuery<DeviceBindUnionDTO> jpaQuery = new JPAQueryFactory(entityManager())
                .select(Projections.fields(
                        DeviceBindUnionDTO.class,
                        qTerminal.id,
                        qTerminal.memberId.as("terminalMemberId") ,
                        qTerminal.memberType.as("terminalMemberType"),
                        qRelation.memberId.as("ipcMemberId"),
                        qRelation.memberType.as("ipcMemberType")
                )).from(qTerminal)
                .leftJoin(qRelation).on(qTerminal.id.eq(qRelation.terminalId))
                .where(builder)
                .orderBy(qTerminal.createdTime.desc());

        return jpaQuery.fetchResults();
    }
}
