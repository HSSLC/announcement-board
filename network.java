package net.incognitas.announcementBoard;

import java.io.*;
import java.nio.charset.*;
import java.net.*;
import com.sun.net.httpserver.*;

class network
{
	public static HttpServer server;
	public static void start()
	{
		try
		{
			server = HttpServer.create(new InetSocketAddress(80),0);
			server.setExecutor(null);
			server.createContext("/",exchange ->
			{
				Headers header = exchange.getResponseHeaders();
				header.add("Location","/main");
				exchange.sendResponseHeaders(302,0);
				exchange.close();
			});
			server.createContext("/main",exchange ->
			{
				exchange.sendResponseHeaders(200,0);
				OutputStream os = exchange.getResponseBody();
				byte[] buffer = new byte[1024];
				InputStream is = network.class.getResourceAsStream("/webPage/index.html");
				int hasRead;
				while((hasRead = is.read(buffer)) > 0)
				{
					os.write(buffer,0,hasRead);
				}
				is.close();
				os.close();
				exchange.close();
			});
			server.createContext("/webPage",exchange ->
			{
				URI uri = exchange.getRequestURI();
				String path = uri.getPath();
				
				boolean isAllow = false;
				for(String str : main.allowFiles)
				{
					if(str.equals(path))
					{
						isAllow = true;
						break;
					}
				}
				try
				{
					if(!isAllow) throw new Exception("Not Allow To Access File");
					OutputStream os = exchange.getResponseBody();
					byte[] buffer = new byte[1024];
					InputStream is = network.class.getResourceAsStream(path);
					if(is == null) throw new Exception("File Not Exists");
					exchange.sendResponseHeaders(200,0);
					int hasRead;
					while((hasRead = is.read(buffer)) > 0)
					{
						os.write(buffer,0,hasRead);
					}
					is.close();
					os.close();
				}
				catch(Exception e)
				{
					exchange.sendResponseHeaders(404,0);
				}
				finally
				{
					exchange.close();
				}
			});
			server.createContext("/data",exchange ->
			{
				Headers header = exchange.getResponseHeaders();
				header.add("Content-Type","application/json; charset=UTF-8");
				byte[] bytes = main.data.toString().getBytes(StandardCharsets.UTF_8);
				exchange.sendResponseHeaders(200,bytes.length);
				OutputStream os = exchange.getResponseBody();
				os.write(bytes);
				os.close();
				exchange.close();
			});
			server.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public static void stop()
	{
		server.stop(0);
	}
}