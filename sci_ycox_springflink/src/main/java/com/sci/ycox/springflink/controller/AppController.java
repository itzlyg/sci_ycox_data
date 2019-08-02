package com.sci.ycox.springflink.controller;

import com.sci.ycox.springflink.bean.AppDataSources;
import com.sci.ycox.springflink.dao.AppDataSourcesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@RestController
public class AppController {

//    @Autowired
    private AppDataSourcesMapper mapper;

    @RequestMapping(value = "/api/app_list", method = RequestMethod.POST)
    public List<AppDataSources> list(@Valid AppDataSources ads){
        return mapper.list();
    }
}
