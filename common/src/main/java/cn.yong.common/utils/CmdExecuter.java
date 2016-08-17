package cn.yong.common.utils;

import java.io.InputStream;

/**
 *  利用Process 调用shell或者cmd 读取console 内容
 */
public class CmdExecuter {


    public static ExecResult exec(String[] cmdarray) throws Exception {
        Process process = Runtime.getRuntime().exec(cmdarray);
        process.waitFor();
        return new ExecResult(process.getInputStream(), process.getErrorStream());
    }

    static class ExecResult {
        public InputStream in, err;

        public ExecResult(InputStream in, InputStream err) {
            this.in = in;
            this.err = err;
        }
    }
}
