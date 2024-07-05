package io.zoooohs.realworld.domain.article.repository;

import io.zoooohs.realworld.domain.article.entity.ArticleEntity;
import io.zoooohs.realworld.domain.article.entity.CommentEntity;
import io.zoooohs.realworld.domain.common.repository.AutoIncrementIdSave;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository
        extends MongoRepository<ArticleEntity, String>, AutoIncrementIdSave<ArticleEntity> {
//("{slug:'?0'}")
    Optional<ArticleEntity> findBySlug(@Param("slug") String slug);
    List<CommentEntity> getCommentsFromAnArticle(String slug);
}
