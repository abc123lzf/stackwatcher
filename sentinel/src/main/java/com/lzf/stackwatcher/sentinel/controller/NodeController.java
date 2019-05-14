package com.lzf.stackwatcher.sentinel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/node")
public class NodeController {

    @RequestMapping("all")
    public Response allNode(HttpServletRequest request, HttpServletResponse response) {

        return null;
    }

}
