package cn.edu.zjut.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {
    public Connection getConnection(){
        Connection conn = null;
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String dburl = "jdbc:sqlserver://localhost:1433;databaseName=MySports";
        String username = "sa"; //���ݿ��¼�û���
        String password = "123456"; //���ݿ��¼����
        try{
            Class.forName(driver); //�������ݿ���������
            conn = DriverManager.getConnection(dburl,username,password);
        }catch( Exception e ){ e.printStackTrace();
            System.out.println("����1");
        }
        return conn;
    }
}
