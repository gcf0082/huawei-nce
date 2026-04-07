package com.huawei.nce.business.service;

import com.huawei.nce.business.dto.DeviceDTO;
import com.huawei.nce.business.model.Device;
import com.huawei.nce.business.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceRepository repository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<DeviceDTO> getAllDevices() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DeviceDTO createDevice(String name, String ip, String deviceType, String location, String remark) {
        log.info("创建设备: name={}, ip={}, deviceType={}, location={}", name, ip, deviceType, location);
        Device device = new Device();
        device.setName(name);
        device.setIp(ip);
        device.setDeviceType(deviceType);
        device.setLocation(location);
        device.setRemark(remark);
        device.setOnline(false);
        device.setLastUpdate(LocalDateTime.now());
        
        device = repository.save(device);
        log.info("设备创建成功: id={}, name={}", device.getId(), device.getName());
        return toDTO(device);
    }

    public boolean updateDevice(Long id, String name, String ip, String deviceType, String location, String remark) {
        return repository.findById(id).map(device -> {
            log.info("更新设备: id={}, name={}, ip={}", id, name, ip);
            device.setName(name);
            device.setIp(ip);
            device.setDeviceType(deviceType);
            device.setLocation(location);
            device.setRemark(remark);
            device.setLastUpdate(LocalDateTime.now());
            repository.save(device);
            log.info("设备更新成功: id={}", id);
            return true;
        }).orElseGet(() -> {
            log.warn("更新设备失败: 设备不存在, id={}", id);
            return false;
        });
    }

    public boolean updateStatus(Long id, boolean online) {
        return repository.findById(id).map(device -> {
            log.info("更新设备状态: id={}, online={}", id, online);
            device.setOnline(online);
            device.setLastUpdate(LocalDateTime.now());
            repository.save(device);
            return true;
        }).orElseGet(() -> {
            log.warn("更新设备状态失败: 设备不存在, id={}", id);
            return false;
        });
    }

    public boolean deleteDevice(Long id) {
        if (repository.existsById(id)) {
            log.info("删除设备: id={}", id);
            repository.deleteById(id);
            log.info("设备删除成功: id={}", id);
            return true;
        }
        log.warn("删除设备失败: 设备不存在, id={}", id);
        return false;
    }

    private DeviceDTO toDTO(Device device) {
        return new DeviceDTO(
            device.getId(),
            device.getName(),
            device.getIp(),
            device.getDeviceType(),
            device.getOnline(),
            device.getLastUpdate() != null ? device.getLastUpdate().format(formatter) : null,
            device.getLocation(),
            device.getRemark()
        );
    }
}
