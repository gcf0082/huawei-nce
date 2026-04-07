package com.huawei.nce.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {
    private Long id;
    private String name;
    private String ip;
    private String deviceType;
    private Boolean online;
    private String lastUpdate;
    private String location;
    private String remark;
}
