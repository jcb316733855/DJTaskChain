package com.danny.common.taskchain;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 网络Task,默认POST请求
 * 
 * @Description    
 * @Creator  danny_jiang  
 * @CreatTime  2015-12-21 下午2:15:59
 *
 */
public class NetWorkTask extends Task {

	private int method = Method.POST;
	private final String host;

	private final HashMap<String, String> params;

	private final INetWorkTaskCallback netWorkTaskCallback;

	private Context context;

	public NetWorkTask(Context context, String host, HashMap<String, String> params,
			INetWorkTaskCallback netWorkTaskCallback) {
		super(TaskType.Thread_Work);
		this.context = context;
		this.host = host;
		this.params = params;
		this.netWorkTaskCallback = netWorkTaskCallback;
	}
	
	public NetWorkTask(Context context, int method, String host, HashMap<String, String> params,
			INetWorkTaskCallback netWorkTaskCallback) {
		super(TaskType.Thread_Work);
		this.method = method;
		this.context = context;
		this.host = host;
		this.params = params;
		this.netWorkTaskCallback = netWorkTaskCallback;
	}
	
	

	public interface INetWorkTaskCallback {
		void onTaskFinish(String response);
	}

	@Override
	protected boolean run() {
		RequestFuture<String> future = RequestFuture.newFuture();
		StringRequest request = new StringRequest(method, host, future, future){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return params;
			}
		};
		Volley.newRequestQueue(context).add(request);
		try {
			String result = future.get();
			Log.e("danny", "volley result:" + result);
			if (netWorkTaskCallback != null) {
				netWorkTaskCallback.onTaskFinish(result);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return true;
	}
}
