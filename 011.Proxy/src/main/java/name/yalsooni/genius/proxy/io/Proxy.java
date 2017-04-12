package name.yalsooni.genius.proxy.io;

import name.yalsooni.genius.proxy.definition.Code;
import name.yalsooni.genius.proxy.exception.ClientIOException;
import name.yalsooni.genius.proxy.process.DataPassWorker;
import name.yalsooni.genius.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 프록시
 * Created by ijyoon on 2017. 4. 12..
 */
public class Proxy {

    private ServerSocket serverSocket;
    private ThreadPoolExecutor threadPoolExecutor;

    private int sourcePort;
    private String targetIP;
    private int targetPort;

    private boolean running = true;

    public Proxy(int sourcePort, String targetIP, int targetPort){
        this.sourcePort = sourcePort;
        this.targetIP = targetIP;
        this.targetPort = targetPort;
    }

    /**
     * 초기화
     * @throws Exception
     */
    public void initialize() throws Exception{
        try {
            serverSocket = new ServerSocket(sourcePort);
            Log.console("Listening Port : " + sourcePort);
        } catch (IOException e) {
            Log.console(Code.G_011_0001, e);
        }

        threadPoolExecutor = new ThreadPoolExecutor(10,100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * 실행
     */
    public void execute(){

        Socket socket = null;

        while(running){
            try {
                socket = serverSocket.accept();
                Log.console("Accept Socket : " + socket.getLocalAddress());
                threadPoolExecutor.execute(new DataPassWorker(socket, targetIP, targetPort));
            } catch (ClientIOException cioe){
                Log.console(Code.G_011_0002, cioe);
            } catch (IOException e) {
                Log.console(Code.G_011_0003, e);
            }
        }
    }
}