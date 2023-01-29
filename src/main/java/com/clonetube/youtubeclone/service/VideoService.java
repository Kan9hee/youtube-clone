package com.clonetube.youtubeclone.service;

import com.clonetube.youtubeclone.model.Video;
import com.clonetube.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final BlobService blobService;
    private final VideoRepository videoRepository;

    public void uploadVideo(MultipartFile multipartFile){
        //Azure Blob에 파일 업로드 및 데이터베이스에 비디오 데이터 저장
        String videoUrl=blobService.uploadFile(multipartFile);
        var video=new Video();
        video.setVideoUrl(videoUrl);

        videoRepository.save(video);
    }
}
