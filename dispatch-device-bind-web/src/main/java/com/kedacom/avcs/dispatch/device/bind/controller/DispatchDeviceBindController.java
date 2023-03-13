package com.kedacom.avcs.dispatch.device.bind.controller;

import com.kedacom.avcs.dispatch.device.bind.dto.DeviceBindDTO;
import com.kedacom.avcs.dispatch.device.bind.dto.DeviceInfoDTO;
import com.kedacom.avcs.dispatch.device.bind.service.DispatchDeviceBindService;
import com.kedacom.avcs.dispatch.device.bind.vo.DeviceBindVO;
import com.kedacom.avcs.dispatch.device.bind.vo.DeviceInfoVO;
import com.kedacom.avcs.dispatch.device.bind.vo.SearchVO;
import com.kedacom.ctsp.web.controller.message.ResponseMessage;
import com.kedacom.ctsp.web.entity.page.PagerResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author liuyue
 * @date 2022/4/14 14:02
 */
@RestController
@Slf4j
@RequestMapping("/deviceBind")
@Api( tags= "设备绑定")
public class DispatchDeviceBindController {

    @Autowired
    private DispatchDeviceBindService deviceBindService;

    @ApiOperation(value = "新增设备绑定", notes = "新增设备绑定")
    @PostMapping("/add")
    public ResponseMessage addDeviceBind(@Valid DeviceBindVO deviceBindVO){
        deviceBindService.addDeviceBind(deviceBindVO);
        return ResponseMessage.ok("成功");
    }

    @ApiOperation(value = "查询设备绑定列表", notes = "查询设备绑定列表")
    @PostMapping("/queryList")
    public ResponseMessage<PagerResult<DeviceBindDTO>> queryDeviceBindList(@Valid @RequestBody SearchVO searchVO){
        return ResponseMessage.ok(deviceBindService.queryDeviceBindList(searchVO));
    }

    @ApiOperation(value = "查询单个会议终端绑定的设备", notes = "查询单个会议终端绑定的设备")
    @GetMapping("/query/{id}")
    public ResponseMessage<DeviceBindDTO> queryDeviceBind(@PathVariable Long id){
        return ResponseMessage.ok(deviceBindService.queryDeviceBind(id));
    }

    @ApiOperation(value = "编辑设备绑定", notes = "编辑设备绑定")
    @PostMapping("/update/{id}")
    public ResponseMessage updatedDeviceBind(@RequestBody(value = "id") Long id, @Valid @RequestBody DeviceBindVO deviceBindVO){
        deviceBindService.updateDeviceBind(id, deviceBindVO.getIpcDeviceListVO());
        return ResponseMessage.ok("成功");
    }

    @ApiOperation(value = "删除设备绑定", notes = "删除设备绑定")
    @DeleteMapping("/delete")
    public ResponseMessage deleteDeviceBind(@RequestBody List<Long> ids){
        deviceBindService.deleteDeviceBind(ids);
        return ResponseMessage.ok("成功");
    }

    @ApiOperation(value = "根据会议终端查询绑定的网络摄像机设备", notes = "根据会议终端查询绑定的网络摄像机设备")
    @PostMapping("/queryByTerminal")
    public ResponseMessage<List<DeviceInfoDTO>> queryByTerminal(@all DeviceInfoVO terminal){
        return ResponseMessage.ok(deviceBindService.queryDeviceBindByTerminal(terminal));
    }

}
