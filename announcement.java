package net.incognitas.announcementBoard;

import org.json.*;

public class announcement
{
	public String id;
	public String title;
	public String source;
	public String content;
	public String activeTime;
	public String deadTime;
	public String createTime;
	public JSONObject getJSONObject()
	{
		JSONObject obj = new JSONObject();
		obj.put("id",id);
		obj.put("title",title);
		obj.put("source",source);
		obj.put("content",content);
		obj.put("activeTime",activeTime);
		obj.put("deadTime",deadTime);
		obj.put("createTime",createTime);
		return obj;
	}
	public announcement(JSONObject obj)
	{
		id = (String)obj.get("id");
		title = (String)obj.get("title");
		source = (String)obj.get("source");
		content = (String)obj.get("content");
		activeTime = (String)obj.get("activeTime");
		deadTime = (String)obj.get("deadTime");
		createTime = (String)obj.get("createTime");
	}
	public announcement()
	{
		
	}
}