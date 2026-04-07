package com.huawei.nce.website.controller;

import com.huawei.nce.website.dto.ApiResponse;
import com.huawei.nce.website.service.BusinessClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@RestController
@RequestMapping("/rest/api")
@Tag(name = "配置模板管理", description = "配置模板的上传、查询、下载、删除接口")
public class ApiController {

    @Autowired
    private BusinessClient businessClient;

    @Value("${business.url}")
    private String businessUrl;

    @PostMapping("/templates/upload")
    @Operation(summary = "上传配置模板", description = "上传ZIP格式的配置模板文件，系统将自动解压保存")
    public ApiResponse<?> uploadTemplate(
            @Parameter(description = "模板名称") @RequestParam("name") String name,
            @Parameter(description = "ZIP文件") @RequestParam("file") MultipartFile file) {
        try {
            return businessClient.uploadTemplate(name, file);
        } catch (IOException e) {
            return ApiResponse.error("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/templates")
    @Operation(summary = "获取模板列表", description = "获取所有已上传的配置模板列表")
    public ApiResponse<?> getTemplates() {
        try {
            return businessClient.getTemplates();
        } catch (IOException e) {
            return ApiResponse.error("获取列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/templates/{id}/download")
    @Operation(summary = "下载配置模板", description = "根据模板ID下载对应的配置文件")
    public void downloadTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            HttpServletResponse response) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            org.apache.http.client.methods.HttpGet get = new org.apache.http.client.methods.HttpGet(
                businessUrl + "/rest/internal/templates/" + id + "/file");
            
            try (CloseableHttpResponse httpResponse = client.execute(get)) {
                String filename = httpResponse.getFirstHeader("Content-Disposition") != null 
                    ? httpResponse.getFirstHeader("Content-Disposition").getValue().replace("attachment; filename=\"", "").replace("\"", "")
                    : "template.zip";
                
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
                
                try (InputStream is = httpResponse.getEntity().getContent();
                     OutputStream os = response.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DeleteMapping("/templates/{id}")
    @Operation(summary = "删除配置模板", description = "根据模板ID删除对应的配置模板")
    public ApiResponse<?> deleteTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) {
        try {
            return businessClient.deleteTemplate(id);
        } catch (IOException e) {
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/auth/status")
    @Operation(summary = "获取认证状态", description = "获取当前用户的登录状态")
    public ApiResponse<?> getStatus(@RequestAttribute("SPRING_SECURITY_CONTEXTHolder") Object context) {
        return ApiResponse.success(Map.of("authenticated", true));
    }
}
