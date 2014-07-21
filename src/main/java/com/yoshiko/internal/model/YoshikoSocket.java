package com.yoshiko.internal.model;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class YoshikoSocket {
	private Socket socket = null;
	private BufferedInputStream dm = null;
	private DataInputStream din = null;
	private DataOutputStream dout = null;
	private readThread readth = null;
	private boolean getflag = false;
	private int getdata = 0;
	private String outdata = null;

	public YoshikoSocket() {
	}

	public void connect(final String host, final int port) throws UnknownHostException, IOException {
		int timeout = 100;
		socket = new Socket();
		do {
			socket.connect(new InetSocketAddress(InetAddress.getByName(host), port), 500);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(timeout > 1) timeout--;
			else return;
		}while(!socket.isConnected());
		dm = new BufferedInputStream(socket.getInputStream());
		din = new DataInputStream(dm);
		dout = new DataOutputStream(socket.getOutputStream());
		readth = new readThread();
		readth.start();
	}

	public void disconnect() throws IOException {
		if (readth != null)
			readth.close();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (din != null) {
			din.close();
			din = null;
		}
		if (dm != null) {
			dm.close();
			dm = null;
		}
		if (dout != null) {
			dout.close();
			dout = null;
		}
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

	public boolean SendCommand(int type, String name, String payload)
			throws IOException {
		getflag = false;
		byte[] payloaddata = payload.getBytes();
		byte[] namedata = name.getBytes();
		int namelen = namedata.length;
		int payloadlen = payloaddata.length;
		dout.write((byte) type);
		dout.writeInt(namelen);
		if (namelen > 0) {
			dout.write(namedata);
		}
		dout.writeInt(payloadlen);
		if (payloadlen > 0) {
			dout.write(payloaddata);
		}
		dout.flush();
		while (getflag == false)
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (getdata == 8)
			return true;
		else
			return false;
	}

	public String getOutData() {
		return outdata;
	}

	private class readThread extends Thread {
		private boolean running = true;
		private byte[] read_buffer = new byte[1];
		private int count = 0;

		public void run() {
			while (running) {
				if (din != null) {
					try {
						count = din.read(read_buffer, 0, 1);
						if (count > 0) {
							getdata = read_buffer[0];
							int len = din.readInt();
							if (len > 0) {
								byte[] buffer = new byte[len];
								din.read(buffer, 0, len);
								outdata = new String(buffer);
							}
							getflag = true;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void close() {
			running = false;
		}
	}
}