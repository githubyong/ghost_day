package cn.yong.common.utils;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  多线程批量测试代理 <br>
 *      常用代理网站:http://www.youdaili.net/
 *
 */
public class MutiThreadProxyTest {

    public static void main(String[] args) throws Exception {
        List<InetSocketAddress> addresses = fetchAddress("proxytest/20160817-3.txt");
        System.out.println(addresses);
        refreshProxy(addresses);
    }

    public static List<InetSocketAddress> fetchAddress(String pathName) throws Exception {
        Path path = Paths.get(pathName);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> anons = new ArrayList<>();//匿名
        List<InetSocketAddress> addresses = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("匿")) {
                String addr = line.substring(0, line.indexOf('@'));
                anons.add(addr);
                String[] arr = addr.split(":");
                addresses.add(new InetSocketAddress(arr[0], Integer.valueOf(arr[1])));
            }
        }
        return addresses;
    }


    private static List<InetSocketAddress> proxyList = new ArrayList<InetSocketAddress>();

    static final LinkedBlockingDeque<Runnable> deque = new LinkedBlockingDeque<Runnable>();
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(200, 500, 10, TimeUnit.SECONDS, deque);

    static synchronized void refreshProxy(List<InetSocketAddress> proxies) {
        if (executor == null || executor.isShutdown()) {
            executor = new ThreadPoolExecutor(200, 500, 10, TimeUnit.SECONDS, deque);
        }
        for (final InetSocketAddress proxy : proxies) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    boolean available = testProxyByUrlConnection(proxy);
                    if (available && !proxyList.contains(proxy)) {
                        proxyList.add(proxy);
                    }
                }
            });
        }
        new Thread(new ExcutorCloseRunnable()).start();//等待测试完成关闭线程池
    }

    /**
     * 监控excutor线程池，如果所有的连接测试都结束，关闭线程池
     */
    static class ExcutorCloseRunnable implements Runnable {
        @Override
        public void run() {
            while (executor.getActiveCount() > 0) {
                try {
                    System.out.println(executor + " availableProxy=" + proxyList.size());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
            System.out.println("shut down excutor ============================" + executor.toString() + " availableProxy=" + proxyList.size());
            System.out.println(proxyList);
        }
    }


    /**
     * 通过urlconnection 测试代理连接情况
     *
     * @param address
     * @return
     */
    public static boolean testProxyByUrlConnection(InetSocketAddress address) {
        try {
            String proxy_url = "http://apis.baidu.com/heweather/weather/free";
            URL url = new URL(proxy_url);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address.getAddress(), address.getPort()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            return 200 == connection.getResponseCode();
        } catch (Exception e) {
            return false;
        }
    }
}
