package io.zoooohs.realworld.domain.article.servie;

import io.zoooohs.realworld.domain.article.dto.ArticleDto;
import io.zoooohs.realworld.domain.article.entity.*;
import io.zoooohs.realworld.domain.article.model.ArticleQueryParam;
import io.zoooohs.realworld.domain.article.model.FeedParams;
import io.zoooohs.realworld.domain.article.repository.ArticleRepository;

import io.zoooohs.realworld.domain.article.repository.ArticleRepositoryImpl;
import io.zoooohs.realworld.domain.profile.dto.ProfileDto;
import io.zoooohs.realworld.domain.profile.entity.FollowEntity;
import io.zoooohs.realworld.domain.profile.repository.FollowRepository;
import io.zoooohs.realworld.domain.profile.service.ProfileService;
import io.zoooohs.realworld.exception.AppException;
import io.zoooohs.realworld.exception.Error;
import io.zoooohs.realworld.security.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    // TODO: 1 svc, 1 repo 구조 갖추고 통합 서비스를 두는 것 고려
    private final FollowRepository followRepository;
    private final ArticleRepository mongoArticleRepository;
    private final ArticleRepositoryImpl mongoCustomArticleRepository;

    private final ProfileService profileService;

    private String getSlug(String title) {
        return String.join("-", title.toLowerCase().split(" "));
    }

    @Transactional
    @Override
    public ArticleDto createArticle(ArticleDto article, AuthUserDetails authUserDetails) {
        String slug = getSlug(article.getTitle());

        List<TagEntity> tagList = new ArrayList<>();
        for (String tag : article.getTagList()) {
            tagList.add(TagEntity.builder().tag(tag).build());
        }
            ArticleEntity articleEntity = ArticleEntity.builder()
                    .slug(slug)
                    .title(article.getTitle())
                    .description(article.getDescription())
                    .body(article.getBody())
                    .authorId(authUserDetails.getId())
                    .tags(tagList)
                    .build();


        articleEntity = mongoArticleRepository.save(articleEntity);
        return convertMongoEntityToDto(articleEntity, false, 0L, authUserDetails);
    }

    @Override
    public ArticleDto getArticle(String slug, AuthUserDetails authUserDetails) {
        ArticleEntity found = mongoArticleRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        List<FavoriteEntity> favorites = found.getFavorites();
        boolean favorited = false;
        int favoriteCount = 0;
        if (favorites != null) {
            favorited = favorites.stream().anyMatch(favoriteEntity -> favoriteEntity.getUserId().equals(authUserDetails.getId()));
            favoriteCount = favorites.size();
        }
        return convertMongoEntityToDto(found, favorited, (long) favoriteCount, authUserDetails);
    }

    private ArticleDto convertMongoEntityToDto(ArticleEntity entity, Boolean favorited, Long favoritesCount, AuthUserDetails authUserDetails) {
        ProfileDto author = profileService.getProfileByUserId(entity.getAuthorId(), authUserDetails);
        var tags = entity.getTags() == null ? Collections.EMPTY_LIST : entity.getTags().stream().map(TagEntity::getTag).collect(Collectors.toList());
        return ArticleDto.builder()
                .slug(entity.getSlug())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .body(entity.getBody())
                .author(author)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .favorited(favorited)
                .favoritesCount(favoritesCount)
                .tagList(tags)
                .build();
    }

    @Transactional
    @Override
    public ArticleDto updateArticle(String slug, ArticleDto.Update article, AuthUserDetails authUserDetails) {
        ArticleEntity found = mongoArticleRepository.findBySlug(slug).filter(
                        entity -> entity.getAuthorId().equals(authUserDetails.getId()))
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));

        String updatedSlug = slug;
        if (article.getTitle() != null) {
            updatedSlug = getSlug(article.getTitle());
            found.setTitle(article.getTitle());
            found.setSlug(updatedSlug);
        }

        if (article.getDescription() != null) {
            found.setDescription(article.getDescription());
        }

        if (article.getBody() != null) {
            found.setBody(article.getBody());
        }

        if (!article.getTagList().isEmpty()) {
            found.setTags(
                    article.getTagList()
                            .stream()
                            .map(t -> TagEntity.builder().tag(t).build()).
                            collect(Collectors.toList())
            );
        }
        mongoArticleRepository.save(found);

        return getArticle(updatedSlug, authUserDetails);
    }

    @Transactional
    @Override
    public void deleteArticle(String slug, AuthUserDetails authUserDetails) {
        System.out.println("In Delete. slug = " + slug);
        ArticleEntity found = mongoArticleRepository.findBySlug(slug).filter(
                        entity -> entity.getAuthorId().equals(authUserDetails.getId()))
                .orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));

        mongoArticleRepository.delete(found);
    }

    @Override
    public List<ArticleDto> feedArticles(AuthUserDetails authUserDetails, FeedParams feedParams) {
        List<Long> feedAuthorIds = followRepository.findByFollowerId(authUserDetails.getId()).stream()
                .map(FollowEntity::getFollowee).collect(Collectors.toList());

        // Ensure offset and limit are not null, providing default values if necessary
        int offset = Optional.ofNullable(feedParams.getOffset()).orElse(0);
        int limit = Optional.ofNullable(feedParams.getLimit()).orElse(10); // Default limit if none provided

        // Fetch articles with pagination
        return mongoCustomArticleRepository.findByAuthorIdInOrderByCreatedAtDesc(feedAuthorIds, PageRequest.of(offset, limit)).stream()
                .map(entity -> {
                    List<FavoriteEntity> favorites = entity.getFavorites();

                    boolean favorited = false;
                    int favoriteCount = 0;
                    if (favorites != null) {
                        favorited = favorites.stream()
                                .anyMatch(favoriteEntity -> favoriteEntity.getUserId().equals(authUserDetails.getId()));
                        favoriteCount = favorites.size();
                    }

                    return convertMongoEntityToDto(entity, favorited, (long) favoriteCount, authUserDetails);
                }).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public ArticleDto favoriteArticle(String slug, AuthUserDetails authUserDetails) {
        ArticleEntity found = mongoArticleRepository.findBySlug(slug).orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));

        mongoCustomArticleRepository.findByArticleIdAndUserId(found.getId(), authUserDetails.getId())
                .ifPresent(favoriteEntity -> {
                    throw new AppException(Error.ALREADY_FAVORITED_ARTICLE);
                });

        List<FavoriteEntity> modifiedFavorites = found.getFavorites()
                .stream().map(f -> FavoriteEntity.builder().userId(f.getUserId()).build())
                .collect(Collectors.toList());
        modifiedFavorites.add(FavoriteEntity.builder().userId(authUserDetails.getId()).build());

        found.setFavorites(modifiedFavorites);
        mongoArticleRepository.save(found);

        return getArticle(slug, authUserDetails);
    }

    @Transactional
    @Override
    public ArticleDto unfavoriteArticle(String slug, AuthUserDetails authUserDetails) {
        ArticleEntity found = mongoArticleRepository.findBySlug(slug).orElseThrow(() -> new AppException(Error.ARTICLE_NOT_FOUND));
        found.getFavorites()
                .stream()
                .filter(favoriteEntity -> favoriteEntity.getUserId().equals(authUserDetails.getId())).findAny() //only favorites for the current user
                .orElseThrow(() -> new AppException(Error.FAVORITE_NOT_FOUND));

        List<FavoriteEntity> modifiedFavorites = found.getFavorites()
                .stream()
                .filter(favoriteEntity -> !favoriteEntity.getUserId().equals(authUserDetails.getId()))
                .map(f -> FavoriteEntity.builder().userId(f.getUserId()).build())
                .collect(Collectors.toList());

        found.setFavorites(modifiedFavorites);

        mongoArticleRepository.save(found);
        return getArticle(slug, authUserDetails);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ArticleDto> listArticle(ArticleQueryParam articleQueryParam, AuthUserDetails authUserDetails) {
        Pageable pageable = null;
        if (articleQueryParam.getOffset() != null) {
            pageable = PageRequest.of(articleQueryParam.getOffset(), articleQueryParam.getLimit());
        }

        List<ArticleEntity> mongoArticleEntities;
        if (articleQueryParam.getTag() != null && !articleQueryParam.getTag().isEmpty()) {
            mongoArticleEntities = mongoCustomArticleRepository.findByTagsContaining(articleQueryParam.getTag(), pageable);
        } else if (articleQueryParam.getAuthor() != null && !articleQueryParam.getAuthor().isEmpty()) {
            mongoArticleEntities = mongoCustomArticleRepository.findByAuthorName(articleQueryParam.getAuthor(), pageable);
        } else if (articleQueryParam.getFavorited() != null) {
            mongoArticleEntities = mongoCustomArticleRepository.findByFavoritedUsername(articleQueryParam.getFavorited(), pageable);
        } else {
            mongoArticleEntities = mongoCustomArticleRepository.findAll(pageable);
        }

        return convertToMongoArticleList(mongoArticleEntities, authUserDetails);

    }


    private List<ArticleDto> convertToMongoArticleList(List<ArticleEntity> articleEntities, AuthUserDetails authUserDetails) {
        return articleEntities.stream().map(entity -> {
            List<FavoriteEntity> favorites = entity.getFavorites();
            boolean favorited = false;
            int favoriteCount = 0;
            if (favorites != null) {
                favorited = favorites.stream().anyMatch(favoriteEntity -> favoriteEntity.getUserId().equals(authUserDetails.getId()));
                favoriteCount = favorites.size();
            }
            return convertMongoEntityToDto(entity, favorited, (long) favoriteCount, authUserDetails);
        }).collect(Collectors.toList());
    }
}