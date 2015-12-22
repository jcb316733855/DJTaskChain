package com.danny.commom.taskchain;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.danny.common.taskchain.NetWorkTask;
import com.danny.common.taskchain.Task;
import com.danny.common.taskchain.TaskExecutor;
import com.danny.common.taskchain.TaskGroup;
import com.danny.common.taskchain.TaskType;

import java.util.HashMap;

public class MainActivity extends Activity {
    protected static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        executeTaskChain();
    }

    private String result;
    private void executeTaskChain() {
        final TextView tv = (TextView) findViewById(R.id.tv);

        TaskGroup group = new TaskGroup();
        group.addTask(new Task(TaskType.Thread_UI) {

            @Override
            protected boolean run() {
                Log.w(TAG, "TASK GROUP UI 1" + Thread.currentThread());
                // TODO Auto-generated method stub
                return true;
            }
        }).addTask(new Task(TaskType.Thread_Work) {

            @Override
            protected boolean run() {
                Log.w(TAG, "TASK GROUP WORK 2"  + Thread.currentThread());
                return true;
            }
        }).addTask(new Task(TaskType.Thread_Work) {

            @Override
            protected boolean run() {
                Log.w(TAG, "TASK GROUP WORK 3" + Thread.currentThread());
                return true;

            }
        }).addTask(new Task(TaskType.Thread_Work) {

            @Override
            protected boolean run() {
                Log.w(TAG, "TASK GROUP WORK 4" + Thread.currentThread());
                return true;

            }
        });

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", "admin");
        params.put("password", "admin");
        params.put("action", "login");
        new TaskExecutor().addTask(new NetWorkTask(this, "http://112.74.203.90/WebServer/tie.ashx", params, new NetWorkTask.INetWorkTaskCallback() {

            @Override
            public void onTaskFinish(String response) {
                Log.e(TAG, "TASK result1:" + response);
                result = response;
            }
        }))
                .addTask(new Task(TaskType.Thread_UI) {

                    @Override
                    protected boolean run() {
                        Log.w(TAG, "TASK ui 1>>");

                        return true;
                    }
                }).addTask(new Task(TaskType.Thread_Work) {

            @Override
            protected boolean run() {
                Log.w(TAG, "TASK work 2>>");
                return true;
            }
        }).addTask(group)
                .addTask(new Task(TaskType.Thread_Work) {

                    @Override
                    protected boolean run() {
                        Log.w(TAG, "TASK work 3>>");
                        return true;
                    }
                })
                .addTask(new Task(TaskType.Thread_UI) {

                    @Override
                    protected boolean run() {
                        Log.w(TAG, "TASK ui 4>>");
                        tv.setText(result);
                        return true;

                    }
                })
                .execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
