package net.ddns.thezeax.mqttchat;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button submitButton;
    TextView displayText;
    EditText inputText;

    BroadcastReceiver receiver;

    /*MqttAndroidClient client;
    MemoryPersistence memPer = new MemoryPersistence();
    static String MQTTBROKER = "tcp://thezeax.ddns.net:1883";
    static String USERNAME = "android";
    static String PASSWORD = "android";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitButton = (Button) findViewById(R.id.submitButton);
        displayText = (TextView) findViewById(R.id.displayText);
        inputText = (EditText) findViewById(R.id.inputText);

        Intent i = new Intent(MainActivity.this, MqttCallbackService.class);
        startService(i);

        /*receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra("nachricht");
                displayText.setText(s);
                // do something here.
            }
        };*/

        //mqttConnect();
    }

    public void mqttConnect() {
        /*String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(MainActivity.this, MQTTBROKER, clientId, memPer);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        options.setCleanSession(true);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    subChannel("mqttchat/chat");
                    subChannel("mqttchat/version/number");
                    //subChannel("dev/version/notes");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Connection failed :(", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;

                if(topic.equals("mqttchat/version/number")) {
                    String ver = new String(message.getPayload());
                    if(!version.equals(ver)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(ver).setTitle("You are on " + version + ". Get the latest version from TheZeaX.ddns.net");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else if(topic.equals("mqttchat/chat")) {

                    String msg = new String(message.getPayload());
                    displayText.setText(displayText.getText().toString() + "\n" + msg);

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this);
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
    }*/

    /*public void pubMessage(View view) {
        String topic = "mqttchat/chat";
        String message = inputText.getText().toString();
        if(!message.equals("")) {
            try {
                client.publish(topic, message.getBytes(), 1, false);
                inputText.setText("");
            } catch(MqttException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, "Please enter something first!", Toast.LENGTH_SHORT).show();
        }
    }*/
    }
}
