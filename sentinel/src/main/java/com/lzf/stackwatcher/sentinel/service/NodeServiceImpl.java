package com.lzf.stackwatcher.sentinel.service;

import com.lzf.stackwatcher.sentinel.dao.NodeMapper;
import com.lzf.stackwatcher.sentinel.entity.Node;
import com.lzf.stackwatcher.sentinel.entity.NodeExample;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NodeServiceImpl implements NodeService {

    @Autowired private NodeMapper nodeMapper;

    @Override
    public List<Node> allNode() {
        NodeExample ne = new NodeExample();
        return nodeMapper.selectByExample(ne);
    }

    @Override
    public void insertNode(Node node) {
        nodeMapper.insertSelective(node);
    }


}
