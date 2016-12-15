package com.fm.commons.util;

import java.util.Collection;

public class CollectionUtils {

	public static boolean isEmpty(Collection<?> collection){
		return null == collection || collection.isEmpty();
	}
	
}
