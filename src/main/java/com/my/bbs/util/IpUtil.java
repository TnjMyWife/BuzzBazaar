package com.my.bbs.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;


public class IpUtil {
    static String dbPath = "/ipdb/ip2region.xdb";

    public static String getIpAddress(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        System.out.println("x-forwarded-for ip: " + ip);
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",")!=-1 ){
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            System.out.println("Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            System.out.println("WL-Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            System.out.println("HTTP_CLIENT_IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            System.out.println("HTTP_X_FORWARDED_FOR ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            System.out.println("X-Real-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            System.out.println("getRemoteAddr ip: " + ip);
        }
        //获取本地ip
        if("0:0:0:0:0:0:0:1".equals(ip)){
            try {
                ip =  InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            System.out.println("getLocal ip: " + ip);
        }
        return ip;
    }

    public static String getLocation(HttpServletRequest request) {
        Random random = new Random();
        // 生成四个字节的随机数，每个字节在0到255之间
        String ip = random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256) + "." +
                random.nextInt(256);
        //String ip = IpUtil.getIpAddress(request);
        String location = "中国|未知|未知|未知|未知";

        Searcher searcher;
        try {
            InputStream inputStream = new ClassPathResource(dbPath).getInputStream();
            byte[] dbBinStr = FileCopyUtils.copyToByteArray(inputStream);
            searcher = Searcher.newWithBuffer(dbBinStr);
            location = searcher.search(ip);
        } catch (Exception e) {
            System.out.printf("failed to create content cached searcher: %s\n", e);
            throw new RuntimeException(e);
        }

        System.out.println("=== 访问者的地址为："+location+" === ");
        String[] parts = location.split("\\|");
        // 提取出所需的部分
        String country = parts[0];
        if (country.equals("0")){
            country = "阿姆斯特丹";
        }
        String province = parts[2];
        String city = parts[3];

        if (!city.equals("0")){
            return country + ":" + city;
        }
        city = "未知";
        if (!province.equals("0"))
            return country + ":" + province;
        province = "未知";

        return country + ":" + city;
    }
}


