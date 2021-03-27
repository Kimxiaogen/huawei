package com.huawei.java.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Solution {
    //用Map来记录会产生的主机和虚拟机列表
    // map<hosttype,[cores,mems,price,dayprice]>
    public static HashMap<String, int[]> serverMap = new HashMap<>();
    // map<vmstring,[need_cores,need_mems,type=0/1]>
    public static HashMap<String, int[]> vmMap = new HashMap<>();
    //用二维数组来存放会产生的操作
    //[[[add,vmstring,saveid]]]
    public static ArrayList<ArrayList<String[]>> allOptList = new ArrayList<>();

    // public static ServerPool serverPool = new ServerPool();
    public static Long totalCount = 0L;

    public static void main(String[] args) {
        // readInput();
        readTxt("src/schedule/data/training-2.txt");
        process();
    }

    //从输入中读取信息，并填充上述三个列表
    public static void readInput() {
        Scanner in = new Scanner(System.in);
        int counts = 0;
        counts = Integer.valueOf(in.nextLine());
        for (int i = 0; i < counts; i++) {
            String s = in.nextLine();
            s = s.substring(1, s.length() - 1);
            String[] params = s.split(",");
            int[] values = new int[4];
            for (int j = 0; j < 4; j++) {
                values[j] = Integer.valueOf(params[j + 1].trim());
            }
            serverMap.put(params[0], values);
        }
        counts = Integer.valueOf(in.nextLine());
        for (int i = 0; i < counts; i++) {
            String s = in.nextLine();
            s = s.substring(1, s.length() - 1);
            String[] params = s.split(",");
            int[] values = new int[3];
            for (int j = 0; j < 3; j++) {
                values[j] = Integer.valueOf(params[j + 1].trim());
            }
            vmMap.put(params[0], values);
        }
        counts = Integer.valueOf(in.nextLine());
        for (int i = 0; i < counts; i++) {
            int length = Integer.valueOf(in.nextLine());
            ArrayList<String[]> dayOptList = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                String s = in.nextLine();
                s = s.substring(1, s.length() - 1);
                String[] params = s.split(",");
                dayOptList.add(params);
            }
            allOptList.add(dayOptList);
        }
    }

    public static void process() {
        // 维持一个操作后的id与vmsting+hostid+[A/B节点]绑定, String[3]=[vmstring,hostid,{A,B,1}]
        Map<String, String[]> optIdofVMandHost = new HashMap<>();
        // 当前的服务器
        Map<Integer, int[]> currentHost = new HashMap<>();

        //只买最贵的
        String buytype = "hostUY41I";
        int cores = serverMap.get(buytype)[0];
        int mems = serverMap.get(buytype)[1];
        //给出在几天
        int i = 1;
        for (ArrayList<String[]> opt : allOptList) {
            //
            System.out.println("处理第" + i + "天的任务");
            if(i==800){
                for(int j=0;j<800;j++){
                    for(int l:currentHost.get(j))
                    System.out.print(l+"==");
                    System.out.println("");
                }

            }

            int all_cores = 0;
            int all_mems = 0;
            for (String[] strings : opt) {
                if ("add".equals(strings[0].trim())) {
                    all_cores += vmMap.get(strings[1].trim())[0];
                    all_mems += vmMap.get(strings[1].trim())[1];
                }
//                }else {
//                    all_cores += vmMap.get(strings[1])[0];
//                    all_mems += vmMap.get(strings[1])[1];
//                }
            }
            // todo 优化：购买服务器的逻辑
            // todo start
            int count = all_cores / cores + 1;
            int count1 = all_mems / mems + 1;
            count = count1 > count ? count1 : count;
            totalCount += count* serverMap.get(buytype)[2];
            while (count > 0) {
                int[] coreandmem = {cores / 2, cores / 2, mems / 2, mems / 2};
                currentHost.put(currentHost.size(), coreandmem);
                count--;
            }
            // todo end
            for (String[] strings : opt) {
                if ("add".equals(strings[0].trim())) {
                    // todo 优化添加 start
                    //处理添加操作
                    // [124cpu, 2mem, 1type]
                    int[] vminfo = vmMap.get(strings[1].trim());
                    for (int k = 0; k < currentHost.size(); k++) {
                        int[] current = currentHost.get(k);
                        if (vminfo[2] == 1 && current[0] >= vminfo[0] / 2 && current[1] >= vminfo[0] / 2 && current[2] >= vminfo[1] / 2 && current[3] >= vminfo[1] / 2) {
                            // 双节点部署
                            current[0] -= vminfo[0] / 2;
                            current[1] -= vminfo[0] / 2;
                            current[2] -= vminfo[1] / 2;
                            current[3] -= vminfo[1] / 2;
                            // vmtype hostid type{1,A,B}
                            String[] info = {strings[1].trim(), "" + k, "" + 1};
                            optIdofVMandHost.put(strings[2].trim(), info);
                            break;
                        } else if (vminfo[2] == 0) {
                            // 单节点部署
                            if (current[0] >= vminfo[0] && current[2] >= vminfo[1]) {
                                current[0] -= vminfo[0];
                                current[2] -= vminfo[1];
                                // vmtype hostid type{1,A,B}
                                String[] info = {strings[1].trim(), "" + k, "A"};
                                optIdofVMandHost.put(strings[2].trim(), info);
                                break;
                            } else if (current[1] >= vminfo[0] && current[3] >= vminfo[1]) {
                                current[1] -= vminfo[0];
                                current[3] -= vminfo[1];
                                // vmtype hostid type{1,A,B}
                                String[] info = {strings[1].trim(), "" + k, "B"};
                                optIdofVMandHost.put(strings[2].trim(), info);
                                break;
                            }
                        }
                        if(k==currentHost.size()-1){
                            System.out.println("出现了错误");
                        }
                    }
                    // todo end
                } else {
                    // 处理删除操作
                    String id = strings[1];
                    String[] info = optIdofVMandHost.get(id);
                    int[] current = currentHost.get(Integer.valueOf(info[1]));
                    int[] vminfo = vmMap.get(info[0]);
                    if ("1".equals(info[2])) {
                        current[0] += vminfo[0]/2;
                        current[1] += vminfo[0]/2;
                        current[2] += vminfo[1]/2;
                        current[3] += vminfo[1]/2;
                    } else if ("A".equals(info[2])) {
                        current[0] += vminfo[0];
                        current[2] += vminfo[1];
                    } else if ("B".equals(info[2])) {
                        current[1] += vminfo[0];
                        current[3] += vminfo[1];
                    }
                }
            }
            i++;
        }
        System.out.println(totalCount + currentHost.size()*serverMap.get(buytype)[3]*allOptList.size());
        System.out.println(totalCount);
    }

    public static void readTxt(String path) {
        File file = new File(path);
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件
            int counts = 0;
            counts = Integer.valueOf(br.readLine());
            for (int i = 0; i < counts; i++) {
                String s = br.readLine();
                s = s.substring(1, s.length() - 1);
                String[] params = s.split(",");
                int[] values = new int[4];
                for (int j = 0; j < 4; j++) {
                    values[j] = Integer.valueOf(params[j + 1].trim());
                }
                serverMap.put(params[0], values);
            }
            counts = Integer.valueOf(br.readLine());
            for (int i = 0; i < counts; i++) {
                String s = br.readLine();
                s = s.substring(1, s.length() - 1);
                String[] params = s.split(",");
                int[] values = new int[3];
                for (int j = 0; j < 3; j++) {
                    values[j] = Integer.valueOf(params[j + 1].trim());
                }
                vmMap.put(params[0], values);
            }
            counts = Integer.valueOf(br.readLine());
            for (int i = 0; i < counts; i++) {
                int length = Integer.valueOf(br.readLine());
                ArrayList<String[]> dayOptList = new ArrayList<>();
                for (int j = 0; j < length; j++) {
                    String s = br.readLine();
                    s = s.substring(1, s.length() - 1);
                    String[] params = s.replaceAll(" ","").split(",");
                    dayOptList.add(params);
                }
                allOptList.add(dayOptList);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
