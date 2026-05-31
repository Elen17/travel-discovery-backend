package com.travel.discovery.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.Nullable;

/**
 * Keeps the app resilient to unreadable cache entries — e.g. values left in
 * Redis by a previous serializer format. Instead of letting a deserialization
 * failure surface as a 500, a failed read is treated as a cache miss: the bad
 * entry is evicted and the value is recomputed (and re-stored in the current
 * format) by the underlying method.
 */
@Slf4j
public class RedisCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Cache GET failed for {}::{} — treating as miss and evicting: {}",
            cache.getName(), key, exception.getMessage());
        try {
            cache.evict(key);
        } catch (RuntimeException evictEx) {
            log.warn("Failed to evict bad entry {}::{}: {}", cache.getName(), key, evictEx.getMessage());
        }
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
        log.warn("Cache PUT failed for {}::{}: {}", cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Cache EVICT failed for {}::{}: {}", cache.getName(), key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Cache CLEAR failed for {}: {}", cache.getName(), exception.getMessage());
    }
}