package com.danny.common.taskchain;

public abstract class Task{
	public TaskType taskType = TaskType.Thread_UI;
	
	public Task(TaskType taskType) {
		super();
		this.taskType = taskType;
	}


	//执行的时候会要求传入上一条的执行结果
	protected abstract boolean run();

}
