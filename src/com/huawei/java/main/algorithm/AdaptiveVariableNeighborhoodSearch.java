package com.huawei.java.main.algorithm;

import com.huawei.java.main.model.Server;
import com.huawei.java.main.model.Virtual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自适应变量邻域搜索
 *
 * @author Kim小根
 * @date 2021/3/24 13:53
 * <p>Description:</p>
 */
public class AdaptiveVariableNeighborhoodSearch extends AbstractMigrationAlgorithm {

    public AdaptiveVariableNeighborhoodSearch(List<Server> currentServers, Map<Integer, Virtual> currentVirtuals, int maxMoves, double min_cost) {
        super(currentServers, currentVirtuals, maxMoves, min_cost);
    }

    @Override
    public List<Virtual> call() throws Exception {
        return null;
    }

}
