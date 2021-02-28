/*
 * Symphony - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.processor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.util.Paginator;
import org.b3log.symphony.model.*;
import org.b3log.symphony.service.*;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Domain processor.
 * <ul>
 * <li>Shows domains (/domains), GET</li>
 * <li>Shows domain article (/domain/{domainURI}), GET</li>
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 11, 2020
 * @since 1.4.0
 */
@Singleton
public class DomainProcessor {
    private static final Cache<String, Map<String, Object>> CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(100000)
            .expireAfterAccess(60 * 60 * 24 * 7, TimeUnit.SECONDS)
            .build();
    ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Logger LOGGER = LogManager.getLogger(DomainProcessor.class);
    /**
     * Article query service.
     */
    @Inject
    private ArticleQueryService articleQueryService;

    /**
     * Domain query service.
     */
    @Inject
    private DomainQueryService domainQueryService;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Shows domain articles.
     *
     * @param context the specified context
     */
    public void showDomainArticles(final RequestContext context) {
        long startTime = System.currentTimeMillis();
        final String domainURI = context.pathVar("domainURI");
        final Request request = context.getRequest();

        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "domain-articles.ftl");
        Map<String, Object> dataModel = renderer.getDataModel();
        final int pageNum = Paginator.getPage(request);
        int pageSize = Symphonys.ARTICLE_LIST_CNT;

        final JSONObject user = Sessions.getUser();
        if (null != user) {
            pageSize = user.optInt(UserExt.USER_LIST_PAGE_SIZE);

            if (!UserExt.finshedGuide(user)) {
                context.sendRedirect(Latkes.getServePath() + "/guide");
                return;
            }
        }

        final JSONObject domain = domainQueryService.getByURI(domainURI);
        if (null == domain) {
            context.sendError(404);
            return;
        }

        final List<JSONObject> tags = domainQueryService.getTags(domain.optString(Keys.OBJECT_ID));
        domain.put(Domain.DOMAIN_T_TAGS, (Object) tags);

        dataModel.put(Domain.DOMAIN, domain);
        dataModel.put(Common.SELECTED, domain.optString(Domain.DOMAIN_URI));

        final String domainId = domain.optString(Keys.OBJECT_ID);




        String key = domainId + "_" + pageNum + "_" + pageSize;
        if (null != CACHE.getIfPresent(key)) {
            LOGGER.info("domain命中缓存");
            dataModel = CACHE.getIfPresent(key);
            dataModelService.fillHeaderAndFooter(context, dataModel);
            dataModelService.fillRandomArticles(dataModel);
            dataModelService.fillSideHotArticles(dataModel);
            dataModelService.fillSideTags(dataModel);
            dataModelService.fillLatestCmts(dataModel);
            renderer.getDataModel().putAll(dataModel);
            int finalPageSize = pageSize;
            Map<String, Object> finalDataModel = Maps.newHashMap(dataModel);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    JSONObject result = null;
                    Map<String, Object> dataModel2 = finalDataModel;

                    result = articleQueryService.getDomainArticles(domainId, pageNum, finalPageSize);

                    final List<JSONObject> latestArticles = (List<JSONObject>) result.opt(Article.ARTICLES);
                    dataModel2.put(Common.LATEST_ARTICLES, latestArticles);

                    final JSONObject pagination = result.optJSONObject(Pagination.PAGINATION);
                    final int pageCount = pagination.optInt(Pagination.PAGINATION_PAGE_COUNT);

                    final List<Integer> pageNums = (List<Integer>) pagination.opt(Pagination.PAGINATION_PAGE_NUMS);
                    if (!pageNums.isEmpty()) {
                        dataModel2.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
                        dataModel2.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
                    }

                    dataModel2.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
                    dataModel2.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
                    dataModel2.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);



                    CACHE.put(key, dataModel2);
                }
            });
        } else {
            JSONObject result = null;
            long start = System.currentTimeMillis();
            result = articleQueryService.getDomainArticles(domainId, pageNum, pageSize);
            System.out.println("getDomainArticles:" + (System.currentTimeMillis() - start));
            final List<JSONObject> latestArticles = (List<JSONObject>) result.opt(Article.ARTICLES);
            dataModel.put(Common.LATEST_ARTICLES, latestArticles);

            final JSONObject pagination = result.optJSONObject(Pagination.PAGINATION);
            final int pageCount = pagination.optInt(Pagination.PAGINATION_PAGE_COUNT);

            final List<Integer> pageNums = (List<Integer>) pagination.opt(Pagination.PAGINATION_PAGE_NUMS);
            if (!pageNums.isEmpty()) {
                dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
                dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM, pageNums.get(pageNums.size() - 1));
            }

            dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, pageNum);
            dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            dataModelService.fillHeaderAndFooter(context, dataModel);
            dataModelService.fillRandomArticles(dataModel);
            dataModelService.fillSideHotArticles(dataModel);
            dataModelService.fillSideTags(dataModel);
            dataModelService.fillLatestCmts(dataModel);

            CACHE.put(key, dataModel);
            LOGGER.info("domain没有命中缓存" + (System.currentTimeMillis() - start));

        }








        LOGGER.info("domain耗时:{}", System.currentTimeMillis() - startTime);
    }

    /**
     * Shows domains.
     *
     * @param context the specified context
     */
    public void showDomains(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "domains.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final JSONObject statistic = optionQueryService.getStatistic();
        final int tagCnt = statistic.optInt(Option.ID_C_STATISTIC_TAG_COUNT);
        dataModel.put(Tag.TAG_T_COUNT, tagCnt);

        final int domainCnt = statistic.optInt(Option.ID_C_STATISTIC_DOMAIN_COUNT);
        dataModel.put(Domain.DOMAIN_T_COUNT, domainCnt);

        final List<JSONObject> domains = domainQueryService.getAllDomains();
        dataModel.put(Common.ALL_DOMAINS, domains);

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }
}
