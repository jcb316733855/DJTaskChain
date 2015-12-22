package com.danny.common.taskchain;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UI可以与Work线程并行运行，多个WORK线程也是并发执行的，但是UI线程如果有多个是顺序执行
 * @author z672693
 *
 */
public class TaskGroup extends Task{

	protected static final String TAG = "TaskGroup";

	private AtomicInteger resultCounter = new AtomicInteger();
	private AtomicInteger resultFailCounter = new AtomicInteger(0);
	private Object lock = new Object();

	public TaskGroup() {
		super(TaskType.Thread_GROUP);
	}

	@Override
	protected boolean run() {
		synchronized (lock) {
			if(taskList.isEmpty()){
				return needScheduleNext();
			}
			final int size = taskList.size();
			ExecutorService service = Executors.newFixedThreadPool(size);
			while (true) {
				final Task task = taskList.poll();
				if(task == null){
					try {
						lock.wait();
						Log.w(TAG, "组任务全部完成");
						return needScheduleNext();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return needScheduleNext();
					}
				}
				if(task.taskType == TaskType.Thread_UI){
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							synchronized (lock) {
								boolean needScheduleNext = task.run();
								int count = resultCounter.incrementAndGet();
								if(count == size){
									lock.notify();
								}
								if(needScheduleNext){
									
								}else{
									resultFailCounter.incrementAndGet();
									Log.w(TAG, "任务失败，取消后续任务");
								}
							}
						}
					});
				}else if(task.taskType == TaskType.Thread_Work){
					service.execute(new Runnable() {
						
						@Override
						public void run() {
							synchronized (lock) {
								boolean needScheduleNext = task.run();
								int count = resultCounter.incrementAndGet();
								if(count == size){
									lock.notify();
								}
								if(needScheduleNext){
									
								}else{
									resultFailCounter.incrementAndGet();
									Log.w(TAG, "任务失败，取消后续任务");
								}
							}
						}
					});
				}
			}
		}
	}

	protected boolean needScheduleNext() {//计划以此来取消后续的任务执行，若为false的话,后续任务不会再执行
		return resultFailCounter.get() == 0;
	}
	
	private final Handler handler = new Handler(Looper.getMainLooper());
	private LinkedList<Task> taskList = new LinkedList<Task>();
	

	public TaskGroup addTask(Task task){
		synchronized (lock) {
			taskList.add(task);
		}
		return this;
	}


}
