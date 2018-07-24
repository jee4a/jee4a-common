package com.jee4a.common.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class HttpUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	/**
	 * 定义编码格式 UTF-8
	 */
	public static final String UTF_8 = "UTF-8";

	/**
	 * 定义编码格式 GBK
	 */
	public static final String GBK = "GBK";

	private static final String URL_PARAM_CONNECT_FLAG = "&";

	private static final String EMPTY = "";

	private static MultiThreadedHttpConnectionManager connectionManager = null;

	private static int connectionTimeOut = 20000;

	private static int socketTimeOut = 120000;

	private static int maxConnectionPerHost = 200;

	private static int maxTotalConnections = 200;

	private static HttpClient client;

	static {
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setConnectionTimeout(connectionTimeOut);
		connectionManager.getParams().setSoTimeout(socketTimeOut);
		connectionManager.getParams().setDefaultMaxConnectionsPerHost(maxConnectionPerHost);
		connectionManager.getParams().setMaxTotalConnections(maxTotalConnections);
		client = new HttpClient(connectionManager);
	}

	/**
	 * POST方式提交数据
	 * 
	 * @param url
	 *            待请求的URL
	 * @param params
	 *            要提交的数据
	 * @param enc
	 *            编码
	 * @return 响应结果
	 * @throws IOException
	 *             IO异常
	 */
	public static String post(String url, Map<String, Object> params, String enc) {
		String response = EMPTY;
		PostMethod postMethod = null;
		try {
			postMethod = new PostMethod(url);
			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);
			// 将表单的值放入postMethod中
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				Object value = params.get(key);
				if (value != null) {
					postMethod.addParameter(key, value.toString());
				}
			}
			// 执行postMethod
			int statusCode = client.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(postMethod.getResponseBodyAsStream(), enc));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				response = sb.toString();
			} else {
				logger.error("statusCode = " + postMethod.getStatusCode());
				logger.error("response = " + postMethod.getResponseBodyAsString());
			}
		} catch (HttpException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (postMethod != null) {
				postMethod.releaseConnection();
				postMethod = null;
			}
		}
		return response;
	}
	
	public static String postJson(String url, String body, String enc) {
        String response = EMPTY;
        PostMethod postMethod = null;
        try {
            postMethod = new PostMethod(url);
            postMethod.setRequestHeader("Content-Type", "application/json;charset=" + enc);
            // 将表单的值放入postMethod中
            if (body != null) {
                ByteArrayRequestEntity en = new ByteArrayRequestEntity(body.getBytes(enc));
                postMethod.setRequestEntity(en);
            }
            // 执行postMethod
            int statusCode = client.executeMethod(postMethod);
            if (statusCode == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(postMethod.getResponseBodyAsStream(), enc));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                response = sb.toString();
            } else {
                logger.error("statusCode = " + postMethod.getStatusCode());
                logger.error("response = " + postMethod.getResponseBodyAsString());
            }
        } catch (HttpException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
                postMethod = null;
            }
        }
        return response;
    }

	/**
	 * GET方式提交数据
	 * 
	 * @param url
	 *            待请求的URL
	 * @param params
	 *            要提交的数据
	 * @param enc
	 *            编码
	 * @return 响应结果
	 * @throws IOException
	 *             IO异常
	 */
	public static String get(String url, Map<String, Object> params, String enc) {

		String response = EMPTY;
		GetMethod getMethod = null;
		StringBuffer strtTotalURL = new StringBuffer(EMPTY);
		strtTotalURL.append(url);
		if (params != null && params.keySet().size() != 0) {
    		if (strtTotalURL.indexOf("?") == -1) {
    		    strtTotalURL.append("?").append(getUrl(params, enc));
    		} else {
    			strtTotalURL.append("&").append(getUrl(params, enc));
    		}
		}
		try {
			getMethod = new GetMethod(strtTotalURL.toString());
			getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);
			// 执行getMethod
			int statusCode = client.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
			    //getResponseCharSet 有问题
				//response = getMethod.getResponseBodyAsString();
			    byte[] responseBody = getMethod.getResponseBody();
			    if (StringUtils.isEmpty(enc)) {
			        response = new String(responseBody);
			    } else {
			        response = new String(responseBody, enc);
			    }
			} else {
				logger.error("statusCode = " + getMethod.getStatusCode());
			}
		} catch (HttpException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (getMethod != null) {
				getMethod.releaseConnection();
				getMethod = null;
			}
		}

		return response;
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 * @param file
	 * @param params
	 * @param enc
	 * @return
	 */
	public static boolean download(String url, File file, Map<String, Object> params, String enc) {
		GetMethod getMethod = null;
		StringBuffer strtTotalURL = new StringBuffer(url);
		if (params != null) {
			if (strtTotalURL.indexOf("?") == -1) {
				strtTotalURL.append("?").append(getUrl(params, enc));
			} else {
				strtTotalURL.append("&").append(getUrl(params, enc));
			}
		}
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			getMethod = new GetMethod(strtTotalURL.toString());
			getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + enc);
			// 执行getMethod
			int statusCode = client.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				inputStream = getMethod.getResponseBodyAsStream();
				fileOutputStream = new FileOutputStream(file);
				byte[] b = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(b)) > 0) {
					fileOutputStream.write(b, 0, len);
				}
				return true;
			} else {
				logger.error("statusCode = " + getMethod.getStatusCode());
			}
		} catch (HttpException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e2) {
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (Exception e2) {
				}
			}
			if (getMethod != null) {
				getMethod.releaseConnection();
				getMethod = null;
			}
		}
		return false;
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 * @param file
	 * @return
	 */
	public static boolean download(String url, File file) {
		return download(url, file, null, null);
	}

	public static void main(String[] args) {
		download("http://www.cnpps.org/attachement/jpg/site15/20140922/bc305bae4037158a06ea62.jpg",
				new File("d:/123.jpg"));
	}

	public static HttpClient getClient() {
		return client;
	}

	/**
	 * 据Map生成URL字符串
	 * 
	 * @param map
	 *            Map
	 * @param valueEnc
	 *            URL编码
	 * @return URL
	 */
	private static String getUrl(Map<String, Object> map, String valueEnc) {

		if (null == map || map.keySet().size() == 0) {
			return (EMPTY);
		}
		StringBuffer url = new StringBuffer();
		for (String key : map.keySet()) {
			Object val = map.get(key);
			if (val == null) {
				continue;
			}
			try {
				val = URLEncoder.encode(val.toString(), valueEnc);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage(), e);
			}
			url.append(key).append("=").append(val).append(URL_PARAM_CONNECT_FLAG);
		}
		String strURL = url.toString();
		if (strURL.endsWith(URL_PARAM_CONNECT_FLAG)) {
			strURL = strURL.substring(0, strURL.length() - 1);
		}
		return strURL;
	}
}
