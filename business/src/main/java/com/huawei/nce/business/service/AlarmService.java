package com.huawei.nce.business.service;

import com.huawei.nce.business.dto.AlarmDTO;
import com.huawei.nce.business.model.Alarm;
import com.huawei.nce.business.repository.AlarmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlarmService {

    private static final Logger log = LoggerFactory.getLogger(AlarmService.class);

    @Autowired
    private AlarmRepository repository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<AlarmDTO> getAllAlarms() {
        return repository.findByOrderByAlarmTimeDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AlarmDTO> getUnhandledAlarms() {
        return repository.findByHandledFalseOrderByAlarmTimeDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AlarmDTO createAlarm(String title, String content, String level, String deviceName, String deviceIp) {
        log.info("创建告警: title={}, level={}, deviceName={}, deviceIp={}", title, level, deviceName, deviceIp);
        Alarm alarm = new Alarm();
        alarm.setTitle(title);
        alarm.setContent(content);
        alarm.setLevel(level);
        alarm.setDeviceName(deviceName);
        alarm.setDeviceIp(deviceIp);
        alarm.setAlarmTime(LocalDateTime.now());
        alarm.setHandled(false);
        
        alarm = repository.save(alarm);
        log.info("告警创建成功: id={}, title={}", alarm.getId(), alarm.getTitle());
        return toDTO(alarm);
    }

    public boolean handleAlarm(Long id, String handleRemark, String handleUser) {
        return repository.findById(id).map(alarm -> {
            log.info("处理告警: id={}, handleUser={}", id, handleUser);
            alarm.setHandled(true);
            alarm.setHandleRemark(handleRemark);
            alarm.setHandleUser(handleUser);
            alarm.setHandleTime(LocalDateTime.now());
            repository.save(alarm);
            log.info("告警处理成功: id={}", id);
            return true;
        }).orElseGet(() -> {
            log.warn("处理告警失败: 告警不存在, id={}", id);
            return false;
        });
    }

    public boolean deleteAlarm(Long id) {
        if (repository.existsById(id)) {
            log.info("删除告警: id={}", id);
            repository.deleteById(id);
            log.info("告警删除成功: id={}", id);
            return true;
        }
        log.warn("删除告警失败: 告警不存在, id={}", id);
        return false;
    }

    private AlarmDTO toDTO(Alarm alarm) {
        return new AlarmDTO(
            alarm.getId(),
            alarm.getTitle(),
            alarm.getContent(),
            alarm.getLevel(),
            alarm.getDeviceName(),
            alarm.getDeviceIp(),
            alarm.getAlarmTime() != null ? alarm.getAlarmTime().format(formatter) : null,
            alarm.getHandled(),
            alarm.getHandleRemark(),
            alarm.getHandleTime() != null ? alarm.getHandleTime().format(formatter) : null,
            alarm.getHandleUser()
        );
    }
}
