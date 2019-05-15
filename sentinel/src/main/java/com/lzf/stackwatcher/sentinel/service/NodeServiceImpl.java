package com.lzf.stackwatcher.sentinel.service;

import com.lzf.stackwatcher.sentinel.bean.*;
import com.lzf.stackwatcher.sentinel.dao.NodeMapper;
import com.lzf.stackwatcher.sentinel.entity.Node;
import com.lzf.stackwatcher.sentinel.entity.NodeExample;
import com.lzf.stackwatcher.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeServiceImpl implements NodeService {
    private final Logger log = LoggerFactory.getLogger(NodeService.class);

    private static final String ZNODE_AGENT_PATH = "/stackwatcher/agent/";
    private static final String ZNODE_COLLECTOR_PATH = "/stackwatcher/collector/";
    private static final String ZNODE_KAFKA_PATH = "/brokers/ids/";
    private static final String ZNODE_INFLUXDB_PATH = "/stackwatcher/influxdb/";

    @Autowired private NodeMapper nodeMapper;
    @Autowired private ZooKeeper zooKeeper;

    private final Map<String, String> kafkaNodeCache = new ConcurrentHashMap<>();

    @Override
    public List<Node> allNode() {
        NodeExample ne = new NodeExample();
        return nodeMapper.selectByExample(ne);
    }

    @Override
    public void insertNode(Node node) {
        nodeMapper.insertSelective(node);
    }

    @Override
    public NodeInfo getNodeInfo(int id) {
        NodeExample ne = new NodeExample();
        ne.createCriteria().andIdEqualTo(id);
        List<Node> list = nodeMapper.selectByExample(ne);

        if(list.size() == 0)
            return null;

        Node node = list.get(0);
        NodeInfo nodeInfo = null;
        try {
            switch (node.getId()) {
                case Node.TYPE_AGENT: {
                    nodeInfo = AgentNode.parseZNodeString(zooKeeper.readNode(ZNODE_AGENT_PATH + node.getHostname()));
                }
                break;
                case Node.TYPE_COLLECTOR: {
                    nodeInfo = CollectorNode.parseZNodeString(zooKeeper.readNode(ZNODE_COLLECTOR_PATH + node.getHostname()));
                }
                break;
                case Node.TYPE_KAFKA: {
                    if(kafkaNodeCache.isEmpty()) {
                        List<String> l = zooKeeper.getChildNode(ZNODE_KAFKA_PATH);
                        for(String path : l) {
                            KafkaNode n = KafkaNode.parseZNodeString(zooKeeper.readNode(path = ZNODE_KAFKA_PATH + path));
                            kafkaNodeCache.put(n.host, path);
                        }
                    }

                    String path = kafkaNodeCache.get(node.getHostname());
                    if(path != null) {
                        nodeInfo = KafkaNode.parseZNodeString(zooKeeper.readNode(path));
                    }
                }
                break;
                case Node.TYPE_INFLUXDB: {
                    nodeInfo = InfluxDBNode.parseZNodeString(zooKeeper.readNode(ZooKeeper.DEFAULT_CONFIG_NAME + node.getHostname()));
                }
                break;
            }

            return nodeInfo;
        } catch (Exception e) {
            log.error("Read znode occur a exception", e);
            return null;
        }
    }
}
