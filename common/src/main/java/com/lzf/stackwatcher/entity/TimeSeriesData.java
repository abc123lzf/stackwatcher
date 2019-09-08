package com.lzf.stackwatcher.entity;

import java.util.Map;

public interface TimeSeriesData extends Data {


    /**
     * 获取该数据的标签
     * @return 包含该数据标签的Map
     */
    Map<String, String> getTags();

    /**
     * 获取该数据的时间
     * @return 时间戳
     */
    long getTime();

    /**
     * 获取该数据的记录值
     * @return 包含所有记录值的Map
     */
    Map<String, Object> getFields();
}
