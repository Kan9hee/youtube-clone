package com.clonetube.youtubeclone.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BlobService implements FileService{
    private final BlobContainerClient containerClient=new BlobContainerClientBuilder()
            .connectionString("")
            .containerName("videodata")
            .buildClient();

    private BlobClient blobClient;

    @Override
    public String uploadFile(MultipartFile file){
        blobClient=containerClient.getBlobClient(file.getOriginalFilename());

        try {
            blobClient.upload(file.getInputStream(), file.getSize(), true);
        }catch(IOException ioException){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "An Exception occured while uploading the file");
        }

        return blobClient.getBlobUrl();
    }
}
