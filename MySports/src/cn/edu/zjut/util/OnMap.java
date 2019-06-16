package cn.edu.zjut.util;

import cn.edu.zjut.net.ChatSocket;

import java.util.HashMap;

public class OnMap {
    private HashMap<Integer,ChatSocket> clientMap;
    private static OnMap instance;

    public static OnMap getInstance(){
        if(instance==null)
            instance = new OnMap();
        return instance;
    }

    private OnMap(){
        clientMap = new HashMap<Integer, ChatSocket>();
    }

    public synchronized ChatSocket getClientById(int id){
        return clientMap.get(id);
    }

    public synchronized void addClient(int id,ChatSocket ca0){
        clientMap.put(id,ca0);
    }

    public synchronized void removeClient(int id){
        clientMap.remove(id);
    }

    public synchronized boolean isContainId(int id){
        return clientMap.containsKey(id);
    }

    public int size(){
        return clientMap.size();
    }
}
