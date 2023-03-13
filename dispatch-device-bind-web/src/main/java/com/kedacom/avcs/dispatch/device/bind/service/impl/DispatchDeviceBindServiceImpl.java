package com.kedacom.avcs.dispatch.device.bind.service.impl;

import com.kedacom.avcs.dispatch.device.bind.dao.DispatchDeviceBindRelationDao;
import com.kedacom.avcs.dispatch.device.bind.dao.DispatchDeviceBindTerminalDao;
import com.kedacom.avcs.dispatch.device.bind.dto.*;
import com.kedacom.avcs.dispatch.device.bind.entity.AvcsDeviceBindRelation;
import com.kedacom.avcs.dispatch.device.bind.entity.AvcsDeviceBindTerminal;
import com.kedacom.avcs.dispatch.device.bind.exception.DeviceBindExceptionCode;
import com.kedacom.avcs.dispatch.device.bind.service.DeviceBindApiService;
import com.kedacom.avcs.dispatch.device.bind.service.DispatchDeviceBindService;
import com.kedacom.avcs.dispatch.device.bind.vo.DeviceBindVO;
import com.kedacom.avcs.dispatch.device.bind.vo.DeviceInfoVO;
import com.kedacom.avcs.dispatch.device.bind.vo.SearchVO;
import com.kedacom.ctsp.web.entity.page.PagerResult;
import com.kedacom.dispatch.common.data.dto.mix.IdTypeQueryDTO;
import com.kedacom.dispatch.common.data.dto.mix.MixDeviceDTO;
import com.kedacom.dispatch.common.data.dto.mix.MixResultDTO;
import com.kedacom.dispatch.common.data.exception.CommonException;
import com.kedacom.dispatch.common.data.exception.DispatchMessage;
import com.kedacom.dispatch.common.data.vo.CommonDeviceVO;
import com.kedacom.dispatch.common.mix.service.AvcsMixService;
import com.querydsl.core.QueryResults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liuyue
 * @date 2022/4/14 14:22
 */
@Slf4j
@Service
public class DispatchDeviceBindServiceImpl implements DispatchDeviceBindService {

    @Autowired
    private DispatchDeviceBindTerminalDao terminalDao;

    @Autowired
    private DispatchDeviceBindRelationDao bindRelationDao;

    @Autowired
    private AvcsMixService avcsMixService;

    @Autowired
    private DeviceBindApiService deviceBindApiService;

    @Override
    @Transactional
    public void addDeviceBind(DeviceBindVO deviceBindVO) {

        validateDeviceBind(deviceBindVO);

        // 会议终端数据
        DeviceInfoVO terminalDeviceInfoVO = deviceBindVO.getTerminalDeviceInfoVO();
        // IPC设备数据
        List<DeviceInfoVO> ipcDeviceList = deviceBindVO.getIpcDeviceListVO();

        List<AvcsDeviceBindTerminal> allByMemberId = terminalDao.findAllByMemberId(terminalDeviceInfoVO.getMemberId());
        // 如果该会议终端已经存在，则重新绑定
        if (allByMemberId.size() > 0){
            // 删除已绑定的数据
            bindRelationDao.deleteAllByTerminalIdEquals(allByMemberId.get(0).getId());
            // 重新绑定
            saveDeviceBindRelation(allByMemberId.get(0).getId(), ipcDeviceList);
        }else {
            // 保存会议终端数据
            AvcsDeviceBindTerminal terminal = new AvcsDeviceBindTerminal();
            terminal.setMemberId(terminalDeviceInfoVO.getMemberId());
            terminal.setMemberType(terminalDeviceInfoVO.getMemberType());
            terminal = terminalDao.save(terminal);

            // 保存会议终端和网络摄像机的绑定关系数据
            saveDeviceBindRelation(terminal.getId(), ipcDeviceList);
        }
    }


