package cn.edu.zjut.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;

import cn.edu.zjut.pojo.*;
import cn.edu.zjut.service.ChatService;
import cn.edu.zjut.util.OnMap;
import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import cn.edu.zjut.service.UserService;


/*
 * 建立socket连接
 */
public class ChatSocket implements Runnable {
//    private LinkedList<News> sendQueue;
    Socket socket = null;
    private LinkedList<MsgNews> sendQueue = new LinkedList<MsgNews>();
    public ChatSocket(Socket socket, LinkedList<MsgNews> sendQueue ) {
        this.socket = socket;
        this.sendQueue = sendQueue;
    }

    @Override
    public void run() {
        while(socket!=null) {

    		String line = "";	//用于readLine()读取数据
            String read = "";	//用于记录读取的数据
            InputStream input;	//输入流
            OutputStream output;	//输出流
            try {

                //输入输出流
                input = socket.getInputStream();
                output = socket.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

                //读取来自客户端的数据
                line = bufferedReader.readLine();
                read = read+line;

                GsonBuilder builder = new GsonBuilder();

                // Register an adapter to manage the date types as long values
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                });


                Gson gson =  new Gson();
                        //new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Request request = gson.fromJson(read, Request.class);
              //User user = JSON.parseObject(buffer, User.class);
//              JSONArray jsonArray = new JSONArray(line);
//              for(int i=0;i<jsonArray.length();i++) {
//              JSONObject jsonObject = jsonArray.getJSONObject(i);
//                	Gson gson = new Gson();
//                	user = gson.fromJson(jsonObject.toString(), User.class);
//                	System.out.println(user.getUserAccount());
//              }
//              JsonObject jsonObject = new JsonObject();
//              jsonObject=	gson.fromJson(line, JsonObject.class);
//              User user = new User(jsonObject.get("userAccount").getAsString(),jsonObject.get("userPassword").getAsString());
                String str1="";	//返回客户端的数据
                //type=0 用户注册
                if(request.getType() == 0) {
                	UserService userService = new UserService();
                	User user = gson.fromJson(request.getData(), User.class);
                	String result = userService.Register(user);

                	if(result.equals("accountTwice"))
                		str1 = "accountTwice";
                	else if(result.equals("nameTwice"))
                		str1 = "nameTwice";
                	else if(result.equals("success")) {
                		str1 = "registersuccess";
                	}
                	else{
                		str1 = "registerfail";
                	}
                }
                //type=1 用户登录
                if(request.getType() == 1 ) {
                	UserService userService = new UserService();
                    User user = gson.fromJson(request.getData(), User.class);
                    User loginUser = userService.Login(user); 
                    if(loginUser!=null) {
                        addClient(loginUser.getUserId(),this);
                    	System.out.println(loginUser.getUserRegtime()+" has logan");
                    	SimplePropertyPreFilter filter = new SimplePropertyPreFilter(User.class, "userId","userSex","userName","userPhone","userEmail","userRegtime");
                    	String jsonStu = JSON.toJSONString(loginUser,filter);
                    	str1 = jsonStu;
                    }
                    else {
                    	str1="loginfail";
                    }
                }

                //type=100 send message
                if(request.getType()==100){
                    ChatService chatService = new ChatService(sendQueue,socket);
                    gson = builder.create();
                    News news = gson.fromJson(request.getData(),News.class);
                    chatService.sendMessage(news);
                }

                //type=101 get message
                if(request.getType()==101){
                    ChatService chatService = new ChatService(sendQueue,socket);
//                    System.out.println("i want to get message and the gson = "+gson);
                    MsgRequest msgRequest = gson.fromJson(request.getData(),MsgRequest.class);
                    chatService.getMessage(msgRequest.getSendId(),msgRequest.getReceiveId());
                }

                //type=102 get contact
                if(request.getType()==102){
                    ChatService chatService = new ChatService(sendQueue,socket);
                    int userId = gson.fromJson(request.getData(),Integer.class);
                    chatService.getContact(userId);
                }

                //type=108 pic
                if(request.getType()==108){
                    ChatService chatService = new ChatService(sendQueue,socket);
                    gson = builder.create();
                    News news = gson.fromJson(request.getData(),News.class);
                    chatService.sendMessage(news);
                }

                if(request.getType()==0||request.getType()==1)
                {
                    str1 = str1 +"\n";
                    System.out.println(str1);
                    output.write(str1.getBytes("UTF-8"));
                    output.flush();
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
    	}
    }

    public void addClient(int id,ChatSocket ca0){
        OnMap.getInstance().addClient(id,ca0);
    }

}
