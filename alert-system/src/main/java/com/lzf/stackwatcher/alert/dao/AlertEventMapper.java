package com.lzf.stackwatcher.alert.dao;

import com.lzf.stackwatcher.alert.entity.AlertEvent;
import com.lzf.stackwatcher.alert.entity.AlertEventExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AlertEventMapper {
    long countByExample(AlertEventExample example);

    int deleteByExample(AlertEventExample example);

    int insert(AlertEvent record);

    int insertSelective(AlertEvent record);

    List<AlertEvent> selectByExample(AlertEventExample example);

    int updateByExampleSelective(@Param("record") AlertEvent record, @Param("example") AlertEventExample example);

    int updateByExample(@Param("record") AlertEvent record, @Param("example") AlertEventExample example);
}