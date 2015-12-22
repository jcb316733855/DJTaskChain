package com.danny.common.taskchain;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor implements Callback{

	protected static final String TAG = "TaskExecutor";
	private final Handler handler = new Handler(Looper.getMainLooper());
	private LinkedList<Task> taskList = new LinkedList<Task>();
	//单独的looper用来处理task
	private Looper taskLooper;
	private Handler taskHandler;
	
	
	public TaskExecutor() {
		super();
		HandlerThread thread = new HandlerThread(this.getClass().getSimpleName(), 
				Process.THREAD_PRIORITY_FOREGROUND + Process.THREAD_PRIORITY_LESS_FAVORABLE);
        thread.start();
        taskLooper = thread.getLooper();
        taskHandler = new Handler(taskLooper, this);
	}

	public synchronized TaskExecutor addTask(Task task){
		taskList.add(task);
		return this;
	}
	
	/**
	 * 取消执行，如果当前有任务正在执行，不会取消，后续任务会 被取消执行
	 */
	public synchronized void cancel(){
		taskList.clear();
	}
	
	/**
	 * 关闭任务执行
	 */
	private synchronized void close() {
		Log.w(TAG, "Task任务关闭");
		taskList.clear();
        taskLooper.quit();
        taskHandler = null;
    }
	
	public synchronized void execute(){
		if(!taskList.isEmpty()){
			Task task = taskList.poll();
			Message msg = Message.obtain();
			TaskMessage taskMessage = new TaskMessage();
			taskMessage.task = task;
			msg.obj = taskMessage;
			taskHandler.sendMessage(msg);
		}else{
			close();
		}
	}
	
	private void scheduleNext() {
		if(!taskList.isEmpty()){
			Task task = taskList.poll();
			Message msg = Message.obtain();
			TaskMessage taskMessage = new TaskMessage();
			taskMessage.task = task;
			msg.obj = taskMessage;
			taskHandler.sendMessage(msg);
		}else{
			close();
		}
	}
	

	private void runOnUIThread(final Object param, final Task task) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				boolean needScheduleNext = task.run();
				if(needScheduleNext){
					//为false取消后面的任务执行
					scheduleNext();
				}else{
					Log.w(TAG, "任务失败，取消后续任务");
				}
			}
		});
	}

	@Override
	public boolean handleMessage(Message msg) {
		TaskMessage taskMessage = (TaskMessage) msg.obj;
		if(taskMessage.task == null){
			close();
			return false;
		}
		if(taskMessage.task.taskType == TaskType.Thread_UI){
			runOnUIThread(taskMessage.params, taskMessage.task);
		}else if(taskMessage.task.taskType == TaskType.Thread_Work){
			runWorkThread(taskMessage.params, taskMessage.task);
		}else{
			boolean needScheduleNext = taskMessage.task.run();
			if(needScheduleNext){
				//为false取消后面的任务执行
				scheduleNext();
			}else{
				Log.w(TAG, "任务失败，取消后续任务");
			}
		}
		return false;
	}


	private void runWorkThread(final Object param, final Task task) {
		ExecutorService service = Executors.newSingleThreadExecutor();//简易版本，后续可添加配置线程池个数
		service.execute(new Runnable() {
			
			@Override
			public void run() {
				boolean needScheduleNext = task.run();
				if(needScheduleNext){
					//为false取消后面的任务执行
					scheduleNext();
				}else{
					Log.w(TAG, "任务失败，取消后续任务");
				}
			}
		});
	}
	

	private static class TaskMessage{
		Task task;
		Object params;
	}
}
