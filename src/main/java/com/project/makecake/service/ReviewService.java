package com.project.makecake.service;

import com.project.makecake.dto.HomeReviewDto;
import com.project.makecake.dto.HomeStoreDto;
import com.project.makecake.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private ReviewRepository reviewRepository;

    public List<HomeReviewDto> getHomeReviewList(){
        List<HomeReviewDto> homeReviewDtoList = new ArrayList<>();

        return homeReviewDtoList;
    }

}
