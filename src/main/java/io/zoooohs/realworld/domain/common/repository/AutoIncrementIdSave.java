package io.zoooohs.realworld.domain.common.repository;

public interface AutoIncrementIdSave<MongoAutoIncrementID> {
    <S extends MongoAutoIncrementID> S save(S entity);
}

