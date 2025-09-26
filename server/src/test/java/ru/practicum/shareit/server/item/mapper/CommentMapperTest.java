package ru.practicum.shareit.server.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toCommentDto_shouldConvertCommentToDto() {
        // Given
        User author = new User();
        author.setName("Author Name");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.of(2024, 1, 1, 10, 0));

        // When
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        // Then
        assertNotNull(commentDto);
        assertEquals(1L, commentDto.getId());
        assertEquals("Great item!", commentDto.getText());
        assertEquals("Author Name", commentDto.getAuthorName());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), commentDto.getCreated());
    }

    @Test
    void toComment_shouldConvertDtoToComment() {
        // Given
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        // When
        Comment comment = CommentMapper.toComment(commentDto);

        // Then
        assertNotNull(comment);
        assertEquals("Great item!", comment.getText());
    }

    @Test
    void toCommentDto_shouldHandleNullComment() {
        // When
        CommentDto commentDto = CommentMapper.toCommentDto(null);

        // Then
        assertNull(commentDto);
    }

    @Test
    void toComment_shouldHandleNullDto() {
        // When
        Comment comment = CommentMapper.toComment(null);

        // Then
        assertNull(comment);
    }
}