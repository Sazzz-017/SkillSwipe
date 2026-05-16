package com.backend.SkillSwipe.service;

import com.backend.SkillSwipe.dto.UserDTO;
import com.backend.SkillSwipe.kafka.dto.LikeNotificationEvent;
import com.backend.SkillSwipe.kafka.producer.KafkaProducerService;
import com.backend.SkillSwipe.model.*;
import com.backend.SkillSwipe.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SwipeService {

    private static final Logger log = LoggerFactory.getLogger(SwipeService.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserSkillsRepo userSkillsRepo;

    @Autowired
    private SwipeActionRepo swipeActionRepo;

    @Autowired
    private ExchangeRequestsRepo exchangeRequestsRepo;

    /**
     * KafkaProducerService is injected here to publish a LikeNotificationEvent
     * asynchronously after a successful LIKE action.
     * The email is NOT sent here — it is handled by the Kafka consumer,
     * keeping this service fast and the HTTP response unblocked.
     */
    @Autowired
    private KafkaProducerService kafkaProducerService;

    public List<UserDTO> getCandidates(int userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<UsersSkills> mySkills = userSkillsRepo.findByUser(user);

        Set<Integer> myTeachSkillIds = mySkills.stream()
                .filter(s -> s.getType() == UsersSkills.UserSkillType.TEACH)
                .map(s -> s.getSkill().getSkillId())
                .collect(Collectors.toSet());

        Set<Integer> myLearnSkillIds = mySkills.stream()
                .filter(s -> s.getType() == UsersSkills.UserSkillType.LEARN)
                .map(s -> s.getSkill().getSkillId())
                .collect(Collectors.toSet());

        // IDs of users already swiped on
        Set<Integer> alreadySwiped = swipeActionRepo.findBySwiper(user).stream()
                .map(sa -> sa.getTarget().getUserId())
                .collect(Collectors.toSet());

        // Find all users whose skills overlap with mine
        List<Users> allOtherUsers = userRepo.findAll().stream()
                .filter(u -> u.getUserId() != userId)
                .filter(u -> !alreadySwiped.contains(u.getUserId()))
                .collect(Collectors.toList());

        return allOtherUsers.stream()
                .filter(candidate -> {
                    List<UsersSkills> theirSkills = userSkillsRepo.findByUser(candidate);
                    Set<Integer> theirTeachIds = theirSkills.stream()
                            .filter(s -> s.getType() == UsersSkills.UserSkillType.TEACH)
                            .map(s -> s.getSkill().getSkillId())
                            .collect(Collectors.toSet());
                    Set<Integer> theirLearnIds = theirSkills.stream()
                            .filter(s -> s.getType() == UsersSkills.UserSkillType.LEARN)
                            .map(s -> s.getSkill().getSkillId())
                            .collect(Collectors.toSet());
                    // They teach something I want to learn
                    boolean theyTeachWhatILearn = theirTeachIds.stream().anyMatch(myLearnSkillIds::contains);
                    // They want to learn something I teach
                    boolean theyLearnWhatITeach = theirLearnIds.stream().anyMatch(myTeachSkillIds::contains);
                    return theyTeachWhatILearn || theyLearnWhatITeach;
                })
                .map(candidate -> toUserDTO(candidate))
                .collect(Collectors.toList());
    }

    @Transactional
    public SwipeAction recordAction(int swiperId, int targetId, String actionStr) {
        Users swiper = userRepo.findById(swiperId)
                .orElseThrow(() -> new RuntimeException("User not found: " + swiperId));
        Users target = userRepo.findById(targetId)
                .orElseThrow(() -> new RuntimeException("User not found: " + targetId));

        SwipeAction.Action action = SwipeAction.Action.valueOf(actionStr);

        // Upsert: update if exists, else create
        SwipeAction swipeAction = swipeActionRepo.findBySwiperAndTarget(swiper, target)
                .orElse(new SwipeAction());
        swipeAction.setSwiper(swiper);
        swipeAction.setTarget(target);
        swipeAction.setAction(action);
        swipeActionRepo.save(swipeAction);

        // If LIKE, check for mutual match AND send async notification email
        if (action == SwipeAction.Action.LIKE) {
            // ── Async email notification via Kafka ──────────────────────────
            // We publish the event here, immediately after the like is saved.
            // The Kafka consumer will pick it up and send the email without
            // blocking this HTTP request thread.
            LikeNotificationEvent event = new LikeNotificationEvent(
                    target.getUserEmail(),
                    target.getUserName(),
                    swiper.getUserName(),
                    LocalDateTime.now()
            );
            kafkaProducerService.publishLikeNotification(event);
            log.info("[SwipeService] Like notification event published for target user '{}'", target.getUserEmail());
            // ────────────────────────────────────────────────────────────────

            Optional<SwipeAction> reverse = swipeActionRepo.findBySwiperAndTarget(target, swiper);
            if (reverse.isPresent() && reverse.get().getAction() == SwipeAction.Action.LIKE) {
                // reverse.get().swiper = the first liker (target of this action) → they become sender
                createMatchedExchangeRequest(target, swiper);
            }
        }

        return swipeAction;
    }

    @Transactional
    public ExchangeRequests acceptLike(int swiperId, int targetId) {
        // swiperId = original liker, targetId = logged-in user accepting
        Users swiper = userRepo.findById(swiperId)
                .orElseThrow(() -> new RuntimeException("User not found: " + swiperId));
        Users target = userRepo.findById(targetId)
                .orElseThrow(() -> new RuntimeException("User not found: " + targetId));

        // Record that targetId now likes swiperId back
        SwipeAction swipeAction = swipeActionRepo.findBySwiperAndTarget(target, swiper)
                .orElse(new SwipeAction());
        swipeAction.setSwiper(target);
        swipeAction.setTarget(swiper);
        swipeAction.setAction(SwipeAction.Action.LIKE);
        swipeActionRepo.save(swipeAction);

        return createMatchedExchangeRequest(swiper, target);
    }

    public List<UserDTO> getLikesReceived(int userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        return swipeActionRepo.findByTargetAndAction(user, SwipeAction.Action.LIKE).stream()
                .map(sa -> toUserDTO(sa.getSwiper()))
                .collect(Collectors.toList());
    }

    private ExchangeRequests createMatchedExchangeRequest(Users sender, Users receiver) {
        ExchangeRequests request = new ExchangeRequests();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(ExchangeRequests.Status.ACCEPTED);

        // Use first available skills as placeholders — the match is the important part
        List<UsersSkills> senderSkills = userSkillsRepo.findByUser(sender);
        List<UsersSkills> receiverSkills = userSkillsRepo.findByUser(receiver);

        Skills offeredSkill = senderSkills.isEmpty() ? null : senderSkills.get(0).getSkill();
        Skills requestedSkill = receiverSkills.isEmpty() ? null : receiverSkills.get(0).getSkill();

        request.setOfferedSkill(offeredSkill);
        request.setRequestedSkill(requestedSkill);

        return exchangeRequestsRepo.save(request);
    }

    private UserDTO toUserDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setUserEmail(user.getUserEmail());
        dto.setUserBio(user.getUserBio());
        dto.setSkills(userSkillsRepo.findByUser(user));
        return dto;
    }
}
