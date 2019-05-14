package com.lzf.stackwatcher.sentinel.dao;

import com.lzf.stackwatcher.sentinel.entity.NotifyMethod;
import com.lzf.stackwatcher.sentinel.entity.NotifyMethodExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface NotifyMethodMapper {
    long countByExample(NotifyMethodExample example);

    int deleteByExample(NotifyMethodExample example);

    int insert(NotifyMethod record);

    int insertSelective(NotifyMethod record);

    List<NotifyMethod> selectByExample(NotifyMethodExample example);

    int updateByExampleSelective(@Param("record") NotifyMethod record, @Param("example") NotifyMethodExample example);

    int updateByExample(@Param("record") NotifyMethod record, @Param("example") NotifyMethodExample example);
}