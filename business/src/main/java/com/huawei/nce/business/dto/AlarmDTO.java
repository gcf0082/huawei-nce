package com.huawei.nce.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDTO {
    private Long id;
    private String title;
    private String content;
    private String level;
    private String deviceName;
    private String deviceIp;
    private String alarmTime;
    private Boolean handled;
    private String handleRemark;
    private String handleTime;
    private String handleUser;
}
