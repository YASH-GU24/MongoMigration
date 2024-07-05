package io.zoooohs.realworld.domain.article.model;

import lombok.*;

import javax.validation.constraints.AssertTrue;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedParams {
    protected Integer offset;
    protected Integer limit;

    @AssertTrue
    protected boolean isValidPage() {
        return (offset != null && limit != null) || (offset == null && limit == null);
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }
}
