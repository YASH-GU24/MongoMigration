package io.zoooohs.realworld.domain.article.repository;

import com.mongodb.BasicDBObject;
import io.zoooohs.realworld.domain.article.entity.ArticleEntity;
import io.zoooohs.realworld.domain.article.entity.CommentEntity;
import io.zoooohs.realworld.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleCustomRepository {

    private final MongoTemplate mongoTemplate;


    private Optional<UserEntity> findUserByUsername(String username) {
        Query userQuery = new Query();
        userQuery.addCriteria(Criteria.where("username").is(username));
        List<UserEntity> matchingUsers = mongoTemplate.find(userQuery, UserEntity.class);

        return matchingUsers.stream().findFirst();
    }

    public List<ArticleEntity> findAll(Pageable pageable) {
        Query query = new Query().with(pageable);
        return mongoTemplate.find(query, ArticleEntity.class);
    }

    @Override
    public List<ArticleEntity> findByTagsContaining(String tag, Pageable pageable) {
        Query articleQuery = new Query();
        articleQuery.addCriteria(Criteria.where("tags").elemMatch(Criteria.where("tag").is(tag)));
        return mongoTemplate.find(articleQuery, ArticleEntity.class);
    }

    public List<ArticleEntity> findByAuthorName(String name, Pageable pageable) {
        Optional<UserEntity> maybeUser = findUserByUsername(name);

        if (maybeUser.isEmpty()) {
            return Collections.emptyList();
        }
        Long userId = maybeUser.get().getId();

        Query articleQuery = new Query().with(pageable);
        articleQuery.addCriteria(Criteria.where("authorId").is(userId));

        return mongoTemplate.find(articleQuery, ArticleEntity.class);
    }

    public List<ArticleEntity> findByFavoritedUsername(String favoritedUsername, Pageable pageable) {
        Optional<UserEntity> maybeUser = findUserByUsername(favoritedUsername);

        if (maybeUser.isEmpty()) {
            return Collections.emptyList();
        }
        Long userId = maybeUser.get().getId();

        Query articleQuery = new Query().with(pageable);
        articleQuery.addCriteria(Criteria.where("favorites").elemMatch(Criteria.where("userId").is(userId)));
        return mongoTemplate.find(articleQuery, ArticleEntity.class);
    }

    public List<ArticleEntity> findByAuthorIdInOrderByCreatedAtDesc(List<Long> ids, Pageable pageable) {
        Query query = new Query().with(pageable);
        query.addCriteria(Criteria.where("authorId").in(ids));
        return mongoTemplate.find(query, ArticleEntity.class);
    }

    public Optional<ArticleEntity> findByArticleIdAndUserId(Long articleId, Long userId) {
        Query query = new Query();
        query.addCriteria(
                Criteria
                        .where("id").is(articleId)
                        .and("favorites").elemMatch(Criteria.where("userId").is(userId))
        );
        return mongoTemplate.find(query, ArticleEntity.class).stream().findFirst();
    }

    @Override
    public void addCommentToArticle(ArticleEntity article, CommentEntity commentEntity) {
        Query query = new Query();
        query.addCriteria(Criteria.where("slug").is(article.getSlug()));
        Update updateCommentIdCounter = new Update();
        if (article.getCommentIdCounter() == null) {
            updateCommentIdCounter.set("commentIdCounter", article.getMaxCommentId() + 1);
        } else {
            updateCommentIdCounter.inc("commentIdCounter");
        }
        ArticleEntity updatedArticle = mongoTemplate.findAndModify(
                query, updateCommentIdCounter, FindAndModifyOptions.options().returnNew(true), ArticleEntity.class);
        commentEntity.setId(updatedArticle.getCommentIdCounter());
        Update update = new Update();
        update.push("comments", commentEntity);
        mongoTemplate.updateFirst(query, update, ArticleEntity.class);
    }


    @Override
    public void deleteCommentFromArticle(ArticleEntity articleEntity, Long commentId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("slug").is(articleEntity.getSlug()));
        Update update = new Update();
        update.pull("comments", new BasicDBObject("id", commentId));
        mongoTemplate.updateFirst(query, update, ArticleEntity.class);
    }

    @Override
    public List<CommentEntity> getCommentsFromAnArticle(String slug) {
        Query query = new Query();
        query.addCriteria(Criteria.where("slug").is(slug));
        ArticleEntity article = mongoTemplate.findOne(query, ArticleEntity.class);
        if (article != null && article.getComments() != null) {
            return article.getComments();
        }
        return Collections.emptyList();  // Return an empty list if no comments are found
    }

}
