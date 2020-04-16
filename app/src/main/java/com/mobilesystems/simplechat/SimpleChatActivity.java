package com.mobilesystems.simplechat;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.w3c.dom.Text;

import android.icu.util.BuddhistCalendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import static android.widget.TextView.*;

public class SimpleChatActivity extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ListView chatListView;
    EditText msgEditText;
    Handler handler;
    String nick;
    String ip;
    MessageAdapter adapter = null;
    MqttClient client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_chat);

        nick = getIntent().getStringExtra(MainActivity.NICK);
        ip = getIntent().getStringExtra(MainActivity.IP);

        chatListView = findViewById(R.id.chatListView);
        msgEditText = findViewById(R.id.msgEditText);
        TextView txt = findViewById(R.id.connectedAs);
        txt.setText("Connected as " + nick + ".");
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                startMQTT();
            }
        }).start();

        msgEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    sendOnClick(msgEditText);
                    handled = true;

                }
                return handled;
            }
        });

        adapter = new MessageAdapter(this);
        chatListView.setAdapter(adapter);

        handler = new MsgHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(client != null) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendOnClick(View view) {
        try {
            if(client == null) {
                System.out.println("client is null");
                return;
            }
            client.publish(nick, msgEditText.getText().toString().getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        msgEditText.getText().clear();
    }


    private static class MsgHandler extends Handler {
        private final WeakReference<SimpleChatActivity> sActivity;
        MsgHandler(SimpleChatActivity activity){
            sActivity = new WeakReference<SimpleChatActivity>(activity);
        }
        public void handleMessage(Message msg) {
            SimpleChatActivity activity = sActivity.get();
            MemberData memberData = new MemberData(msg.getData().getString("NICK"), getRandomColor());
            CustomMessage customMessage = new CustomMessage(msg.getData().getString("MSG"), memberData,
                    memberData.getName().equals(activity.nick));
            activity.adapter.add(customMessage);
        }
    }

    private static String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    private void startMQTT() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String broker = "tcp://" + ip + ":1883";
            String clientId = nick;

            client = new MqttClient(broker, clientId, persistence);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Message msg = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK", topic);
                    b.putString("MSG", message.toString());
                    msg.setData(b);
                    handler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            client.connect(options);
            client.subscribe("#");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
