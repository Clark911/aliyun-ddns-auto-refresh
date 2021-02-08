package com.oobss.ddns;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeSubDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeSubDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.oobss.ddns.constant.DnsConstant;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DDNS implements Job {
    /**
     * 获取主域名的所有解析记录列表
     */
    private DescribeSubDomainRecordsResponse describeSubDomainRecords(DescribeSubDomainRecordsRequest request, IAcsClient client) {
        try {
            // 调用SDK发送请求
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            // 发生调用错误，抛出运行时异常
            throw new RuntimeException();
        }
    }

    /**
     * 获取当前主机公网IP
     */
    private String getCurrentHostIP() {
        // 接口返回结果
        String result = "";
        BufferedReader in = null;
        try {
            // 使用HttpURLConnection网络请求第三方接口
            URL url = new URL(DnsConstant.URL_JSON_IP_6);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        Pattern pat = Pattern.compile(DnsConstant.REGEX_IP_6);
        Matcher mat = pat.matcher(result);
        String res = "";
        while (mat.find()) {
            res = mat.group();
            break;
        }
        return res;
    }

    /**
     * 修改解析记录
     */
    private UpdateDomainRecordResponse updateDomainRecord(UpdateDomainRecordRequest request, IAcsClient client) {
        try {
            //  调用SDK发送请求
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
            //  发生调用错误，抛出运行时异常
            throw new RuntimeException();
        }
    }

    private void doRefresh() {
        String accessKeyId = System.getProperty("accessKeyId");
        String secret = System.getProperty("secret");
        //  设置鉴权参数，初始化客户端
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        DDNS ddns = new DDNS();
        //查询指定二级域名的最新解析记录
        DescribeSubDomainRecordsRequest describeSubDomainRecordsRequest = new DescribeSubDomainRecordsRequest();
        describeSubDomainRecordsRequest.setSubDomain("nas.oobss.com");
        DescribeSubDomainRecordsResponse describeSubDomainRecordsResponse = ddns.describeSubDomainRecords(describeSubDomainRecordsRequest, client);
        List<DescribeSubDomainRecordsResponse.Record> domainRecords = describeSubDomainRecordsResponse.getDomainRecords();
        //最新的一条解析记录
        if (domainRecords.size() != 0) {
            DescribeSubDomainRecordsResponse.Record record = domainRecords.get(0);
            //  记录ID
            String recordId = record.getRecordId();
            //  记录值
            String recordsValue = record.getValue();
            //  当前主机公网IP
            String currentHostIP = ddns.getCurrentHostIP();
            System.out.println("-------------------------------当前主机公网IP为：" + currentHostIP + "-------------------------------");
            if (!currentHostIP.equals(recordsValue)) {
                //  修改解析记录
                UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
                //  主机记录
                updateDomainRecordRequest.setRR("nas");
                //  记录ID
                updateDomainRecordRequest.setRecordId(recordId);
                //  将主机记录值改为当前主机IP
                updateDomainRecordRequest.setValue(currentHostIP);
                //  解析记录类型
                updateDomainRecordRequest.setType("AAAA");
                UpdateDomainRecordResponse updateDomainRecordResponse = ddns.updateDomainRecord(updateDomainRecordRequest, client);
                System.out.println(updateDomainRecordResponse.getRecordId());
            }
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        doRefresh();
    }

}