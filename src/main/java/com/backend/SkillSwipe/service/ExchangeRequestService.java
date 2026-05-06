package com.backend.SkillSwipe.service;

import com.backend.SkillSwipe.model.ExchangeRequests;
import com.backend.SkillSwipe.model.ExchangeRequests.Status;
import com.backend.SkillSwipe.model.Users;
import com.backend.SkillSwipe.repository.ExchangeRequestsRepo;
import com.backend.SkillSwipe.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRequestService {

    @Autowired
    ExchangeRequestsRepo exchangeRequestsRepo;

    @Autowired
    UserRepo userRepo;

    public ExchangeRequests createRequest(ExchangeRequests request) {
        request.setStatus(Status.PENDING);
        return exchangeRequestsRepo.save(request);
    }

    public List<ExchangeRequests> getSentRequests(int userId) {
        Users sender = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return exchangeRequestsRepo.findBySender(sender);
    }

    public List<ExchangeRequests> getReceivedRequests(int userId) {
        Users receiver = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return exchangeRequestsRepo.findByReceiver(receiver);
    }

    public ExchangeRequests acceptRequest(int id) {
        ExchangeRequests request = exchangeRequestsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found with id: " + id));
        if (request.getStatus() != Status.PENDING) {
            throw new RuntimeException("Can only accept a PENDING request. Current status: " + request.getStatus());
        }
        request.setStatus(Status.ACCEPTED);
        return exchangeRequestsRepo.save(request);
    }

    public ExchangeRequests rejectRequest(int id) {
        ExchangeRequests request = exchangeRequestsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found with id: " + id));
        if (request.getStatus() != Status.PENDING) {
            throw new RuntimeException("Can only reject a PENDING request. Current status: " + request.getStatus());
        }
        request.setStatus(Status.REJECTED);
        return exchangeRequestsRepo.save(request);
    }

    public ExchangeRequests completeRequest(int id) {
        ExchangeRequests request = exchangeRequestsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found with id: " + id));
        if (request.getStatus() != Status.ACCEPTED) {
            throw new RuntimeException("Can only complete an ACCEPTED request. Current status: " + request.getStatus());
        }
        request.setStatus(Status.COMPLETED);
        return exchangeRequestsRepo.save(request);
    }
}

