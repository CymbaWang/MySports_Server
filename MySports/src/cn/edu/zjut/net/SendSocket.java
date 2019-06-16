package cn.edu.zjut.net;

import cn.edu.zjut.net.ChatSocket;
import cn.edu.zjut.pojo.MsgNews;
import cn.edu.zjut.pojo.News;
import cn.edu.zjut.service.ChatService;

import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class SendSocket implements Runnable{
    private boolean isRunning;
    private LinkedList<MsgNews> sendQueue;
    private Socket socket;
    public SendSocket(LinkedList<MsgNews> sendQueue, Socket socket)
    {
        this.isRunning=true;
        this.sendQueue = sendQueue;
        this.socket=socket;
    }

    @Override
    public void run(){
        while(isRunning){
            if(socket==null)
                System.out.println("the worlds die");
            if(sizeOfQueue()==0)
            {
                try{
                    TimeUnit.SECONDS.sleep(3);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            else
            {
                MsgNews msgNews = removeQueueEle(0);
                System.out.println("run:"+msgNews.getNews().getSendContent());
                ChatService chatService = new ChatService(sendQueue,socket);
                try {
                    TimeUnit.MILLISECONDS.sleep(66);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                chatService.send(msgNews);
            }
        }
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
