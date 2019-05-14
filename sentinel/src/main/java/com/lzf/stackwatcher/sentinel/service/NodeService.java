package com.lzf.stackwatcher.sentinel.service;

import com.lzf.stackwatcher.sentinel.entity.Node;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NodeService {

    /**
     * 查看所有的节点
     * @return 所有节点列表
     */
    List<Node> allNode();

    /**
     * 添加观测节点
     * @param node 节点对象
     */
    void insertNode(Node node);
}
