package com.android.ketaoapp.util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.entity.StringEntity;

public class HTTPRequestUtil {

	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public static void post(Context context, String url, StringEntity entity, String contentType,
			JsonHttpResponseHandler responseHandler) {
		client.post(context, url, entity, contentType, responseHandler);
	}

	public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}

	public static void get(String url, JsonHttpResponseHandler responseHandler){
		client.get(url, responseHandler);
	}
}
