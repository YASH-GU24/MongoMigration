package io.zoooohs.realworld.domain.article.dto;

import io.zoooohs.realworld.domain.profile.dto.ProfileDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleDto {
    private String slug;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String body;

    private List<String> tagList;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean favorited;
    private Long favoritesCount;
    private ProfileDto author;




    // Builder class for ArticleDto
    public static class Builder {
        private String slug;
        private String title;
        private String description;
        private String body;
        private List<String> tagList;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean favorited;
        private Long favoritesCount;
        private ProfileDto author;

        public Builder slug(String slug) {
            this.slug = slug;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder tagList(List<String> tagList) {
            this.tagList = new ArrayList<>(tagList); // Ensure that a mutable copy is created
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder favorited(Boolean favorited) {
            this.favorited = favorited;
            return this;
        }

        public Builder favoritesCount(Long favoritesCount) {
            this.favoritesCount = favoritesCount;
            return this;
        }

        public Builder author(ProfileDto author) {
            this.author = author;
            return this;
        }

        public ArticleDto build() {
            return new ArticleDto(slug, title, description, body, tagList, createdAt, updatedAt, favorited, favoritesCount, author);
        }
    }

    // Static method to initiate the builder
    public static Builder builder() {
        return new Builder();
    }
    public static class SingleArticle<T> {
        private T article;

        // No-arg constructor
        public SingleArticle() {
            // This constructor leaves the article uninitialized (null).
        }

        // Constructor with an article
        public SingleArticle(T article) {
            this.article = article;
        }

        // Getter for article
        public T getArticle() {
            return article;
        }

        // Setter for article
        public void setArticle(T article) {
            this.article = article;
        }
    }


    public static class MultipleArticle {
        private List<ArticleDto> articles;

        // Private constructor to enforce use of builder
        private MultipleArticle(List<ArticleDto> articles) {
            this.articles = articles;
        }

        // Getters (No setters to maintain immutability)
        public List<ArticleDto> getArticles() {
            return articles;
        }

        // Static builder class
        public static class Builder {
            private List<ArticleDto> articles;

            // Method to set the articles list directly
            public Builder articles(List<ArticleDto> articles) {
                this.articles = articles;
                return this;
            }

            // Method to build the final MultipleArticle object
            public MultipleArticle build() {
                return new MultipleArticle(this.articles);  // Direct use of the given list
            }
        }

        // Method to access the builder
        public static Builder builder() {
            return new Builder();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultipleArticleWithCount {
        private List<ArticleDto> articles;
        private long articlesCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor

    public static class Update {
        private String title;
        private String description;
        private String body;
        private List<String> tagList;
    }

    // Getters and Setters for title, tagList, description, and body
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
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

}
