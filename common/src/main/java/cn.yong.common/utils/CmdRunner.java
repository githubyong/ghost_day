package cn.yong.common.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 执行cmd 或者shell  用 线程获取输出流
 */
public class CmdRunner {


    static void runCmd(String[] cmdarray) throws Exception {
        try {
            Process process = Runtime.getRuntime().exec(cmdarray);
            Thread inThread = new StreamHandler(process.getInputStream());
            Thread errThread = new StreamHandler(process.getErrorStream());
            inThread.start();
            errThread.start();
            process.waitFor();
            inThread.join();
            errThread.join();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    static class StreamHandler extends Thread {
        private InputStream in;

        public StreamHandler(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            super.run();
            try {
                int n;
                byte[] buffer = new byte[4096];
                StringBuffer sb = new StringBuffer();
                while ((n = in.read(buffer)) != -1) {
                    sb.append(new String(buffer, 0, n));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
