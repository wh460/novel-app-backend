package com.cafuc.novelapp.controller;

import com.cafuc.novelapp.result.Result;
import com.cafuc.novelapp.result.ResultCode;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${upload.avatar-path}")
    private String avatarPath;

    @PostConstruct
    public void init() {
        File dir = new File(avatarPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestBody Map<String, String> body) {
        String image = body.get("image");
        String ext = body.get("ext");
        if (image == null || image.isEmpty()) {
            return Result.fail(ResultCode.PARAM_ERROR);
        }
        if (ext == null || ext.isEmpty()) {
            ext = ".jpg";
        }
        String newFileName = generateFileName(ext);
        File dest = new File(avatarPath, newFileName);
        try {
            String normalized = image.replace('-', '+').replace('_', '/');
            log.info("收到base64, 长度={}, 前50字符={}", normalized.length(), normalized.substring(0, Math.min(50, normalized.length())));
            String clean = normalized.replaceAll("[^A-Za-z0-9+/=]", "");
            int pad = clean.length() % 4;
            if (pad != 0) {
                StringBuilder sb = new StringBuilder(clean);
                for (int i = pad; i < 4; i++) {
                    sb.append('=');
                }
                clean = sb.toString();
            }
            byte[] data = Base64.getDecoder().decode(clean);
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(data);
            }
            log.info("头像上传成功: {}", newFileName);
            return Result.success(newFileName);
        } catch (IllegalArgumentException e) {
            log.error("Base64解码失败", e);
            return Result.fail(ResultCode.PARAM_ERROR);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.fail(ResultCode.FAIL);
        }
    }

    private String generateFileName(String ext) {
        String name;
        File dir = new File(avatarPath);
        do {
            int num = (int) (Math.random() * 100000000);
            name = String.format("%08d", num) + ext;
        } while (new File(dir, name).exists());
        return name;
    }
}