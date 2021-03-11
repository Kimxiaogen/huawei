package schedule.tool;

import schedule.manager.Manager;
import schedule.model.Server;

import java.util.List;

/**
 * 数据集分析工具
 * @author Kim小根
 * @date 2021/3/11 16:47
 * <p>Description:</p>
 */
public class AnalysisTool {

    public static void printValueForMoney(List<Server> availableServers){

    }

    public static void main(String[] args){
        String path = "src/schedule/data/training-1.txt";    //读取训练数据-1
        //String path = "src/schedule/data/training-2.txt";    //读取训练数据-2
        Manager manager = DataTool.constructDataModel(path);    //调度类
        List<Server> availableServers = manager.getAvailableServers();
        printValueForMoney(availableServers);
    }
}
