package com.example.travelserver.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@CrossOrigin
public class UploadController {

    // 修改为你指定的路径，并在末尾加上斜杠
    private final String uploadPath = "D:/bishe/travel-app/uploads/";

    @PostMapping("/image")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return "";

        // 1. 确保目录存在
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs(); // 如果文件夹不存在则自动创建
        }

        // 2. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;

        // 3. 保存文件到硬盘
        File dest = new File(uploadPath + fileName);
        try {
            file.transferTo(dest);
            // 返回给前端相对路径，例如：/uploads/abc-123.jpg
            return "/uploads/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}