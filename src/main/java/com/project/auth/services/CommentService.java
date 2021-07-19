package com.project.auth.services;


import com.project.auth.domain.Blog;
import com.project.auth.domain.Comment;
import com.project.auth.domain.User;
import com.project.auth.dtos.CommentDto;
import com.project.auth.repository.BlogRepository;
import com.project.auth.repository.CommentRepository;
import com.project.auth.repository.UserRepository;
import com.project.auth.security.services.UserPrinciple;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          BlogRepository blogRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentDto addComment(String blogId, CommentDto commentDto) {
        Blog blog = this.blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException(format("Blog not found, %s", blogId)));
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userPrinciple.getId())
                .orElseThrow(() -> new RuntimeException(format("User not found %s", userPrinciple.getId())));

        Comment comment = new Comment(user, commentDto.getContent());
        blog.addComment(comment);
        this.blogRepository.save(blog);
        return new CommentDto(comment);
    }

    @Transactional
    public CommentDto updateComment(String commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException(format("comment not found %s", commentId)));
        Comment updatedComment = comment.updateComment(commentDto.getContent());
        return new CommentDto(updatedComment);
    }

    @Transactional
    public void deleteComment(String id, String blogId) {
        Blog blog = this.blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("blog not found"));

        Comment comment = this.commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("comment not found"));

        if (!this.isOwnComment(comment)) {
            throw new RuntimeException("can only delete your comment");
        }
        blog.removeComment(comment);
    }

    private boolean isOwnComment(Comment comment) {
        UserPrinciple userPrinciple = (UserPrinciple) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return comment.getCommentedBy().getId().equals(userPrinciple.getId());
    }
}
