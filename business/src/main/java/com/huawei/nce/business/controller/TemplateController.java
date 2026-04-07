package com.huawei.nce.business.controller;

import com.huawei.nce.business.dto.ApiResponse;
import com.huawei.nce.business.dto.TemplateSummary;
import com.huawei.nce.business.model.ConfigTemplate;
import com.huawei.nce.business.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipFile;

@RestController
@RequestMapping("/rest/internal")
@Tag(name = "配置模板业务接口", description = "配置模板的内部业务处理接口")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @PostMapping("/templates/upload")
    @Operation(summary = "上传配置模板", description = "接收ZIP文件并解压保存到存储目录")
    public ApiResponse<?> uploadTemplate(
            @Parameter(description = "模板名称") @RequestParam("name") String name,
            @Parameter(description = "ZIP配置文件") @RequestParam("file") MultipartFile file) {
        if (name == null || name.trim().isEmpty()) {
            return ApiResponse.error("模板名称不能为空");
        }
        if (file.isEmpty()) {
            return ApiResponse.error("文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".zip")) {
            return ApiResponse.error("只支持ZIP格式");
        }
        try {
            templateService.saveTemplate(name, file);
            return ApiResponse.success(true);
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        } catch (IOException e) {
            return ApiResponse.error("保存失败: " + e.getMessage());
        }
    }

    @GetMapping("/templates")
    @Operation(summary = "获取模板列表", description = "获取所有已保存的配置模板")
    public ApiResponse<List<TemplateSummary>> getTemplates() {
        return ApiResponse.success(templateService.getAllTemplates());
    }

    @GetMapping("/templates/{id}")
    @Operation(summary = "获取模板详情", description = "根据ID获取配置模板详情")
    public ApiResponse<ConfigTemplate> getTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        return templateService.getTemplate(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("模板不存在"));
    }

    @GetMapping("/templates/{id}/file")
    @Operation(summary = "下载模板文件", description = "根据ID下载配置模板文件")
    public ResponseEntity<Resource> downloadTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) throws IOException {
        Path zipPath = templateService.getTemplateZipPath(id);
        if (zipPath == null || !Files.exists(zipPath)) {
            return ResponseEntity.notFound().build();
        }
        
        ConfigTemplate template = templateService.getTemplate(id).orElse(null);
        String filename = template != null ? template.getOriginalFilename() : "template.zip";
        
        Resource resource = new org.springframework.core.io.FileSystemResource(zipPath.toFile());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/templates/{id}")
    @Operation(summary = "删除模板", description = "根据ID删除配置模板")
    public ApiResponse<?> deleteTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        if (templateService.deleteTemplate(id)) {
            return ApiResponse.success(true);
        }
        return ApiResponse.error("删除失败");
    }
}
