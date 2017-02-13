package net.ddns.thezeax.mqttchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {
    Button submitButton;
    TextView displayText;
    EditText inputText;

    MqttAndroidClient client;
    MemoryPersistence memPer = new MemoryPersistence();
    static String MQTTBROKER = "tcp://thezeax.ddns.net:1883";
    static String USERNAME = "android";
    static String PASSWORD = "android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitButton = (Button) findViewById(R.id.submitButton);
        displayText = (TextView) findViewById(R.id.displayText);
        inputText = (EditText) findViewById(R.id.inputText);

        mqttConnect();
    }

    public void mqttConnect() {
        String clientId = MqttClient.generateClientId();
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
                    subChannel("dev/test");
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
                String msg = new String(message.getPayload());
                displayText.setText(displayText.getText().toString()+"\n"+msg);


                /*Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivities(MainActivity.this, 0,intent, PendingIntent.FLAG_ONE_SHOT);*/

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

    public void pubMessage(View view) {
        String topic = "dev/test";
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
    }
}
