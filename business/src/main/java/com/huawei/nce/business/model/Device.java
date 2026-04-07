package com.huawei.nce.business.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device")
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String ip;
    
    @Column(name = "device_type")
    private String deviceType;
    
    @Column(name = "is_online")
    private Boolean online = false;
    
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    
    private String location;
    
    private String remark;
}
