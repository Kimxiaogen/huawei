package com.huawei.java.main.algorithm;

import com.huawei.java.main.model.Virtual;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 迁移算法
 * @author Kim小根
 * @date 2021/3/24 12:43
 * <p>Description: 迁移算法</p>
 */
public interface MigrationAlgorithm extends Callable<List<Virtual>>{

    /**
     * 计算cost
     * @return
     */
    double cost();

}
