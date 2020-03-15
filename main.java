package net.incognitas.announcementBoard;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import org.json.*;
import java.util.*;

public class main
{
	public static JSONObject data;
	public static HashMap<String,announcement> announcements = new HashMap<>();
	public static ArrayList<String> allowFiles = new ArrayList<>();
	private static void load()
	{
		//���Jjson���
		File dataFile = new File("data.json");
		if(dataFile.exists())
		{
			String str = readString("data.json");
			if(str != "")
			{
				data = new JSONObject(str);
				Iterator<String> iter = data.keys();
				while(iter.hasNext())
				{
					String key = iter.next();
					announcements.put(key,new announcement((JSONObject)data.get(key)));
				}
			}
			else
			{
				data = new JSONObject();
			}
		}
		else
		{
			try
			{
				dataFile.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		//���J���\�ɮײM��
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(main.class.getResourceAsStream("/allowFiles.txt"),StandardCharsets.UTF_8));
			String read;
			while((read = br.readLine()) != null)
			{
				allowFiles.add(read);
			}
		}
		catch(IOException e)
		{
			
		}
	}
	public static void save()
	{
		//�x�s
		updateJSON();
		try
		{
			OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream("data.json"),StandardCharsets.UTF_8);
			fw.write(data.toString());
			fw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public static void updateJSON()
	{
		//��sjson����H�ŦXannouncements map
		data = new JSONObject();
		announcements.forEach((k,v) ->
		{
			data.put(k,v.getJSONObject());
		});
	}
	public static void main(String[] args)
	{
		System.out.println("���J�ɮ׸�Ƥ�...");
		load();
		System.out.println("�غcGUI��...");
		new gui();
		System.out.println("�Ұ�Server��...");
		network.start();
		System.out.println("��l�Ƨ���");
	}
	private static String readString(String path)
	{
		try
		{
			StringBuilder str = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path),StandardCharsets.UTF_8));
			String read;
			while((read = br.readLine()) != null)
			{
				str.append(read);
			}
			if(str.toString().equals("")) return "";
			else return str.toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}