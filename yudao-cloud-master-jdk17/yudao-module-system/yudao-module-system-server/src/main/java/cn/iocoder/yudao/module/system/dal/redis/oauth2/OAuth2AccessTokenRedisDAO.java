package cn.iocoder.yudao.module.system.dal.redis.oauth2;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.module.system.dal.redis.RedisKeyConstants.OAUTH2_ACCESS_TOKEN;
import static cn.iocoder.yudao.module.system.dal.redis.RedisKeyConstants.OAUTH2_USER_SESSION;

/**
 * {@link OAuth2AccessTokenDO} 的 RedisDAO
 *
 * @author 芋道源码
 */
@Repository
public class OAuth2AccessTokenRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public OAuth2AccessTokenDO get(String accessToken) {
        String redisKey = formatKey(accessToken);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), OAuth2AccessTokenDO.class);
    }

    public void set(OAuth2AccessTokenDO accessTokenDO) {
        String redisKey = formatKey(accessTokenDO.getAccessToken());
        // 清理多余字段，避免缓存
        accessTokenDO.setUpdater(null).setUpdateTime(null).setCreateTime(null).setCreator(null).setDeleted(null);
        long time = LocalDateTimeUtil.between(LocalDateTime.now(), accessTokenDO.getExpiresTime(), ChronoUnit.SECONDS);
        if (time > 0) {
            stringRedisTemplate.opsForValue().set(redisKey, JsonUtils.toJsonString(accessTokenDO), time, TimeUnit.SECONDS);
        }
    }

    public void delete(String accessToken) {
        String redisKey = formatKey(accessToken);
        stringRedisTemplate.delete(redisKey);
    }

    public void deleteList(Collection<String> accessTokens) {
        List<String> redisKeys = CollectionUtils.convertList(accessTokens, OAuth2AccessTokenRedisDAO::formatKey);
        stringRedisTemplate.delete(redisKeys);
    }

    /** 延长访问令牌的 Redis TTL */
    public void extendTtl(OAuth2AccessTokenDO accessTokenDO) {
        String redisKey = formatKey(accessTokenDO.getAccessToken());
        long time = LocalDateTimeUtil.between(LocalDateTime.now(), accessTokenDO.getExpiresTime(), ChronoUnit.SECONDS);
        if (time > 0) {
            stringRedisTemplate.expire(redisKey, time, TimeUnit.SECONDS);
        }
    }

    // ========== 用户会话（7天滑动过期）==========

    private static final long SESSION_TTL_DAYS = 7;

    /** 首次登录时创建用户会话（固定7天TTL，不续期） */
    public void createUserSession(Long userId, Integer userType) {
        String key = formatSessionKey(userId, userType);
        stringRedisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()),
                SESSION_TTL_DAYS, TimeUnit.DAYS);
    }

    /** 检查用户是否有活跃会话 */
    public boolean hasUserSession(Long userId, Integer userType) {
        String key = formatSessionKey(userId, userType);
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /** 删除用户会话（退出时调用） */
    public void deleteUserSession(Long userId, Integer userType) {
        String key = formatSessionKey(userId, userType);
        stringRedisTemplate.delete(key);
    }

    private static String formatKey(String accessToken) {
        return String.format(OAUTH2_ACCESS_TOKEN, accessToken);
    }

    private static String formatSessionKey(Long userId, Integer userType) {
        return String.format(OAUTH2_USER_SESSION, userId, userType);
    }

}
