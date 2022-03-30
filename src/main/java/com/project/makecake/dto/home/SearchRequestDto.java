package com.project.makecake.dto.home;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDto {
    private String searchType;
    private String searchText;
}
