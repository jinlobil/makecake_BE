package com.project.makecake.crawling;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CrawlingImageDto {
    private String storeName;
    private List<String> imageList;
}
