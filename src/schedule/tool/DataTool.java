package schedule.tool;

import schedule.manager.Manager;
import schedule.manager.impl.ManagerImpl;
import schedule.model.Request;
import schedule.model.Server;
import schedule.model.Virtual;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据读取工具
 *
 * @author Kim小根
 * @date 2021/3/11 10:55
 * <p>Description:实现数据读取功能</p>
 */
public class DataTool {

    /**
     * 读取指定路径下文件，构造初始全部数据模型
     *
     * @param path 读取路径
     * @return 调度管理类
     */
    public static Manager constructDataModel(String path) {
        File file = new File(path);
        Manager model = null;
        List<Server> availableServers = new ArrayList<>();
        List<Server> currentServers = new ArrayList<>();
        Map<String, Virtual> availableVirtuals = new HashMap<>();
        Request[][] requests = null;
        FileReader fr = null;
        BufferedReader br = null;
        String line;
        List<String> text = new ArrayList<>();
        try {    //读取数据
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                text.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在。");
        } catch (IOException e) {
            System.out.println("文件读取异常。");
        }
        //利用数据构造模型
        int i = 0, j = Integer.valueOf(text.get(i++)), k;
        for (; j > 0; i++, j--) {    //构造服务器
            String curr = text.get(i);
            String[] str;
            Server server;
            curr = curr.substring(1, curr.length() - 1);
            str = curr.split(", ");
            server = new Server(str[0], Integer.valueOf(str[1]), Integer.valueOf(str[2]), Integer.valueOf(str[3]), Integer.valueOf(str[4]));
            availableServers.add(server);
        }
        j = Integer.valueOf(text.get(i++));
        for (; j > 0; i++, j--) {    //构造虚拟机
            String curr = text.get(i);
            String[] str;
            Virtual virtual;
            curr = curr.substring(1, curr.length() - 1);
            str = curr.split(", ");
            virtual = new Virtual(str[0], Integer.valueOf(str[1]), Integer.valueOf(str[2]), Integer.valueOf(str[3]));
            availableVirtuals.put(virtual.getType(), virtual);
        }
        j = Integer.valueOf(text.get(i++));
        requests = new Request[j][];
        for (int m = 0; m < j; m++) {    //构造请求序列
            k = Integer.valueOf(text.get(i++));
            requests[m] = new Request[k];
            for (int n = 0; n < k; i++, n++) {
                String curr = text.get(i);
                String[] str;
                curr = curr.substring(1, curr.length() - 1);
                str = curr.split(", ");
                if (str[0].equals("add")) {
                    requests[m][n] = new Request(str[0], str[1], Integer.valueOf(str[2]));
                } else {
                    requests[m][n] = new Request(str[0], null, Integer.valueOf(str[1]));
                }
            }
        }
        model = new ManagerImpl(availableServers, currentServers, availableVirtuals, requests);
        return model;
    }
}