package com.clonetube.youtubeclone.service;

import com.clonetube.youtubeclone.dto.UploadVideoResponse;
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
    private final UserService userService;

    public UploadVideoResponse uploadVideo(MultipartFile multipartFile){
        //Azure Blob에 파일 업로드 및 데이터베이스에 비디오 데이터 저장
        String videoUrl=blobService.uploadFile(multipartFile);
        var video=new Video();
        video.setVideoUrl(videoUrl);

        var savedVideo = videoRepository.save(video);
        return new UploadVideoResponse(savedVideo.getId(),savedVideo.getVideoUrl());
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

    public VideoDto getVideoDetails(String videoId) {
        Video savedVideo = getVideoById(videoId);

        increaseVideoCount(savedVideo);
        userService.addVideoToHistory(videoId);

        return mapToVideoDto(savedVideo);
    }

    public VideoDto likeVideo(String videoId) {
        Video videoById=getVideoById(videoId);
        //좋아요 증가
        //이미 좋아요를 눌렀다면, 좋아요 감소
        //이미 싫어요를 눌렀다면, 좋아요 증가, 싫어요 감소

        if(userService.ifLikedVideo(videoId)){
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        }else if(userService.ifDisLikedVideo(videoId)){
            videoById.decrementDisLikes();
            userService.removeFromDisLikedVideos(videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }else {
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    private void increaseVideoCount(Video savedVideo){
        savedVideo.incrementViewCount();
        videoRepository.save(savedVideo);
    }

    public VideoDto disLikeVideo(String videoId) {
        Video videoById=getVideoById(videoId);
        //싫어요 증가
        //이미 싫어요를 눌렀다면, 싫어요 감소
        //이미 좋아요를 눌렀다면, 싫어요 증가, 좋아요 감소

        if(userService.ifLikedVideo(videoId)){
            videoById.decrementDisLikes();
            userService.removeFromDisLikedVideos(videoId);
        }else if(userService.ifLikedVideo(videoId)){
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(videoId);
        }else {
            videoById.incrementLikes();
            userService.addToDisLikedVideos(videoId);
        }

        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    private VideoDto mapToVideoDto(Video videoById) {
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(videoById.getVideoUrl());
        videoDto.setThumbnailUrl(videoById.getThumbnailUrl());
        videoDto.setId(videoById.getId());
        videoDto.setTitle(videoById.getTitle());
        videoDto.setDescription(videoById.getDescription());
        videoDto.setTags(videoById.getTags());
        videoDto.setVideoStatus(videoById.getVideoStatus());
        videoDto.setLikeCount(videoById.getLikes().get());
        videoDto.setDislikeCount(videoById.getDisLikes().get());
        videoDto.setViewCount(videoById.getViewCount().get());
        return videoDto;
    }
}
