package org.b3log.symphony.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocalCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCache.class);
    private static final Cache<String, List<JSONObject>> CACHE = CacheBuilder
            .newBuilder().recordStats()
            .expireAfterAccess(60 * 60 * 24, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<String, List<JSONObject>>() {
                @Override
                public void onRemoval(RemovalNotification<String, List<JSONObject>> notification) {
                    LOGGER.debug("出现缓存evit, 当前size:{}", CACHE.size());
                }
            })
            .build();
    private static final LocalCache LOCAL_CACHE = new LocalCache();

    public static LocalCache newLocalCache() {
        return LOCAL_CACHE;
    }
}
