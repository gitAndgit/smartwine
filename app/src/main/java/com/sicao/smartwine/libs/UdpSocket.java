package com.sicao.smartwine.libs;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UdpSocket {

    private static final boolean DEBUG = false;
    private static final String TAG = "UdpSocket";

	public static final int UDP_TIMOUT_MIN = 30;
	public static final int UDP_TIMOUT_MAX = 100;
	public static final int UDP_DATA_MAX = 1024;

	protected boolean mMulticast;

	protected int timeout;

	protected InetAddress mIpAddress;

	protected int mPort;

	protected DatagramSocket mSockect;

	protected DatagramPacket mPacket;

	protected byte[] mData;

	protected Map<String,String> mResult;

	public UdpSocket() {

	}

	public UdpSocket(String host, int port, String data)
			throws UnknownHostException, SocketException {
		this(host, port, data.getBytes());
	}

	public UdpSocket(String host, int port, byte[] data)
			throws UnknownHostException, SocketException {
		mSockect = new DatagramSocket();
		mIpAddress = InetAddress.getByName(host);
		mData = data;
		mPort = port;
		timeout = UDP_TIMOUT_MIN;
	}

	public UdpSocket(String host, int port, String data, boolean multicast)
			throws UnknownHostException, SocketException {
		this(host, port, data.getBytes(), multicast);
	}

	public UdpSocket(String host, int port, byte[] data, boolean multicast)
			throws UnknownHostException, SocketException {
		mSockect = new DatagramSocket();
		mIpAddress = InetAddress.getByName(host);
		mData = data;
		mPort = port;
		timeout = UDP_TIMOUT_MAX;
        mMulticast = multicast;
	}
	
	public void setTimeout(int timeout){
		this.timeout = timeout;
	}

	public void send() {
		try {
			DatagramPacket sendPocket = new DatagramPacket(mData, mData.length,
					mIpAddress, mPort);
			mResult = new HashMap<String, String>();
			mSockect.setSoTimeout(timeout);
            if (DEBUG) {
                Log.e("UdpSocket", "UdpSocket " + mMulticast);
            }
			if (mMulticast) {
				mSockect.send(sendPocket);
				mSockect.send(sendPocket);
                mSockect.send(sendPocket);
                if (DEBUG) {
                    Log.e("UdpSocket", "UdpSocket send 3 packet");
                }
			}else{
				mSockect.send(sendPocket);
			}
			do {
				byte[] reciveBuffer = new byte[UDP_DATA_MAX];
				DatagramPacket recvPacket = new DatagramPacket(reciveBuffer,
						UDP_DATA_MAX);
				mSockect.receive(recvPacket);
				String result = new String(recvPacket.getData(),
						recvPacket.getOffset(), recvPacket.getLength());
                if(DEBUG) {
                    Log.e("UdpSocket", "UdpSocket recev:" + result);
                }
				mResult.put(recvPacket.getAddress().getHostName(), result);
			} while (mMulticast);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Log.e("UdpSocket", e.p);
            if (DEBUG) {
                Log.e("UdpSocket", "UdpSocket +" + timeout + " ms timeout");
            }
		} finally {
			if (!mSockect.isClosed()) {
				mSockect.close();
			}
		}

	}

	public Collection<String> getResult() {
		return mResult.values();
	}
	
	public Map<String,String> getResults(){
		return mResult;
	}

}
