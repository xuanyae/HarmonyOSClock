package com.example.hmostest.dao;

import com.example.hmostest.bean.Clock;

import java.util.List;

public interface ClockMapper {
    int deleteByPrimaryKey(Integer clockid);

    int insert(Clock record);

    int insertSelective(Clock record);

    Clock selectByPrimaryKey(Integer clockid);

    int updateByPrimaryKeySelective(Clock record);

    int updateByPrimaryKey(Clock record);

    List<Clock> selectAll();
}