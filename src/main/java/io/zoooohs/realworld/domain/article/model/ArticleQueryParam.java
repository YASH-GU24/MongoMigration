package io.zoooohs.realworld.domain.article.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleQueryParam extends FeedParams {
    private String tag;
    private String author;
    private String favorited;

    public String getAuthor() {
        return author;
    }

    public String getFavorited() {
        return favorited;
    }

    public String getTag() {
        return tag;
    }

    public Integer getOffset() {
        return super.getOffset();
    }

    public Integer getLimit() {
        return super.getLimit();
    }
}
