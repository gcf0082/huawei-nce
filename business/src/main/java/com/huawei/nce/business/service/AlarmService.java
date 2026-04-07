package com.huawei.nce.business.service;

import com.huawei.nce.business.dto.AlarmDTO;
import com.huawei.nce.business.model.Alarm;
import com.huawei.nce.business.repository.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlarmService {

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
        Alarm alarm = new Alarm();
        alarm.setTitle(title);
        alarm.setContent(content);
        alarm.setLevel(level);
        alarm.setDeviceName(deviceName);
        alarm.setDeviceIp(deviceIp);
        alarm.setAlarmTime(LocalDateTime.now());
        alarm.setHandled(false);
        
        alarm = repository.save(alarm);
        return toDTO(alarm);
    }

    public boolean handleAlarm(Long id, String handleRemark, String handleUser) {
        return repository.findById(id).map(alarm -> {
            alarm.setHandled(true);
            alarm.setHandleRemark(handleRemark);
            alarm.setHandleUser(handleUser);
            alarm.setHandleTime(LocalDateTime.now());
            repository.save(alarm);
            return true;
        }).orElse(false);
    }

    public boolean deleteAlarm(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
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
