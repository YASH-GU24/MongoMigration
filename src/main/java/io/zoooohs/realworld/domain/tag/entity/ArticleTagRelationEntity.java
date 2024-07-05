package io.zoooohs.realworld.domain.tag.entity;

import io.zoooohs.realworld.domain.article.entity.ArticleEntity;
import io.zoooohs.realworld.domain.common.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "tags")
public class ArticleTagRelationEntity extends BaseEntity {
    private String tag;

    @DBRef
    private ArticleEntity article;

    public ArticleTagRelationEntity() {
    }

    public ArticleTagRelationEntity(String tag, ArticleEntity article) {
        this.tag = tag;
        this.article = article;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ArticleEntity getArticle() {
        return article;
    }

    public void setArticle(ArticleEntity article) {
        this.article = article;
    }

    public static class Builder {
        private String tag;
        private ArticleEntity article;

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder article(ArticleEntity article) {
            this.article = article;
            return this;
        }

        public ArticleTagRelationEntity build() {
            return new ArticleTagRelationEntity(tag, article);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
