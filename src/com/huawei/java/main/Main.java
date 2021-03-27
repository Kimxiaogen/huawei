package com.huawei.java.main;

import com.huawei.java.main.manager.Manager;
import com.huawei.java.main.tool.DataTool;

/**
 * 主程序入口
 *
 * @author Kim小根
 * @date 2021/3/11 10:51
 * <p>Description:主程序入口</p>
 */
public class Main {

//    public static void main(String[] args) {
//        String path = "src/com/huawei/java/main/data/training-1.txt";    //读取训练数据-1
//        //String path = "src/com/huawei/java/main/data/training-2.txt";    //读取训练数据-2
//        //String path = "src/com/huawei/java/main/data/training-3.txt";    //读取训练数据-3
//        long start = System.currentTimeMillis();
//        Manager manager = DataTool.constructDataModel(path);    //调度类
//        manager.OutputSolution();
//        long end = System.currentTimeMillis();
//        System.out.println("总用时：" + (end - start) / 1000 + "s");
//    }

    public static void main(String[] args) {
        Manager manager = DataTool.constructDataModel();    //调度类
        manager.OutputSolution();
    }


}
