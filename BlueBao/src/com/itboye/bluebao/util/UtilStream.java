package com.itboye.bluebao.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.util.Base64;
import android.util.Log;

/**
 * 流==》string 帮助类
 * @author Administrator
 *
 */
public class UtilStream {

	/**
     * 从输入流中获取数据
     * @param inStream 输入流
     * @return
     * @throws Exception
     */ 
    public static byte[] readStreamToByte(InputStream inStream) throws Exception{ 
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(); 
        byte[] buffer = new byte[1024]; 
        int len = 0; 
        while( (len=inStream.read(buffer)) != -1 ){ 
            outStream.write(buffer, 0, len); 
        } 
        inStream.close(); 
        return outStream.toByteArray(); 
    } 
       
    /**
     * 从输入流中获取数据
     * @param inStream 输入流
     * @return
     * @throws Exception
     */ 
    public static String readStreamToString(InputStream inStream) throws Exception{ 
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(); 
        byte[] buffer = new byte[1024]; 
        int len = 0; 
        while( (len=inStream.read(buffer)) != -1 ){ 
            outStream.write(buffer, 0, len); 
        } 
        inStream.close(); 
        return outStream.toString(); 
    } 
       
    /**
     * 将输入流转化成某字符编码的String
     * @param inStream 输入流
     * @param encoding 编码
     * @return
     * @throws Exception
     */ 
    public static String readStreamToString(InputStream inStream, String encoding) throws Exception{ 
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(); 
        byte[] buffer = new byte[1024]; 
        int len = 0; 
        while( (len=inStream.read(buffer)) != -1 ){ 
            outStream.write(buffer, 0, len); 
        } 
        inStream.close(); 
        return new String(outStream.toByteArray(), encoding); 
    }
    
    
    // from wujian -----------------------------------------------
    /**
     * 将list数据解析成字符串，字符串为原始编码
     *
     * @param SceneList
     * @return
     * @throws IOException
     */
    public static String SurveyList2String(ArrayList SceneList) throws IOException {
        if(SceneList == null)
            return null;
        Log.e("CacheUitls","sceneList不为空");
        // 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 然后将得到的字符数据装载到ObjectOutputStream
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
        objectOutputStream.writeObject(SceneList);
        // 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
        String SceneListString = new String(Base64.encode(
                byteArrayOutputStream.toByteArray(), Base64.DEFAULT));

        // 关闭objectOutputStream
        objectOutputStream.flush();
        objectOutputStream.close();
        return SceneListString;
    }

    /**
     * 将将原始编码的字符串解析成SurveyList
     *
     * @param SceneListString
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static ArrayList String2SurveyList(String SceneListString) throws IOException,
            ClassNotFoundException {
        if(SceneListString==null||SceneListString.isEmpty())
            return null;
        //将base64解码
        byte[] mobileBytes = Base64.decode(SceneListString.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                mobileBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        ArrayList SceneList = (ArrayList) objectInputStream.readObject();
        if(SceneList == null){
            return null;
        }

        objectInputStream.close();
        return SceneList;
    }
    
	/**
	 * 解析服务器返回的信息
	 */
	public static String unicode2String (String str){
		
	        str = (str == null ? "" : str);  
	        if (str.indexOf("\\u") == -1)  
	            return str;  
	  
	        StringBuffer sb = new StringBuffer(1000);  
	  
	        for (int i = 0; i <= str.length() - 6;) {  
	            String strTemp = str.substring(i, i + 6);  
	            String value = strTemp.substring(2);  
	            int c = 0;  
	            for (int j = 0; j < value.length(); j++) {  
	                char tempChar = value.charAt(j);  
	                int t = 0;  
	                switch (tempChar) {  
	                case 'a':  
	                    t = 10;  
	                    break;  
	                case 'b':  
	                    t = 11;  
	                    break;  
	                case 'c':  
	                    t = 12;  
	                    break;  
	                case 'd':  
	                    t = 13;  
	                    break;  
	                case 'e':  
	                    t = 14;  
	                    break;  
	                case 'f':  
	                    t = 15;  
	                    break;  
	                default:  
	                    t = tempChar - 48;  
	                    break;  
	                }  
	  
	                c += t * ((int) Math.pow(16, (value.length() - j - 1)));  
	            }  
	            sb.append((char) c);  
	            i = i + 6;  
	        }  
	        return sb.toString();  
	    }
	}