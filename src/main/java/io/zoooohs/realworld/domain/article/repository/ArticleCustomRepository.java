package io.zoooohs.realworld.domain.article.repository;

import io.zoooohs.realworld.domain.article.entity.ArticleEntity;
import io.zoooohs.realworld.domain.article.entity.CommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleCustomRepository {
    List<ArticleEntity> findAll(Pageable pageable);

    //    List<MongoArticleEntity> findByAuthorId(Long authorId);
    List<ArticleEntity> findByTagsContaining(String tag, Pageable pageable);

    List<ArticleEntity> findByAuthorName(String name, Pageable pageable);

    List<ArticleEntity> findByFavoritedUsername(String favoritedUsername, Pageable pageable);

    List<ArticleEntity> findByAuthorIdInOrderByCreatedAtDesc(List<Long> ids, Pageable pageable);

    Optional<ArticleEntity> findByArticleIdAndUserId(Long articleId, Long userId);

    void addCommentToArticle(ArticleEntity articleEntity, CommentEntity commentEntity);

    void deleteCommentFromArticle(ArticleEntity articleEntity, Long commentId);

    List<CommentEntity> getCommentsFromAnArticle(String slug);
}
