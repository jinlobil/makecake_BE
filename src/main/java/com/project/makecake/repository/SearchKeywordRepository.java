package com.project.makecake.repository;

import com.project.makecake.model.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {
    Optional<SearchKeyword> findBySearchInput(String searchText);
}
