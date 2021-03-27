package com.huawei.java.main.algorithm;

import com.huawei.java.main.model.Server;
import com.huawei.java.main.model.Virtual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于模拟退火的超启发式
 *
 * @author Kim小根
 * @date 2021/3/24 14:05
 * <p>Description:</p>
 */
public class SimulatedAnnealingbasedHyperHeuristic extends AbstractMigrationAlgorithm {

    public SimulatedAnnealingbasedHyperHeuristic(List<Server> currentServers, Map<Integer, Virtual> currentVirtuals, int maxMoves, double min_cost) {
        super(currentServers, currentVirtuals,maxMoves, min_cost);
    }

    @Override
    public List<Virtual> call() throws Exception {
        return null;
    }

}
