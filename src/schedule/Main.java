package schedule;

import schedule.manager.Manager;
import schedule.model.Request;
import schedule.model.Server;
import schedule.model.Virtual;
import schedule.tool.DataTool;

import java.util.List;
import java.util.Map;

/**
 * 主程序入口
 * @author Kim小根
 * @date 2021/3/11 10:51
 * <p>Description:主程序入口</p>
 */
public class Main {

    public static void main(String[] args){
        String path = "src/schedule/data/training-1.txt";    //读取训练数据-1
        //String path = "src/schedule/data/training-2.txt";    //读取训练数据-2
        Manager manager = DataTool.constructDataModel(path);    //调度类
    }


}
