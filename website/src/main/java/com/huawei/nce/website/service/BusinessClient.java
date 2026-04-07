package com.huawei.nce.website.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.nce.website.dto.ApiResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class BusinessClient {

    @Value("${business.url}")
    private String businessUrl;

    private final ObjectMapper mapper = new ObjectMapper();

    public ApiResponse<?> uploadTemplate(String name, MultipartFile file) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(businessUrl + "/rest/internal/templates/upload");
            
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addTextBody("name", name)
                    .addBinaryBody("file", file.getInputStream(), 
                            ContentType.APPLICATION_OCTET_STREAM, 
                            file.getOriginalFilename())
                    .build();
            post.setEntity(entity);

            return execute(client, post);
        }
    }

    public ApiResponse<?> getTemplates() throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(businessUrl + "/rest/internal/templates");
            return execute(client, get);
        }
    }

    public ApiResponse<?> getTemplateFile(Long id) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(businessUrl + "/rest/internal/templates/" + id + "/file");
            return execute(client, get);
        }
    }

    public ApiResponse<?> deleteTemplate(Long id) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            org.apache.http.client.methods.HttpDelete delete = 
                new org.apache.http.client.methods.HttpDelete(businessUrl + "/rest/internal/templates/" + id);
            return execute(client, delete);
        }
    }

    private ApiResponse<?> execute(CloseableHttpClient client, HttpUriRequest request) throws IOException {
        try (CloseableHttpResponse response = client.execute(request)) {
            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return mapper.readValue(body, ApiResponse.class);
        }
    }
}
