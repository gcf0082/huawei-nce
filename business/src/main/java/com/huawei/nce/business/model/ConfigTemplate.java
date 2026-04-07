package com.huawei.nce.business.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "config_template")
@Data
public class ConfigTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "original_filename")
    private String originalFilename;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "upload_time")
    private LocalDateTime uploadTime;
    
    @Column(name = "storage_path")
    private String storagePath;
}
