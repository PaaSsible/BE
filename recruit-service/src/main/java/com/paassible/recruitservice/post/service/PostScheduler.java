package com.paassible.recruitservice.post.service;

import com.paassible.recruitservice.post.entity.Post;
import com.paassible.recruitservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostScheduler {

    private final PostRepository postRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void closeExpiredPosts() {
        List<Post> expiredPosts =
                postRepository.findByDeadlineBeforeAndClosed(LocalDate.now(), false);

        expiredPosts.forEach(Post::close);
    }
}

