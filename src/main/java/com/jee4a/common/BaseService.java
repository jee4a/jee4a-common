package com.jee4a.common;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jee4a.common.io.cache.redis.RedisUtils;

/**
 * <p></p> 
 * @author tpeng 2018年2月7日
 * @email 398222836@qq.com
 */
public abstract class BaseService {
	public Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	public   RedisUtils  redisUtils ;
}
