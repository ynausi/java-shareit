package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toData(CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public CommentResponse toResponse(Comment comment) {
        CommentResponse commentResponse = new CommentResponse();

        commentResponse.setId(comment.getId());
        commentResponse.setCreated(comment.getCreated());
        commentResponse.setText(comment.getText());
        commentResponse.setAuthorName(comment.getAuthor().getName());

        return commentResponse;
    }
}