    @Override
    public DeviceBindDTO queryDeviceBind(Long id) {

        AvcsDeviceBindTerminal terminal = terminalDao.findOne(id);

        List<AvcsDeviceBindRelation> deviceBindRelations = bindRelationDao.findAllByTerminalIdEquals(id);

        DeviceBindDTO deviceBindDTO = new DeviceBindDTO();

        List<IdTypeQueryDTO> idTypeQueryDTOS = new ArrayList<>();
        if (terminal != null && deviceBindRelations.size() > 0){

            // 封装会议终端请求数据
            IdTypeQueryDTO terminalIdType = new IdTypeQueryDTO();
            terminalIdType.setMemberId(terminal.getMemberId());
            terminalIdType.setMemberType(terminal.getMemberType());
            idTypeQueryDTOS.add(terminalIdType);

            // 封装网络摄像机请求数据
            for (AvcsDeviceBindRelation deviceBindRelation : deviceBindRelations) {
                IdTypeQueryDTO idTypeQueryDTO = new IdTypeQueryDTO();
                idTypeQueryDTO.setMemberId(deviceBindRelation.getMemberId());
                idTypeQueryDTO.setMemberType(deviceBindRelation.getMemberType());
                idTypeQueryDTOS.add(idTypeQueryDTO);
            }

            // 保存memberId和MixDeviceDTO的映射关系
            HashMap<String, MixDeviceDTO> memberIdToMixDevice = new HashMap<>();

            if (idTypeQueryDTOS.size() > 0){
                MixResultDTO mixResultDTO = avcsMixService.queryNodeByIdType(idTypeQueryDTOS);

                if (mixResultDTO != null && mixResultDTO.getDevices().size() > 0){
                    for (MixDeviceDTO mixDeviceDTO : mixResultDTO.getDevices()) {
                        memberIdToMixDevice.put(mixDeviceDTO.getMemberId(), mixDeviceDTO);
                    }
                }
            }

            // 封装返回的数据
            if (memberIdToMixDevice.size() > 0){
                // 会议终端
                String terminalMemberId = terminal.getMemberId();
                if (memberIdToMixDevice.get(terminalMemberId) != null){
                    DeviceInfoDTO terminalDeviceVO = new DeviceInfoDTO(memberIdToMixDevice.get(terminalMemberId));
                    deviceBindDTO.setTerminalDeviceVO(terminalDeviceVO);
                }

                List<DeviceInfoDTO> ipcDeviceList = new ArrayList<>();
                for (AvcsDeviceBindRelation deviceBindRelation : deviceBindRelations) {
                    if (memberIdToMixDevice.get(deviceBindRelation.getMemberId()) != null){
                        DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO(memberIdToMixDevice.get(deviceBindRelation.getMemberId()));
                        ipcDeviceList.add(deviceInfoDTO);
                    }
                }
                deviceBindDTO.setIpcDeviceList(ipcDeviceList);
            }
        }

        return deviceBindDTO;
    }

    @Override
    public PagerResult<DeviceBindDTO> queryDeviceBindList(SearchVO searchVO) {
        PageRequest pageRequest = PageRequest.of(searchVO.getPageNo(), searchVO.getPageSize());
        QueryResults<DeviceBindTerminalDTO> deviceBindTerminal = terminalDao.findDeviceBindTerminal(pageRequest);

        return convertQueryResults2PageResult(deviceBindTerminal, searchVO.getPageSize(), searchVO.getPageNo());
    }



    private PagerResult<DeviceBindDTO> convertQueryResults2PageResult(QueryResults<DeviceBindTerminalDTO> page, int pageSize, int pageNo){
        long total = page.getTotal();
        long totalPages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        PagerResult<DeviceBindDTO> pagerResult = new PagerResult<>();
        pagerResult.setPageNo(pageNo);
        pagerResult.setPageSize(pageSize);
        pagerResult.setTotal(total);
        pagerResult.setTotalPages(totalPages);

        List<DeviceBindTerminalDTO> deviceBindTerminalDTOS = page.getResults();
        List<DeviceBindDTO> deviceBindData = getDeviceBindData(deviceBindTerminalDTOS);
        pagerResult.setData(deviceBindData);

        return pagerResult;
    }

