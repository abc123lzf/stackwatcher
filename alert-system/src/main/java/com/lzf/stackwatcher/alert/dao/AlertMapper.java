package com.lzf.stackwatcher.alert.dao;

import com.lzf.stackwatcher.alert.entity.Alert;
import com.lzf.stackwatcher.alert.entity.AlertExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AlertMapper {
    long countByExample(AlertExample example);

    int deleteByExample(AlertExample example);

    int insert(Alert record);

    int insertSelective(Alert record);

    List<Alert> selectByExample(AlertExample example);

    int updateByExampleSelective(@Param("record") Alert record, @Param("example") AlertExample example);

    int updateByExample(@Param("record") Alert record, @Param("example") AlertExample example);

    List<Integer> selectUsingRuleIdByObject(String object);
}