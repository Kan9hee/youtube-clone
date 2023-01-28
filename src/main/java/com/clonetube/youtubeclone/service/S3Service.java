package com.clonetube.youtubeclone.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class S3Service implements FileService{

    @Override
    public String uploadFile(MultipartFile file){
        //Azure Blob에 파일 업로드

        //키 준비
        var filenameExtension=StringUtils.getFilenameExtension(file.getOriginalFilename());

        var key= UUID.randomUUID().toString()+filenameExtension;

    }
}
