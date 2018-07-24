package com.jee4a.common;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jee4a.common.io.cache.redis.RedisUtils;


/**
 */
public abstract class BaseManager {
	
	public Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	public   RedisUtils  redisUtils ;
}
