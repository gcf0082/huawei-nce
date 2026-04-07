package com.huawei.nce.business.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm")
@Data
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String content;
    
    @Column(nullable = false)
    private String level;
    
    @Column(name = "device_name")
    private String deviceName;
    
    @Column(name = "device_ip")
    private String deviceIp;
    
    @Column(name = "alarm_time")
    private LocalDateTime alarmTime;
    
    @Column(name = "is_handled")
    private Boolean handled = false;
    
    @Column(name = "handle_remark")
    private String handleRemark;
    
    @Column(name = "handle_time")
    private LocalDateTime handleTime;
    
    @Column(name = "handle_user")
    private String handleUser;
}
