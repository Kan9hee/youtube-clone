package com.clonetube.youtubeclone.service;

import com.clonetube.youtubeclone.dto.VideoDto;
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

    public VideoDto editVideo(VideoDto videoDto) {
        //비디오 탐색
        var savedVideo = getVideoById(videoDto.getId());

        //비디오 매핑
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setVideoUrl(videoDto.getVideoUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());

        //비디오 저장
        videoRepository.save(savedVideo);
        return videoDto;
    }

    public String uploadThumbnail(MultipartFile file, String videoId) {
        var savedVideo = getVideoById(videoId);

        String thumbnailUrl = blobService.uploadFile(file);

        savedVideo.setThumbnailUrl(thumbnailUrl);

        videoRepository.save(savedVideo);
        return thumbnailUrl;
    }

    Video getVideoById(String videoId){
        return videoRepository.findById(videoId)
                .orElseThrow(()->new IllegalArgumentException("Cannot find video by id - "+videoId));
    }
}
