package io.zoooohs.realworld.domain.tag.service;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<String> listOfTags(int maxResults) {
        MongoCollection<Document> collection = mongoTemplate.getCollection("articles");
        AggregateIterable<Document> result = collection.aggregate(
                Arrays.asList(
                        Aggregates.unwind("$tags"),
                        Aggregates.group("$tags.tag", Accumulators.sum("count", 1)),
                        Aggregates.sort(Sorts.descending("count")),
                        Aggregates.limit(maxResults)
                )
        );

        return StreamSupport.stream(result.spliterator(), false)
                .map(document -> document.getString("_id"))
                .collect(Collectors.toList());
    }
}
