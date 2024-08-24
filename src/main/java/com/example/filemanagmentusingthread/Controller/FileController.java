package com.example.filemanagmentusingthread.Controller;


import com.example.filemanagmentusingthread.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

    @Autowired
    FileService service;

    @PostMapping("/upload")
    public String uploadFile(@RequestBody String filePath){
        service.saveDataFromFile(filePath);
        return "File processing started . data will be saved now";
    }
}

