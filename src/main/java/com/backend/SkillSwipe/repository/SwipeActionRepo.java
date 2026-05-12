package com.backend.SkillSwipe.repository;

import com.backend.SkillSwipe.model.SwipeAction;
import com.backend.SkillSwipe.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SwipeActionRepo extends JpaRepository<SwipeAction, Long> {
    List<SwipeAction> findBySwiper(Users swiper);
    Optional<SwipeAction> findBySwiperAndTarget(Users swiper, Users target);
    List<SwipeAction> findByTargetAndAction(Users target, SwipeAction.Action action);
}
