package net.ddns.thezeax.mqttchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttCallbackService extends Service {
    MqttAndroidClient client;
    MemoryPersistence memPer = new MemoryPersistence();
    static String MQTTBROKER = "tcp://thezeax.ddns.net:1883";
    static String USERNAME = "android";
    static String PASSWORD = "android";
    LocalBroadcastManager broadcaster;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            //myStuff
            mqttConnect();

            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        //LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);

        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public void mqttConnect() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this, MQTTBROKER, clientId, memPer);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        options.setCleanSession(true);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    subChannel("mqttchat/chat");
                    subChannel("mqttchat/version/number");
                    //subChannel("dev/version/notes");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getApplicationContext(), "Connection failed :(", Toast.LENGTH_SHORT).show();

                    /*AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext())
                            .setTitle("MqttChat Info")
                            .setMessage("Connection failed :(")
                            .create();

                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    alertDialog.show();*/

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(getApplicationContext(), "Connection lost...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;

                if(topic.equals("mqttchat/version/number")) {
                    String ver = new String(message.getPayload());
                    if(!version.equals(ver)) {
                        Toast.makeText(getApplicationContext(), "update", Toast.LENGTH_SHORT).show();
                        /*AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage(ver).setTitle("You are on " + version + ". Get the latest version from TheZeaX.ddns.net");
                        AlertDialog dialog = builder.create();
                        dialog.show();*/
                    }
                } else if(topic.equals("mqttchat/chat")) {

                    String msg = new String(message.getPayload());
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    //displayText.setText(displayText.getText().toString() + "\n" + msg);

                    //sendResult(msg);


                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplication());
                    notificationBuilder.setContentTitle("MqttChat");
                    notificationBuilder.setContentText(msg);
                    notificationBuilder.setAutoCancel(true);
                    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    notificationBuilder.setOnlyAlertOnce(true);
                    notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                    notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
                    notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void subChannel(String topic) {
        try {
            client.subscribe(topic, 1);
        }catch(MqttException e) {
            e.printStackTrace();
        }
    }

    /*public void sendResult(String msg) {
        Intent intent = new Intent("hi");
        if(msg != null) {
            intent.putExtra("nachricht", msg);
            broadcaster.sendBroadcast(intent);
        }
    }*/
}
