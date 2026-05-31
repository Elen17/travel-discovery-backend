package com.travel.discovery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheManager cacheManager;

    /**
     * Clears every registered cache so the next request reloads from source.
     * Generic by design — any cache added later is cleared too, without
     * naming it here.
     *
     * @return the names of the caches that were cleared
     */
    public Collection<String> reloadCache() {
        Collection<String> names = cacheManager.getCacheNames();
        for (String name : names) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
        return names;
    }
}
