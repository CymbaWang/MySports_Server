package cn.edu.zjut.dao;

import cn.edu.zjut.pojo.ContactItem;
import cn.edu.zjut.pojo.MsgNews;
import cn.edu.zjut.pojo.News;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsDao {


    public void insertNews(News news) throws SQLException {
//        System.out.println("insert news");
        String sql = "INSERT INTO news " +
                " (senduserId,receiveuserId,sendContent,sendTime,newsState)VALUES(?,?,?,?,?)";
        Connection conn;
        DBConnect dbConnect = new DBConnect();
        conn = dbConnect.getConnection();
        PreparedStatement pstmt = null;
//        java.util.Date dateutil = news.getSendTime();
//        java.sql.Date date = new java.sql.Date(dateutil.getTime());
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, news.getSenduserId());
            pstmt.setInt(2, news.getReceiveuserId());
            pstmt.setString(3, news.getSendContent());
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//            java.util.Date date = sdf.parse(sdf.format(new Date(System.currentTimeMillis())));
            pstmt.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
//            pstmt.setDate(4, new java.sql.Date(date.getTime()));
            pstmt.setBoolean(5, news.isNewsState());
            pstmt.executeUpdate();
//            return true;
        } catch (SQLException  se) {
            se.printStackTrace();
//            return false;
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public List<MsgNews> getNews(int sendId, int receiveId) {
//        System.out.println("get news");
        String sql =
                "SELECT * FROM news WHERE (receiveuserId = ? AND senduserId = ?) OR (receiveuserId = ? AND senduserId = ?)";
        List<MsgNews> msgNewsList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        News news;
        MsgNews msgNews;
        try {
            DBConnect dbConnect = new DBConnect();
            conn = dbConnect.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, receiveId);
            pstmt.setInt(2, sendId);
            pstmt.setInt(3, sendId);
            pstmt.setInt(4, receiveId);
            rst = pstmt.executeQuery();
            while (rst.next()) {
                news = new News();
                news.setNewsId(rst.getInt("newsId"));
                news.setSenduserId(rst.getInt("senduserId"));
                news.setSendContent(rst.getString("sendContent"));
                news.setReceiveuserId(rst.getInt("receiveuserId"));

//                String sqldate = rst.getDate("sendTime").toString();

//                java.sql.Date datesql = rst.getDate("sendTime");
                //*
                java.util.Date dateutil = new java.util.Date(rst.getTimestamp("sendTime").getTime());

                news.setSendTime(dateutil);
                news.setNewsState(rst.getBoolean("newsState"));
//                System.out.println("it's sure that the id is "+news.getReceiveuserId());
                msgNews = new MsgNews(news, getName(news.getSenduserId()), getName(news.getReceiveuserId()));
//                System.out.println("the news sn is "+msgNews.getSendName()+"and the rn is "+msgNews.getReceiveName());
                msgNewsList.add(msgNews);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.out.println("����2");
            return null;
        } finally {
            try {
                pstmt.close();
                conn.close();

            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return msgNewsList;
    }


    public String getName(int id) {
//        System.out.println("get name and the id is " + id);
        String name = null;
        String sql =
                "SELECT userName FROM usertable WHERE userId = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            DBConnect dbConnect = new DBConnect();
            conn = dbConnect.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rst = pstmt.executeQuery();
            if (rst.next()) {
                name = rst.getString("userName");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.out.println("����2");
            return null;
        } finally {
            try {
                pstmt.close();
                conn.close();

            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return name;
    }


    public List<ContactItem> getContactList(int userId) {
//        System.out.println("get contactList");
        String sql =
                "SELECT * FROM usertable WHERE userId IN (SELECT follow.userId FROM follow WHERE fansId = ?)";
        List<ContactItem> contactItemList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        ContactItem contactItem = null;
        News news = null;
        try {
            DBConnect dbConnect = new DBConnect();
            conn = dbConnect.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rst = pstmt.executeQuery();
            while (rst.next()) {
//                news = new News();
                contactItem = new ContactItem();
                contactItem.setConId(rst.getInt("userId"));
                contactItem.setNickName(rst.getString("userName"));
                contactItemList.add(contactItem);
//                System.out.println("sendId = " + contactItem.getConId() + " userId = " + userId);
                news = getLastNews(contactItem.getConId(), userId);
                contactItem.setContent(news.getSendContent());
                contactItem.setTime(news.getSendTime());
            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.out.println("����2");
            return null;
        } finally {
            try {
                pstmt.close();
                conn.close();

            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return contactItemList;
    }

    private News getLastNews(int sendId, int receId) {
//        System.out.println("get last news");
        News news = new News();
        String sql =
                "SELECT TOP 1 * FROM news WHERE (senduserId = ? AND receiveuserId = ?) OR (senduserId = ? AND receiveuserId = ?) ORDER BY newsId DESC ";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            DBConnect dbConnect = new DBConnect();
            conn = dbConnect.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sendId);
            pstmt.setInt(2, receId);
            pstmt.setInt(3, receId);
            pstmt.setInt(4, sendId);
            rst = pstmt.executeQuery();
            if (rst.next()) {
//                System.out.println("find the last news and the news id is " + rst.getInt("newsId"));
                news.setNewsId(rst.getInt("newsId"));
                news.setSenduserId(rst.getInt("senduserId"));
                news.setSendContent(rst.getString("sendContent"));
                news.setNewsState(rst.getBoolean("newsState"));
                news.setReceiveuserId(rst.getInt("receiveuserId"));
//                java.sql.Date datesql = rst.getDate("sendTime");

                //*
                java.util.Date dateutil = new java.util.Date(rst.getTimestamp("sendTime").getTime());
//                System.out.println("time is " + datesql.getTime()/1000);
                news.setSendTime(dateutil);
            }
        } catch (SQLException se) {
            se.printStackTrace();
            System.out.println("����2");
            return null;
        } finally {
            try {
                pstmt.close();
                conn.close();

            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return news;
    }
}
