package io.zoooohs.realworld.domain.article.entity;

import io.zoooohs.realworld.domain.common.entity.AutoIncrementId;
import io.zoooohs.realworld.domain.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@Document("articles")
public class ArticleEntity extends BaseEntity implements AutoIncrementId {
    @Id
    private String _id;

    private Long id;
    private Long authorId;
    private String slug;
    private String title;
    private String description;
    private String body;
    private Long commentIdCounter;

    private List<CommentEntity> comments = Collections.emptyList();
    private List<TagEntity> tags = Collections.emptyList();
    private List<FavoriteEntity> favorites = Collections.emptyList();

    // Constructors
    public ArticleEntity() {}

    public ArticleEntity(String _id, Long id, Long authorId, String slug, String title, String description, String body,
                         Long commentIdCounter, List<CommentEntity> comments, List<TagEntity> tags, List<FavoriteEntity> favorites) {
        this._id = _id;
        this.id = id;
        this.authorId = authorId;
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.body = body;
        this.commentIdCounter = commentIdCounter;
        this.comments = new ArrayList<>(comments);
        this.tags = new ArrayList<>(tags);
        this.favorites = new ArrayList<>(favorites);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long newValue) {
        this.id = newValue;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public Long getIdLong() {
        return id;
    }

    public void setIdLong(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getCommentIdCounter() {
        return commentIdCounter;
    }

    public void setCommentIdCounter(Long commentIdCounter) {
        this.commentIdCounter = commentIdCounter;
    }

    public List<CommentEntity> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = new ArrayList<>(comments);
    }

    public List<TagEntity> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = new ArrayList<>(tags);
    }

    public List<FavoriteEntity> getFavorites() {
        return Collections.unmodifiableList(favorites);
    }

    public void setFavorites(List<FavoriteEntity> favorites) {
        this.favorites = new ArrayList<>(favorites);
    }

    public Long getMaxCommentId() {
        if (comments == null) {
            return 0L;
        }
        long maxCommentId = 0;
        for (CommentEntity comment : comments) {
            if (comment.getId() != null && comment.getId() > maxCommentId) {
                maxCommentId = comment.getId();
            }
        }
        return maxCommentId;
    }
}