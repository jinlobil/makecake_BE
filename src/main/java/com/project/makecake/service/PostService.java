package com.project.makecake.service;

import com.project.makecake.dto.ImageInfoDto;
import com.project.makecake.model.Design;
import com.project.makecake.model.FolderName;
import com.project.makecake.model.Post;
import com.project.makecake.model.User;
import com.project.makecake.repository.DesignRepository;
import com.project.makecake.repository.PostRepository;
import com.project.makecake.responseDto.DesignResponseDto;
import com.project.makecake.responseDto.PostSimpleResponseDto;
import com.project.makecake.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final DesignRepository designRepository;
    private final S3UploadService s3UploadService;

    // 게시된 도안 사진 리스트
    public List<PostSimpleResponseDto> getAllPosts() {
        List<Post> foundPostList = postRepository.findAll();

        List<PostSimpleResponseDto> responseDtoList = new ArrayList<>();

        for (Post post : foundPostList) {
            PostSimpleResponseDto responseDto = new PostSimpleResponseDto(post);
            responseDtoList.add(responseDto);
        }

        return responseDtoList;
    }

    // 도안 저장
    public DesignResponseDto saveDesign(UserDetailsImpl userDetails, MultipartFile img) throws IOException {
        User user = userDetails.getUser();
        ImageInfoDto imgInfo = s3UploadService.uploadFile(img, FolderName.DESIGN.name());
        Design design = new Design(imgInfo,user);
        Design savedDesign = designRepository.save(design);
        return new DesignResponseDto(savedDesign);
    }
}
