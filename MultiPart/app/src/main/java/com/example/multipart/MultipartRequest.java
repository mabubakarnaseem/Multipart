package com.example.multipart;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import androidx.annotation.Nullable;

public class MultipartRequest extends Request<NetworkResponse> {

    private Response.Listener<NetworkResponse> mListener;
    private Response.ErrorListener mErrorListener;


    public MultipartRequest(String url, Response.ErrorListener listener) {
        super(url, listener);
    }

    public MultipartRequest(int method, String url, Response.Listener<NetworkResponse> listener,@Nullable Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }
}