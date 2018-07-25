package android.com.myapplication;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class FTP {
    //服务器名
    private String hostName;
    //端口号
    private int serverPort;
    //用户名
    private String userName;
    //密码
    private String passWord;
    //ftp连接
    private FTPClient ftpClient;

    public FTP(){
        this.hostName = "192.168.88.17";
        this.serverPort = 21;
        this.userName = "xuzhenyue";
        this.passWord = "123456";
        this.ftpClient = new FTPClient();
    }

    /**
     * 上传单个文件.
     *
     * @param singleFile
     *            本地文件
     * @param remotePath
     *            FTP目录
     *            监听器
     * @throws IOException
     */
    public void uploadSingleFile(File singleFile, String remotePath
                                 ) throws Exception {

        // 上传之前初始化
        this.uploadBeforeOperate(remotePath);
        boolean flag;
        flag = uploadingSingle(singleFile);
        if(flag){
            Log.d("xuzhenyue","上传成功");
        }

        // 上传完成之后关闭连接
        this.uploadAfterOperate();
    }

    /**
     * 上传多个文件.
     *
     * @param fileList
     *            本地文件
     * @param remotePath
     *            FTP目录
     *            监听器
     * @throws IOException
     */
    public void uploadMultiFile(LinkedList<File> fileList, String remotePath
                                ) throws IOException {

        // 上传之前初始化
        this.uploadBeforeOperate(remotePath);

        boolean flag;
        Log.d("xuzhenyue","开始批量上传");
        for (File singleFile : fileList) {
            try {
                deleteSingleFile(remotePath + singleFile.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            flag = uploadingSingle(singleFile);
            if (flag) {

            } else {

            }
        }
        // 上传完成之后关闭连接
        this.uploadAfterOperate();
    }

    /**
     * 上传单个文件.
     *
     * @param localFile
     *            本地文件
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    private boolean uploadingSingle(File localFile
                                    ) throws IOException {
        boolean flag = true;
        // 不带进度的方式
        // 创建输入流
        InputStream inputStream = new FileInputStream(localFile);
        // 上传单个文件
        flag = ftpClient.storeFile(localFile.getName(), inputStream);
        //上传文件后删除本地文件
        if(flag) { ;
            deleteFile(localFile);
        }else{
        }
        // 关闭文件流
        inputStream.close();

        return flag;
    }
    /**
     * 上传文件之前初始化相关参数
     *
     * @param remotePath
     *            FTP目录
     *            监听器
     * @throws IOException
     */
    private void uploadBeforeOperate(String remotePath
                                     ) throws IOException {

        // 打开FTP服务
        try {
            this.openConnect();
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        // 设置模式
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
        // FTP下创建文件夹
        String directory = remotePath.substring(0,remotePath.lastIndexOf("/")+1);
        String[] pah = directory.split("/");
        // 分层创建目录
        for (String pa : pah) {
            System.out.println(pa);
            ftpClient.makeDirectory(pa);
            // 切到到对应目录
            ftpClient.changeWorkingDirectory(pa);
        }
        //ftpClient.makeDirectory(remotePath);
        // 改变FTP目录
        //ftpClient.changeWorkingDirectory(remotePath);
        // 上传单个文件

    }

    /**
     * 上传完成之后关闭连接
     *
     * @throws IOException
     */
    private void uploadAfterOperate()
            throws IOException {
        this.closeConnect();
    }

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    public void openConnect() throws IOException {
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName, serverPort);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        ftpClient.login(userName, passWord);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient
                    .getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        }
    }

    /**
     * 关闭FTP服务.
     *
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            // 退出FTP
            ftpClient.logout();
            // 断开连接
            ftpClient.disconnect();
        }
    }

    //删除上传后的本地文件
    public void deleteFile(File file){
        if(file.exists()){
            file.delete();
        }
    }
    /**
     * 删除Ftp下的文件.
     *
     * @param serverPath
     *            Ftp目录及文件路径
     * @throws IOException
     */
    public void deleteSingleFile(String serverPath)
            throws Exception {


        //String serverPath = "picture/2018_07_24/001/2018_07_24_18_19_33.jpg";
        // 打开FTP服务
        try {
            this.openConnect();
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        // 先判断服务器文件是否存在
        FTPFile[] files = ftpClient.listFiles(serverPath);
        if (files.length == 0) {
            return;
        }

        //进行删除操作
        boolean flag = true;
        flag = ftpClient.deleteFile(serverPath);
        if (flag) {
        } else {
        }

        // 删除完成之后关闭连接
        this.closeConnect();

        return;
    }

}
