package com.yida.epub.utils;

import java.util.Collection;

/**
 * Collection集合工具
 */
public class CollectionUtils {
	public static <E> boolean isEmpty(Collection<E> collection) {
		return collection == null || collection.isEmpty();
	}

	public static <E> boolean isNotEmpty(Collection<E> collection) {
		return !isEmpty(collection);
	}
}
