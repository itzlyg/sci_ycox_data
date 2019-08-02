package com.spring.mysql.dao;


import com.spring.mysql.bean.AppDataSources;

import java.util.List;

/**
 * @Description dao
 * @Author xyb
 * @Date 2019-07-23 11:06:51
 * @Version 1.0.0
 */
public interface AppDataSourcesMapper {

    /**
     * @Description 查询集合
     * @Date 2019-07-23 11:13:05
     * @return java.util.List<com.spring.mysql.bean.AppDataSources>
     * @throws
     */
    List<AppDataSources> list ();
}