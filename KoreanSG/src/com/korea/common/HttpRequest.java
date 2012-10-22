package com.korea.common;
import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.webkit.MimeTypeMap;

public class HttpRequest {

	/**
	 * HttpGet request
	 *
	 * @param sUrl
	 * @return
	 */

	public static HttpData get(String sUrl) {

		HttpData ret = new HttpData();

		String str;

		StringBuffer buff = new StringBuffer();

		try {

			URL url = new URL(sUrl);

			URLConnection con = url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

			while ((str = in.readLine()) != null) {
				buff.append(str);
			}

			ret.content = buff.toString();
			//get headers

			Map<String, List<String>> headers = con.getHeaderFields();
			Set<Entry<String, List<String>>> hKeys = headers.entrySet();

			for (Iterator<Entry<String, List<String>>> i = hKeys.iterator(); i.hasNext();) {

				Entry<String, List<String>> m = i.next();
				System.out.println("HEADER_KEY:" + m.getKey() + "");
				ret.headers.put(m.getKey(), m.getValue().toString());
				if (m.getKey().equals("set-cookie"))
					ret.cookies.put(m.getKey(), m.getValue().toString());

			}

		} catch (Exception e) {

			System.out.println( e.getMessage() );

		}

		return ret;

	}


	/**
	 * HTTP post request
	 *
	 * @param sUrl
	 * @param ht
	 * @return
	 * @throws Exception
	 */

	public static HttpData post(String sUrl, Hashtable<String, String> ht) throws Exception {

		String key;

		StringBuffer data = new StringBuffer();

		Enumeration<String> keys = ht.keys();

		while (keys.hasMoreElements()) {

			key = keys.nextElement();

			data.append(URLEncoder.encode(key, "UTF-8"));

			data.append("=");

			data.append(URLEncoder.encode(ht.get(key), "UTF-8"));

			data.append("&amp;");

		}

		return HttpRequest.post(sUrl, data.toString());

	}

	/**
	 * HTTP post request
	 *
	 * @param sUrl
	 * @param data
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public static HttpData post(String sUrl, String data) {

		StringBuffer ret = new StringBuffer();

		HttpData dat = new HttpData();

		String header;

		try {

			// Send data
			URL url = new URL(sUrl);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			Map<String, List<String>> headers = conn.getHeaderFields();
			Set<Entry<String, List<String>>> hKeys = headers.entrySet();

			for (Iterator<Entry<String, List<String>>> i = hKeys.iterator(); i.hasNext();) {

				Entry<String, List<String>> m = i.next();
				dat.headers.put(m.getKey(), m.getValue().toString());
				if (m.getKey().equals("set-cookie"))
					dat.cookies.put(m.getKey(), m.getValue().toString());

			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			String line;

			while ((line = rd.readLine()) != null) {

				ret.append(line);

			}

			wr.close();
			rd.close();

		} catch (Exception e) {

			System.out.println("ERROR:" + "ERROR IN CODE:"+e.getMessage());

		}

		dat.content = ret.toString();

		return dat;
	}

	/**
	 * Post request (upload files)
	 * @param sUrl
	 * @param files
	 * @return HttpData
	 */

	public static HttpData post(String sUrl, ArrayList<File> files) throws Exception

	{
		Hashtable<String, String> ht = new Hashtable<String, String>();
		return HttpRequest.post(sUrl, ht, files);
	}

	/**
	 * Post request (upload files)
	 * @param sUrl
	 * @param params Form data
	 * @param files
	 * @return
	 */

	public static HttpData post(String sUrl, Hashtable<String, String> params, ArrayList<File> files) throws Exception 
	{

		HttpData ret = new HttpData();
		HttpURLConnection con = null;
		DataOutputStream dos = null;
		FileInputStream fis = null;
		BufferedReader rd = null;

		try {

			String boundary = "*****************************************";

			String newLine = "\r\n";

			int bytesAvailable;

			int bufferSize;

			int maxBufferSize = 4096;

			int bytesRead;

			URL url = new URL(sUrl);

			con = (HttpURLConnection) url.openConnection();

			con.setDoInput(true);

			con.setDoOutput(true);

			con.setUseCaches(false);

			con.setRequestMethod("POST");

			con.setRequestProperty("Connection", "Keep-Alive");

			con.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

			dos = new DataOutputStream(con.getOutputStream());

			//upload files

			for (int i=0; i<files.size(); i++) {

				System.out.println("HREQ:" + i+"");

				fis = new FileInputStream(files.get(i));

				System.out.println("--" + boundary + newLine);
				dos.writeBytes("--" + boundary + newLine);

				String extension = MimeTypeMap.getFileExtensionFromUrl( files.get(i).getPath() );
				String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
				
				dos.writeBytes("Content-Type:" + mimeType + newLine );
				dos.writeBytes("Content-Disposition: form-data; "
                            + "name=image[];filename="
                            + files.get(i).getPath() + "" + newLine + newLine);

				bytesAvailable = fis.available();

				bufferSize = Math.min(bytesAvailable, maxBufferSize);

				byte[] buffer = new byte[bufferSize];

				bytesRead = fis.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);

					bytesAvailable = fis.available();

					bufferSize = Math.min(bytesAvailable, maxBufferSize);

					bytesRead = fis.read(buffer, 0, bufferSize);

				}

				dos.writeBytes(newLine);

				dos.writeBytes("--" + boundary + "--" + newLine);

				fis.close();

			}

			// Now write the data
			Enumeration keys = params.keys();

			String key, val;

			while (keys.hasMoreElements()) {

				key = keys.nextElement().toString();

				val = params.get(key);

				dos.writeBytes("--" + boundary + newLine);
				dos.writeBytes("Content-Disposition: form-data;name="
                            + key+ "" + newLine + newLine + val);
				dos.writeBytes(newLine);
				dos.writeBytes("--" + boundary + "--" + newLine);

			}

			dos.flush();

			rd = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "UTF-8"));

			String line = "";

			while ((line = rd.readLine()) != null) {
				ret.content += line + newLine;
			}

			//get headers
			Map<String, List<String>> headers = con.getHeaderFields();

			Set<Entry<String, List<String>>> hKeys = headers.entrySet();

			for (Iterator<Entry<String, List<String>>> i = hKeys.iterator(); i.hasNext();) {

				Entry<String, List<String>> m = i.next();

				if ( m.getKey() != null )
				{
					ret.headers.put(m.getKey(), m.getValue().toString());
					if (m.getKey().equals("set-cookie"))
						ret.cookies.put(m.getKey(), m.getValue().toString());	
				}
			}

			dos.close();
			rd.close();

		} catch( Exception ex )
		{
			throw ex;
		}
		
		return ret;
	}
}
