package com.huawei.nce.business.service;

import com.huawei.nce.business.dto.TemplateSummary;
import com.huawei.nce.business.model.ConfigTemplate;
import com.huawei.nce.business.repository.ConfigTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class TemplateService {

    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    private static final String UPLOAD_DIR = "./upload/templates";

    @Autowired
    private ConfigTemplateRepository repository;

    public TemplateService() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("创建上传目录失败", e);
        }
    }

    public void checkNameDuplicate(String name) {
        if (repository.existsByName(name)) {
            throw new RuntimeException("模板名称已存在");
        }
    }

    public ConfigTemplate saveTemplate(String name, MultipartFile file) throws IOException {
        log.info("上传模板: name={}, file={}, size={}", name, file.getOriginalFilename(), file.getSize());
        if (repository.existsByName(name)) {
            log.error("模板名称已存在: name={}", name);
            throw new RuntimeException("模板名称已存在");
        }
        
        String baseDir = UPLOAD_DIR + "/" + System.currentTimeMillis();
        
        ConfigTemplate template = new ConfigTemplate();
        template.setName(name);
        template.setOriginalFilename(file.getOriginalFilename());
        template.setFileSize(file.getSize());
        template.setUploadTime(LocalDateTime.now());
        template.setStoragePath(baseDir);

        template = repository.save(template);
        Long id = template.getId();
        
        String finalDir = UPLOAD_DIR + "/" + id;
        template.setStoragePath(finalDir);
        template = repository.save(template);
        
        Files.createDirectories(Paths.get(finalDir));

        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(file.getInputStream()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    Path entryPath = Paths.get(finalDir, entry.getName());
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath);
                }
                zis.closeEntry();
            }
        }

        log.info("模板上传成功: id={}, name={}", id, name);
        return template;
    }

    public List<TemplateSummary> getAllTemplates() {
        return repository.findAll().stream()
                .map(t -> new TemplateSummary(t.getId(), t.getName()))
                .collect(Collectors.toList());
    }

    public Optional<ConfigTemplate> getTemplate(Long id) {
        return repository.findById(id);
    }

    public Path getTemplateFilePath(Long id) {
        Optional<ConfigTemplate> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return null;
        }
        return Paths.get(opt.get().getStoragePath());
    }

    public Path getTemplateZipPath(Long id) {
        Optional<ConfigTemplate> opt = repository.findById(id);
        if (opt.isEmpty()) {
            return null;
        }
        String storagePath = opt.get().getStoragePath();
        Path zipPath = Paths.get(storagePath + ".zip");
        
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Path dir = Paths.get(storagePath);
            if (!Files.exists(dir)) {
                return null;
            }
            
            Files.walk(dir)
                .filter(p -> Files.isRegularFile(p))
                .forEach(file -> {
                    try {
                        Path relativePath = dir.relativize(file);
                        ZipEntry entry = new ZipEntry(relativePath.toString().replace('\\', '/'));
                        zos.putNextEntry(entry);
                        Files.copy(file, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            
            return zipPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteTemplate(Long id) {
        Optional<ConfigTemplate> opt = repository.findById(id);
        if (opt.isEmpty()) {
            log.warn("删除模板失败: 模板不存在, id={}", id);
            return false;
        }
        
        ConfigTemplate template = opt.get();
        log.info("删除模板: id={}, name={}", id, template.getName());
        try {
            Path dir = Paths.get(template.getStoragePath());
            if (Files.exists(dir)) {
                Files.walk(dir)
                     .sorted(Comparator.reverseOrder())
                     .forEach(p -> {
                         try { Files.delete(p); } catch (IOException ignored) {}
                     });
            }
            Path zipPath = Paths.get(template.getStoragePath() + ".zip");
            if (Files.exists(zipPath)) {
                Files.delete(zipPath);
            }
        } catch (IOException e) {
            log.error("删除模板文件失败: id={}, error={}", id, e.getMessage());
        }
        
        repository.deleteById(id);
        log.info("模板删除成功: id={}", id);
        return true;
    }
}
