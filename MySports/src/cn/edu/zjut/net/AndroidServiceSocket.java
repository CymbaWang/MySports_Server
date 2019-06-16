package cn.edu.zjut.net;

import cn.edu.zjut.pojo.MsgNews;
import cn.edu.zjut.pojo.News;
import cn.edu.zjut.util.OnMap;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class AndroidServiceSocket {
    private ServerSocket serivce;
    private LinkedList<MsgNews> sendQueue = new LinkedList<MsgNews>();
    public static void main(String[] args) throws IOException {
        new AndroidServiceSocket().begin();
    }

    public void begin(){
        try{
            serivce = new ServerSocket(8080);
        }catch (IOException e){
            e.printStackTrace();
        }
        while (true) {
            try {
                Socket socket = serivce.accept();
                new Thread(new ChatSocket(socket,sendQueue)).start();
                new Thread(new SendSocket(sendQueue,socket)).start();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public ChatSocket getClientByID(int id){
        return OnMap.getInstance().getClientById(id);
    }
    public void closeClientByID(int id){
        OnMap.getInstance().removeClient(id);
    }
    public void addClient(int id,ChatSocket ca0){
        OnMap.getInstance().addClient(id,ca0);
    }
    public boolean containId(int id){
        return OnMap.getInstance().isContainId(id);
    }
    public void close(){
        try{
            serivce.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public int size(){
        return OnMap.getInstance().size();
    }


    public synchronized void insertQueue(MsgNews tran) {
        sendQueue.add(tran);
    }

    public synchronized int sizeOfQueue() {
        return sendQueue.size();
    }

    public synchronized MsgNews removeQueueEle(int i) {
        return sendQueue.remove(i);
    }
}
