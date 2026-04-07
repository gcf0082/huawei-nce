package com.huawei.nce.business.controller;

import com.huawei.nce.business.dto.ApiResponse;
import com.huawei.nce.business.dto.DeviceDTO;
import com.huawei.nce.business.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/internal")
@Tag(name = "网络监控", description = "设备的查询、添加、修改、删除接口")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/devices")
    @Operation(summary = "获取设备列表", description = "获取所有设备")
    public ApiResponse<List<DeviceDTO>> getDevices() {
        return ApiResponse.success(deviceService.getAllDevices());
    }

    @PostMapping("/devices")
    @Operation(summary = "添加设备", description = "添加新设备")
    public ApiResponse<DeviceDTO> createDevice(
            @Parameter(description = "设备名称") @RequestParam("name") String name,
            @Parameter(description = "设备IP") @RequestParam("ip") String ip,
            @Parameter(description = "设备类型") @RequestParam("deviceType") String deviceType,
            @Parameter(description = "位置") @RequestParam("location") String location,
            @Parameter(description = "备注") @RequestParam("remark") String remark) {
        return ApiResponse.success(deviceService.createDevice(name, ip, deviceType, location, remark));
    }

    @PutMapping("/devices/{id}")
    @Operation(summary = "更新设备", description = "更新设备信息")
    public ApiResponse<?> updateDevice(
            @Parameter(description = "设备ID") @PathVariable Long id,
            @Parameter(description = "设备名称") @RequestParam("name") String name,
            @Parameter(description = "设备IP") @RequestParam("ip") String ip,
            @Parameter(description = "设备类型") @RequestParam("deviceType") String deviceType,
            @Parameter(description = "位置") @RequestParam("location") String location,
            @Parameter(description = "备注") @RequestParam("remark") String remark) {
        if (deviceService.updateDevice(id, name, ip, deviceType, location, remark)) {
            return ApiResponse.success(true);
        }
        return ApiResponse.error("设备不存在");
    }

    @PutMapping("/devices/{id}/status")
    @Operation(summary = "更新设备状态", description = "更新设备在线状态")
    public ApiResponse<?> updateStatus(
            @Parameter(description = "设备ID") @PathVariable Long id,
            @Parameter(description = "是否在线") @RequestParam("online") boolean online) {
        if (deviceService.updateStatus(id, online)) {
            return ApiResponse.success(true);
        }
        return ApiResponse.error("设备不存在");
    }

    @DeleteMapping("/devices/{id}")
    @Operation(summary = "删除设备", description = "删除指定设备")
    public ApiResponse<?> deleteDevice(@Parameter(description = "设备ID") @PathVariable Long id) {
        if (deviceService.deleteDevice(id)) {
            return ApiResponse.success(true);
        }
        return ApiResponse.error("删除失败");
    }
}
