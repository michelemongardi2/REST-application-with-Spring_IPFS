package com.example.ipfsdemo;

import org.springframework.web.multipart.MultipartFile;

public interface FileServiceImpl {

    String saveFile(MultipartFile file);


    byte[] loadFile(String hash);

}
