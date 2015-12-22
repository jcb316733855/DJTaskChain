# DJTaskChain
An enhanced version of the AsyncTask for android,you can specify the ui thread and worker thread

## Usage
HashMap<String, String> params = new HashMap<String, String>();
params.put("username", "admin");
params.put("password", "admin");
params.put("action", "login");
//��ӵ�����Task����˳��ִ�У�����ָ��TaskҪ������UI/Work�߳�
new TaskExecutor().addTask(new NetWorkTask(this, "http://112.74.203.90/WebServer/tie.ashx", params, new NetWorkTask.INetWorkTaskCallback() {
			
			@Override
			public void onTaskFinish(String response) {
				Log.e(TAG, "TASK result1:" + response);
			}
		}))
     .addTask(new Task(TaskType.Thread_UI) {//ָ������UI�߳�
			
			@Override
			protected boolean run() {//
				Log.w(TAG, "TASK ui 1>>");
				tv.setText("--");
				return true;
			}
		}).addTask(new Task(TaskType.Thread_Work) {//ָ������Work�߳�
			
			@Override
			protected boolean run() {
				Log.w(TAG, "TASK work 2>>");
				return true;
			}
		})
    .execute();
    
    
//����֮ǰҲ����ָ��TaskGroup,TaskGroup�е���Work Task�Ტ��ִ��
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

new TaskExecutor().addTask(group).execute();