package io.zoooohs.realworld.domain.tag.repository;

import io.zoooohs.realworld.domain.tag.entity.ArticleTagRelationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends MongoRepository<ArticleTagRelationEntity, String> {
}
