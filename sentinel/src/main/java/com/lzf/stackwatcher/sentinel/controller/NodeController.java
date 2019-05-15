package com.lzf.stackwatcher.sentinel.controller;

import com.lzf.stackwatcher.sentinel.bean.NodeInfo;
import com.lzf.stackwatcher.sentinel.entity.Node;
import com.lzf.stackwatcher.sentinel.service.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/node")
public class NodeController {
    private final Logger log = LoggerFactory.getLogger(NodeController.class);

    @Autowired private NodeService nodeService;

    private static final Pattern IPV4_PATTERN = Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");
    private static final Pattern IPV6_PATTERN = Pattern.compile("^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$");

    @RequestMapping("all")
    public Response allNode() {
        try {
            return new Response(100, "SUCCESS", nodeService.allNode());
        } catch (Exception e) {
            log.error("Http interface /node/all occur a exception", e);
            return new Response(500, "server error");
        }
    }

    @RequestMapping("insert")
    public Response insertNode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String host = request.getParameter("host");
        String ip = request.getParameter("ip");
        if(!IPV4_PATTERN.matcher(ip).matches() && !IPV6_PATTERN.matcher(ip).matches()) {
            response.sendError(400, "Illegal ip address");
            return null;
        }

        int type = Integer.valueOf(request.getParameter("type"));
        Node node = new Node();
        node.setHostname(host);
        node.setIpAddress(ip);
        node.setType(type);

        nodeService.insertNode(node);

        return new Response(100, "SUCCESS");
    }

    @RequestMapping("info")
    public Response nodeInfo(HttpServletRequest request) {
        int id = Integer.valueOf(request.getParameter("id"));
        NodeInfo info = nodeService.getNodeInfo(id);
        return new Response(100, "SUCCESS", info);
    }
}
