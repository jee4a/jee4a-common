package com.jee4a.common.lang;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

	/**
	 * 是否全部为空
	 * 
	 * @param arr
	 * @return
	 */
	public static boolean isAllBlank(String... arr) {
		if (arr == null) {
			return true;
		}
		for (String string : arr) {
			if (isNotBlank(string)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否全部不为空
	 * 
	 * @param arr
	 * @return
	 */
	public static boolean isAllNotBlank(String... arr) {
		if (arr == null) {
			return false;
		}
		for (String string : arr) {
			if (isBlank(string)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否包含一个为空
	 * 
	 * @param arr
	 * @return
	 */
	public static boolean isOrBlank(String... arr) {
		if (arr == null) {
			return true;
		}
		for (String string : arr) {
			if (isBlank(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否包含一个不为空
	 * 
	 * @param arr
	 * @return
	 */
	public static boolean isOrNotBlank(String... arr) {
		if (arr == null) {
			return false;
		}
		for (String string : arr) {
			if (isNotBlank(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 字符串比较
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean strEquals(String a, String b) {
		if (a == null && b == null)
			return true;
		if (a == null)
			return false;
		return a.equals(b);
	}

	/**
	 * 转Long
	 * 
	 * @param s
	 * @return
	 */
	public static Long toLong(String s) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 转Integer
	 * 
	 * @param s
	 * @return
	 */
	public static Integer toInteger(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * a to z, A to Z
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isAtoZ(String s) {
		if (isEmpty(s)) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z' || s.charAt(i) >= 'a' && s.charAt(i) <= 'z') {

			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 是否为中文(部分匹配)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean containsChinese(String str) {
		if (str == null) {
			return false;
		}
		for (char c : str.toCharArray()) {
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
			if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
					|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
					|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 统计中文字符数
	 * 
	 * @param str
	 * @return
	 */
	public static int chineseCharCount(String str) {
		int count = 0;
		if (str == null) {
			return count;
		}
		for (char c : str.toCharArray()) {
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
			if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
					|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
					|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
					|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 替换emoji 或者 其他非文字类型的字符
	 * 
	 * @param source
	 * @return
	 */
	public static String replaceEmoji(String source) {
	    if (isEmpty(source))
	        return source;
		Pattern emoji = Pattern.compile("[^\u4e00-\u9fa50-9a-zA-Z]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		Matcher emojiMatcher = emoji.matcher(source);
		boolean result = emojiMatcher.find();
		StringBuffer sb = new StringBuffer();

		while (result) {

			emojiMatcher.appendReplacement(sb, "");
			result = emojiMatcher.find();
		}
		emojiMatcher.appendTail(sb);
		return sb.toString();

	}

	/**
	 * 替换可能存在css注入的字符
	 * 
	 * @param value
	 * @return
	 */
	public static String replcaceXSS(String value) {
		if (isNotEmpty(value)) {
			value=value.trim();
			Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");
			scriptPattern = Pattern.compile("<script(.*?)>",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("eval\\((.*?)\\)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("onload(.*?)=",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
		}
		return value;
	}

	/**
	 * 替换可能存在css注入的字符
	 * 
	 * @param bean
	 * @param clazz
	 * @return
	 */
	public static <T> T replcaceXSSForBean(Object bean, Class<T> clazz) {
		HashMap obj2map = JsonUtils.obj2map(bean);
		for (Object k : obj2map.keySet()) {
			Object v = obj2map.get(k);
			if (v != null && v instanceof String) {
				obj2map.put(k, replcaceXSS(v.toString()));
			}
		}
		return JsonUtils.map2obj(obj2map, clazz);
	}
}
