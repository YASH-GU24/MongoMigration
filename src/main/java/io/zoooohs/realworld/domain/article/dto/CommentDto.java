package io.zoooohs.realworld.domain.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.zoooohs.realworld.domain.profile.dto.ProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.annotation.processing.Generated;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @NotNull
    private String body;
    private ProfileDto author;

    public static class SingleComment {
        private CommentDto comment;


        public SingleComment(@JsonProperty("comment") CommentDto comment) {
            this.comment = comment;
        }
        public static SingleCommentBuilder builder() {
            return new SingleCommentBuilder();
        }

        public static class SingleCommentBuilder {
            private CommentDto comment;

            public SingleCommentBuilder comment(CommentDto comment) {
                this.comment = comment;
                return this;
            }

            public SingleComment build() {
                return new SingleComment(comment);
            }
        }

        public CommentDto getComment() {
            return comment;
        }
    }


    public static class MultipleComments {
        private List<CommentDto> comments;

        private MultipleComments(List<CommentDto> comments) {
            this.comments = comments;
        }

        public static MultipleCommentsBuilder builder() {
            return new MultipleCommentsBuilder();
        }

        public static class MultipleCommentsBuilder {
            private List<CommentDto> comments;

            public MultipleCommentsBuilder comments(List<CommentDto> comments) {
                this.comments = comments;
                return this;
            }

            public MultipleComments build() {
                return new MultipleComments(comments);
            }
        }

        public List<CommentDto> getComments() {
            return comments;
        }
    }

}
