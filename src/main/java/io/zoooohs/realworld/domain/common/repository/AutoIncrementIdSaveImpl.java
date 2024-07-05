package io.zoooohs.realworld.domain.common.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import io.zoooohs.realworld.domain.common.entity.AutoIncrementId;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@RequiredArgsConstructor
public class AutoIncrementIdSaveImpl implements AutoIncrementIdSave<AutoIncrementId> {
    public final MongoTemplate mongoTemplate;

    public <S extends AutoIncrementId> S save(S entity) {
        if (entity.getId() == null) {
            MongoCollection<Document> counterCollection = mongoTemplate.getDb().getCollection("counters");
            String collectionName = mongoTemplate.getCollectionName(entity.getClass());
            Document doc = counterCollection.findOneAndUpdate(
                    Filters.eq("_id", collectionName),
                    Updates.inc("counter", 1L),
                    new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
            );
            if (doc != null) {
                entity.setId(doc.getLong("counter"));
            } else {
                // The doc does not exist, find the highest existing ID + 1 and create it.
                // This is not race-condition safe but only happens once.
                Query query = new Query()
                        .limit(1)
                        .with(Sort.by(Sort.Direction.DESC, "id"));
                AutoIncrementId maxUser = mongoTemplate.findOne(query, entity.getClass());
                long counterVal = 1L;
                if (maxUser != null) {
                    counterVal = maxUser.getId() + 1;
                }
                Document counterDoc = new Document();
                counterDoc.put("_id", collectionName);
                counterDoc.put("counter", counterVal);
                counterCollection.insertOne(counterDoc);
                entity.setId(counterVal);
            }
        }
        mongoTemplate.save(entity);
        return entity;
    }
}
