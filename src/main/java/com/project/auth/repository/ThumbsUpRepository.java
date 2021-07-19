package com.project.auth.repository;

import com.project.auth.domain.ThumbsUp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbsUpRepository extends JpaRepository<ThumbsUp, String> {

}