    /**
     * 根据会议终端数据获取并封装设备绑定数据
     * @param terminalDTOS
     * @return
     */
    List<DeviceBindDTO> getDeviceBindData(List<DeviceBindTerminalDTO> terminalDTOS){

        if (terminalDTOS.size() < 1){
            return null;
        }

        List<DeviceBindDTO> deviceBindDTOS = new ArrayList<>();

        List<IdTypeQueryDTO> idTypeQueryDTOS = new ArrayList<>();

        for (DeviceBindTerminalDTO terminalDTO : terminalDTOS) {
            DeviceBindDTO deviceBindDTO = new DeviceBindDTO();

            Long terminalId = terminalDTO.getId();
            deviceBindDTO.setId(terminalDTO.getId());

            DeviceInfoDTO terminal = new DeviceInfoDTO(terminalDTO);
            deviceBindDTO.setTerminalDeviceVO(terminal);

            IdTypeQueryDTO idTypeQueryDTO = new IdTypeQueryDTO();
            idTypeQueryDTO.setMemberId(terminalDTO.getMemberId());
            idTypeQueryDTO.setMemberType(terminalDTO.getMemberType());
            idTypeQueryDTOS.add(idTypeQueryDTO);

            List<AvcsDeviceBindRelation> deviceBindRelation = bindRelationDao.findAllByTerminalIdEquals(terminalId);
            List<DeviceInfoDTO> ipcList = new ArrayList<>();
            for (AvcsDeviceBindRelation deviceBind : deviceBindRelation) {

                DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
                deviceInfoDTO.setMemberId(deviceBind.getMemberId());
                deviceInfoDTO.setMemberType(deviceBind.getMemberType());
                ipcList.add(deviceInfoDTO);

                IdTypeQueryDTO idTypeQueryDTO1 = new IdTypeQueryDTO();
                idTypeQueryDTO1.setMemberId(deviceBind.getMemberId());
                idTypeQueryDTO1.setMemberType(deviceBind.getMemberType());
                idTypeQueryDTOS.add(idTypeQueryDTO1);
            }

            deviceBindDTO.setIpcDeviceList(ipcList);
            deviceBindDTOS.add(deviceBindDTO);
        }

            MixResultDTO mixResultDTO = avcsMixService.queryNodeByIdType(idTypeQueryDTOS);
            if (mixResultDTO != null && mixResultDTO.getDevices().size() > 0){
                HashMap<String, MixDeviceDTO> memberIdToDevice = new HashMap<>();
                List<MixDeviceDTO> devices = mixResultDTO.getDevices();
                for (MixDeviceDTO device : devices) {
                    memberIdToDevice.put(device.getMemberId(), device);
                }

                for (DeviceBindDTO deviceBindDTO : deviceBindDTOS) {
                    if (memberIdToDevice.get(deviceBindDTO.getTerminalDeviceVO().getMemberId()) != null){
                        MixDeviceDTO terminalMixDeviceDTO = memberIdToDevice.get(deviceBindDTO.getTerminalDeviceVO().getMemberId());
                        deviceBindDTO.getTerminalDeviceVO().setMemberName(terminalMixDeviceDTO.getMemberName());
                        deviceBindDTO.getTerminalDeviceVO().setGbId(terminalMixDeviceDTO.getGbid());
                    }

                    for (DeviceInfoDTO deviceInfoDTO : deviceBindDTO.getIpcDeviceList()) {
                        if (memberIdToDevice.get(deviceInfoDTO.getMemberId()) != null){
                            MixDeviceDTO ipcMixDeviceDTO = memberIdToDevice.get(deviceInfoDTO.getMemberId());
                            deviceInfoDTO.setMemberName(ipcMixDeviceDTO.getMemberName());
                            deviceInfoDTO.setGbId(ipcMixDeviceDTO.getGbid());
                        }
                    }
                }
            }

        return deviceBindDTOS;
    }

