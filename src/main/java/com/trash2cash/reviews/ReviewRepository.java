package com.trash2cash.reviews;

import com.trash2cash.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewerId_IdOrTargetUserId_Id(Long reviewerId, Long targetUserId);

}
