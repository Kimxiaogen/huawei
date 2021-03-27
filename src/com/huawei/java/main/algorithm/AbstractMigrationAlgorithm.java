package com.huawei.java.main.algorithm;

import com.huawei.java.main.model.Node;
import com.huawei.java.main.model.Server;
import com.huawei.java.main.model.Virtual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kim小根
 * @date 2021/3/24 14:32
 * <p>Description:</p>
 */
public abstract class AbstractMigrationAlgorithm implements MigrationAlgorithm {
    /**
     * 当前服务器副本
     */
    protected List<Server> servers;

    /**
     * 当前虚拟机副本
     */
    protected Map<Integer, Virtual> virtuals;

    /**
     * 当前移动次数
     */
    protected int currentMoves;

    /**
     * 最大移动次数
     */
    protected int maxMoves;

    /**
     * 迁移结果集
     */
    protected List<Virtual> result;

    /**
     * 成本权重
     */
    protected static double[] weight = {2, 1, 1};

    public AbstractMigrationAlgorithm(List<Server> currentServers, Map<Integer, Virtual> currentVirtuals, int maxMoves, double min_cost) {
        this.servers = new ArrayList<>();
        this.virtuals = new HashMap<>();
        this.result = new ArrayList<>();
        this.currentMoves = 0;
        this.maxMoves = maxMoves;
        for (Server s : currentServers) {
            //s.
        }
    }

    @Override
    public double cost() {
        //成本分为三种，分别为balance cost和move cost以及virtual type cost
        //其中balance cost表示当前服务器资源平衡状态成本
        //move cost表示当前迁移次数成本
        //virtual type cost表示迁移虚拟机类型成本
        double balCost = 0, moveCost = 0, typeCost = 0;
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            Node a = s.getNode_A(), b = s.getNode_B();
            balCost += Math.max(0, balanceOfNode(a));
            balCost += Math.max(0, balanceOfNode(b));
        }
        moveCost += currentMoves;
        for (int i = 0; i < result.size(); i++) {
            Virtual v = result.get(i);
            if (v.isDoubleNodes()) typeCost--;
        }
        return weight[0] * balCost + weight[1] * moveCost + weight[2] * typeCost;
    }

    /**
     * 计算当前节点的平衡度
     *
     * @param node 节点
     * @return
     */
    protected double balanceOfNode(Node node) {
        double ratio = div(node.getCores(), node.getMemorize());
        return ratio * Math.abs(node.getCores() - node.getMemorize());
    }

    protected double div(int cores, int memorize) {
        if (memorize != 0) return (double) cores / memorize;
        if (cores == 0) return (double) 0;
        return Double.MAX_VALUE - 1 / cores;
    }
}