    @Override
    @Transactional
    public void updateDeviceBind(Long terminalId, List<DeviceInfoVO> ipcDeviceListVO) {

        validateIPCDeviceList(ipcDeviceListVO);
        // 删除已绑定的数据
        bindRelationDao.deleteAllByTerminalIdEquals(terminalId);
        // 重新绑定
        saveDeviceBindRelation(terminalId, ipcDeviceListVO);
    }


    @Override
    @Transactional
    public void deleteDeviceBind(List<Long> terminalIds) {
        terminalDao.deleteByIdIn(terminalIds);
        bindRelationDao.deleteByTerminalIdIn(terminalIds);
    }

    @Override
    public List<DeviceInfoDTO> queryDeviceBindByTerminal(DeviceInfoVO terminal) {

        List<DeviceInfoVO> deviceInfoVOS = new ArrayList<>();
        deviceInfoVOS.add(terminal);
        List<DeviceBindResponseDTO> deviceBind = deviceBindApiService.getDeviceBind(deviceInfoVOS);
        if (deviceBind != null && deviceBind.size() > 0){
            return deviceBind.get(0).getBindDevices();
        }
        return null;
    }

    /**
     * 校验数据
     * @param deviceBindVO
     */
    private void validateDeviceBind(DeviceBindVO deviceBindVO){
        if (deviceBindVO.getTerminalDeviceInfoVO() == null || StringUtils.isEmpty(deviceBindVO.getTerminalDeviceInfoVO().getMemberId()) || StringUtils.isEmpty(deviceBindVO.getTerminalDeviceInfoVO().getMemberType())){
            throw CommonException.error(DispatchMessage.message(DeviceBindExceptionCode.EDB001));
        }

        if (! deviceBindVO.getTerminalDeviceInfoVO().getMemberType().equalsIgnoreCase("device_MT")){
            throw CommonException.error(DispatchMessage.message(DeviceBindExceptionCode.EDB003));
        }

        validateIPCDeviceList(deviceBindVO.getIpcDeviceListVO());
    }

     /**
     * 校验网络摄像机设备
     * @param ipcDeviceListVO
     */
     void validateIPCDeviceList(List<DeviceInfoVO> ipcDeviceListVO){
        if (ipcDeviceListVO.size() < 1){
            throw CommonException.error(DispatchMessage.message(DeviceBindExceptionCode.EDB002));
        }

        List<DispatchMessage> dispatchMessages = new ArrayList<>();
        for (DeviceInfoVO deviceInfoVO : ipcDeviceListVO) {
            if (!deviceInfoVO.getMemberType().equalsIgnoreCase("device_IPC")){
                CommonDeviceVO commonDeviceVO = new CommonDeviceVO();
                commonDeviceVO.setMemberId(deviceInfoVO.getMemberId());
                commonDeviceVO.setMemberType(deviceInfoVO.getMemberType());
                dispatchMessages.add(DispatchMessage.message(DeviceBindExceptionCode.EDB004, commonDeviceVO));
            }
        }

        if (dispatchMessages.size() > 0){
            throw CommonException.error(dispatchMessages);
        }
    }


    /**
     * 保存会议终端和网络摄像机的绑定关系数据
     * @param terminalId
     * @param ipcDeviceList
     */
    private void saveDeviceBindRelation(Long terminalId, List<DeviceInfoVO> ipcDeviceList){
        // 过滤无效 IPC设备
        List<DeviceInfoVO> newIPCDeviceList = ipcDeviceList.stream().filter(deviceInfoVO -> StringUtils.isNotBlank(deviceInfoVO.getMemberId()) && StringUtils.isNotBlank(deviceInfoVO.getMemberType())).collect(Collectors.toList());

        // 保存设备绑定数据
        if (newIPCDeviceList.size() > 0){
            List<AvcsDeviceBindRelation> deviceBindRelationList = newIPCDeviceList.stream().map(deviceInfoVO -> new AvcsDeviceBindRelation(deviceInfoVO,terminalId)).collect(Collectors.toList());
            bindRelationDao.saveAll(deviceBindRelationList);
        }
    }
}
