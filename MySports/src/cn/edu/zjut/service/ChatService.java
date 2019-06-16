package cn.edu.zjut.service;

import cn.edu.zjut.dao.NewsDao;
import cn.edu.zjut.net.AndroidServiceSocket;
import cn.edu.zjut.net.ChatSocket;
import cn.edu.zjut.pojo.*;
import cn.edu.zjut.util.OnMap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatService {
//    private LinkedList<News> sendQueue; //message queue

    private User user;
    private Socket mClient;
    private ChatSocket mClientLS;
    private ObjectOutputStream mOutput;
    private ObjectInputStream mInput;
    private LinkedList<MsgNews> sendQueue;
    private OutputStream outputStream;

    public ChatService(LinkedList<MsgNews> sendQueue, Socket mClient) {
        this.sendQueue = sendQueue;
        this.mClient = mClient;


    }

    public synchronized void send(MsgNews tran) {
        try {
//            SimplePropertyPreFilter filter = new SimplePropertyPreFilter(News.class, "newsId","senduserId","receiveuserId","sendContent","sendTime","newsState");

            String jsonStu = JSON.toJSONString(tran);
            Request request = new Request(100, jsonStu);
            Gson gson = new Gson();
            String result = gson.toJson(request) + "\n";
            System.out.println("has been sed");
            outputStream = mClient.getOutputStream();
            outputStream.write(result.getBytes("UTF-8"));
            outputStream.flush();
            notify();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(News news) {
//        ChatSocket friendClient = null;
        String sendName = null;
        String receiveName = null;

        NewsDao newsDao = new NewsDao();
        try {
            newsDao.insertNews(news);
            sendName = newsDao.getName(news.getSenduserId());
            receiveName = newsDao.getName(news.getReceiveuserId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (containId(news.getReceiveuserId())) {
            System.out.println("exec sendMessage / has login and receiveId = " + news.getReceiveuserId());
//            friendClient = getClientByID(news.getReceiveuserId());
            MsgNews msgNews = new MsgNews(news, sendName, receiveName);
            insertQueue(msgNews);
        }


    }

    public void getMessage(int userId, int receiveId) {
//        System.out.println("exec getMessage");
        NewsDao newsDao = new NewsDao();
        List<MsgNews> msgNewsList = new ArrayList<>();
        msgNewsList = newsDao.getNews(userId, receiveId);

//        System.out.println("getMessage and the sr = "+msgNewsList.get(0).getReceiveName());
        String jsonStu = JSON.toJSONString(msgNewsList);
        Request request = new Request(106, jsonStu);
        Gson gson = new Gson();
        String result = gson.toJson(request) + "\n";
        System.out.println(result);
        try {
            outputStream = mClient.getOutputStream();
            outputStream.write(result.getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getContact(int userId) {
//        System.out.println("exec getContact");
        NewsDao newsDao = new NewsDao();
        List<ContactItem> contactItemList = new ArrayList<>();
        contactItemList = newsDao.getContactList(userId);
        try {
            String jsonStu = JSON.toJSONString(contactItemList);
            Request request = new Request(102, jsonStu);
            Gson gson = new Gson();
            String result = gson.toJson(request) + "\n";
            System.out.println(result);

            outputStream = mClient.getOutputStream();
            outputStream.write(result.getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ChatSocket getClientByID(int id) {
        return OnMap.getInstance().getClientById(id);
    }

    public boolean containId(int id) {
        return OnMap.getInstance().isContainId(id);
    }

    public synchronized void insertQueue(MsgNews tran) {
        sendQueue.add(tran);

    }

}



