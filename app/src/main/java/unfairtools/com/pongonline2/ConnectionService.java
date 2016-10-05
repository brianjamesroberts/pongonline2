package unfairtools.com.pongonline2;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class ConnectionService extends Service {
//    @Inject
//    Application mApp;



    private static String ServerIP = "158.69.207.153";
    private static int UDPPort = 8086;
    private static int TSLPort = 8085;

    SocketFactory mSocketFactory;

    SSLSocket s1;
    DatagramSocket d1;

    PrintWriter sockey;

    Resources resources;


    static Charset latin1Charset = Charset.forName("ISO-8859-1");


    private final IBinder mBinder = new LocalBinder();


    public String readTSLLine() {
        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            String s = buf.readLine();
            //buf.close();
            //Log.e("read:",s);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return "";
        }

    }

    public String readUDP(){
        try{
            if(d1.getReceiveBufferSize()==0){
                return "nothing here " + d1.getReceiveBufferSize() + "#";
            }
            byte[] buffer = new byte[1000];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            d1.receive(packet);
            return new String(packet.getData(),0,packet.getLength(),latin1Charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void sendUDP(String s){
        try {
            DatagramPacket dp;
            dp = new DatagramPacket(s.getBytes(), s.length());
            d1.send(dp);
        }catch (java.net.SocketException e){
            e.printStackTrace();
        }catch(java.io.IOException e){
            e.printStackTrace();
        }
    }

    public void sendTSL(String s){
        try {
            if(sockey==null)
                sockey = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s1.getOutputStream())), true);
            sockey.println(s);
            sockey.flush();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class InitUDPRunnable implements Runnable{
        public synchronized void run2(){
            try {
                d1 = new DatagramSocket();
                d1.connect(InetAddress.getByName(ServerIP), UDPPort);
            }catch(Exception e){
                e.printStackTrace();
            }finally {
                notify();
            }
        }
        public void run(){
            run2();
        }
    }

    class InitTSLRunnable implements Runnable{
        public synchronized void run2(){
            try {
                s1 = (SSLSocket) mSocketFactory.createSocket(ServerIP, TSLPort);
                s1.startHandshake();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                notify();
            }
        }
        public void run() {
            run2();
        }
    }


    public boolean getConnectionUDP(){
        InitUDPRunnable r = new InitUDPRunnable();
        Thread t = new Thread(r);
        try{
            synchronized (r) {
                t.start();
                r.wait();
                return d1.isConnected();
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }



    }

    public boolean getConnectionTSL(){
        InitTSLRunnable r = new InitTSLRunnable();
        Thread t = new Thread(r);
        try {
            synchronized (r) {
                t.start();
                r.wait();
                return s1.isConnected();
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public class LocalBinder extends Binder {
        ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    public void killWriter(){
        try {
            if(sockey!=null)
                sockey.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void initVals(Resources r){
        resources = r;
    }


    public void initConnectionService() {
        Log.i("ConnectionService", "intiConnectionService()");
        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            KeyStore ks = KeyStore.getInstance("BKS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            ks.load(resources.openRawResource(R.raw.mykeystore), "mysecret".toCharArray());
            trustManagerFactory.init(ks);
            sslcontext.init(null, trustManagerFactory.getTrustManagers(), null);
            mSocketFactory = sslcontext.getSocketFactory();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        //((AppModule) getApplication()).inject(this);

        return mBinder;
    }
}
