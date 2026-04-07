package com.huawei.nce.business.controller;

import com.huawei.nce.business.dto.AlarmDTO;
import com.huawei.nce.business.dto.ApiResponse;
import com.huawei.nce.business.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/internal")
@Tag(name = "告警管理", description = "告警的查询、创建、处理接口")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @GetMapping("/alarms")
    @Operation(summary = "获取告警列表", description = "获取所有告警记录")
    public ApiResponse<List<AlarmDTO>> getAlarms() {
        return ApiResponse.success(alarmService.getAllAlarms());
    }

    @GetMapping("/alarms/unhandled")
    @Operation(summary = "获取未处理告警", description = "获取所有未处理的告警")
    public ApiResponse<List<AlarmDTO>> getUnhandledAlarms() {
        return ApiResponse.success(alarmService.getUnhandledAlarms());
    }

    @PostMapping("/alarms")
    @Operation(summary = "创建告警", description = "创建新的告警记录")
    public ApiResponse<AlarmDTO> createAlarm(
            @Parameter(description = "告警标题") @RequestParam("title") String title,
            @Parameter(description = "告警内容") @RequestParam("content") String content,
            @Parameter(description = "告警级别") @RequestParam("level") String level,
            @Parameter(description = "设备名称") @RequestParam("deviceName") String deviceName,
            @Parameter(description = "设备IP") @RequestParam("deviceIp") String deviceIp) {
        return ApiResponse.success(alarmService.createAlarm(title, content, level, deviceName, deviceIp));
    }

    @PostMapping("/alarms/{id}/handle")
    @Operation(summary = "处理告警", description = "标记告警为已处理")
    public ApiResponse<?> handleAlarm(
            @Parameter(description = "告警ID") @PathVariable Long id,
            @Parameter(description = "处理备注") @RequestParam("remark") String remark,
            @Parameter(description = "处理人") @RequestParam("user") String user) {
        if (alarmService.handleAlarm(id, remark, user)) {
            return ApiResponse.success(true);
        }
        return ApiResponse.error("告警不存在");
    }

    @DeleteMapping("/alarms/{id}")
    @Operation(summary = "删除告警", description = "删除指定告警")
    public ApiResponse<?> deleteAlarm(@Parameter(description = "告警ID") @PathVariable Long id) {
        if (alarmService.deleteAlarm(id)) {
            return ApiResponse.success(true);
        }
        return ApiResponse.error("删除失败");
    }
}
