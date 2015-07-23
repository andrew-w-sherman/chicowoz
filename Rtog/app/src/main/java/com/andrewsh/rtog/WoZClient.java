package com.andrewsh.rtog;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.io.BufferedWriter;

/**
 * Created by Andrew on 7/22/15.
 */
public class WoZClient {
    private static final boolean DEBUG = true;
    private String ip;
    private static final String DEFAULT_ADDR = "wall-e.hcii.cs.cmu.edu";
    private static final String DEFAULT_PORT = "11111";
    private int port;
    SharedPreferences prefs;

    public WoZClient(SharedPreferences preferences) {
        ip = DEFAULT_ADDR;
        port = Integer.parseInt(DEFAULT_PORT);
        prefs = preferences;
    }

    public void sendCommand(final String cmd) {
        if(DEBUG) {
            Log.d("Network", "Sent message: " + cmd);
            return;
        }
        new AsyncTask<String, Void, Exception>() {
            @Override protected Exception doInBackground(String... params) {
                Exception result = null;
                ip = prefs.getString("pref_ip", DEFAULT_ADDR);
                port = Integer.parseInt(prefs.getString("pref_port", DEFAULT_PORT));
                try {
                    InetAddress serverAdd = InetAddress.getByName(ip);
                    Log.d("Network", " Attempting to connect to " + serverAdd);
                    SocketAddress endPoint = new InetSocketAddress(serverAdd, port);
                    Socket socket = new Socket();
                    socket.connect(endPoint, 5000);
                    Log.d("Network", "Connected!");
                    BufferedWriter bw = new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream()));
                    bw.write(cmd + "<EOF>");
                    bw.flush();
                    socket.close();
                } catch (Exception e) {
                    Log.e("Network", "Error", e);
                    result = e;
                }
                return result;
            }
            @Override protected void onPostExecute(Exception result) {
                // TODO: show toast message for failure to connect
            }
        }.execute();
    }
}
