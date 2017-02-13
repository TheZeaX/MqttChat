package net.ddns.thezeax.mqttchat;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutostartReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mqttCallbackService = new Intent(context, MqttCallbackService.class);
        context.startService(mqttCallbackService);
    }
}
