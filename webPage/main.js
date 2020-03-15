var json;
var blocks = new Array();
var main, pageLb, timeLb;
var leftCss = 0;
var isFirst = true;
var flipCount;
window.addEventListener("load",function()
{
	main = document.getElementById("main");
	document.getElementById("left").onclick = document.getElementById("right").onclick = function()
	{
		clearTimeout(flipCount);
		autoFlip();
	};
	pageLb = document.getElementById("page");
	timeLb = document.getElementById("time");
	setInterval(function()
	{
		timeLb.textContent = new Date().toLocaleString();
	},1000);
	setTimeout(autoFlip,20000);
	update();
});
function autoFlip()
{
	right();
	flipCount = setTimeout(autoFlip,20000);
}
function update()
{
	var xhr = new XMLHttpRequest()
	xhr.open("GET","/data");
	xhr.onreadystatechange = function(e)
	{
		if(e.target.readyState == 4 && e.target.status == 200)
		{
			json = JSON.parse(e.target.responseText);
			updateBlock();
		}
	}
	xhr.send();
	setTimeout(update,10000);
}
function updateBlock()
{
	var runDate = new Date();
	var newBlocks = [];
	for(var annoId in json)
	{
		var obj = json[annoId];
		if(runDate < new Date(obj["activeTime"]) || runDate > new Date(obj["deadTime"])) continue;
		var block = document.createElement("div");
		block.className = "block";
		block.id = obj["id"];
		var title = document.createElement("div");
		title.className = "title";
		title.textContent = obj["title"];
		var source = document.createElement("div");
		source.className = "source";
		source.textContent = obj["source"];
		var deadTime = document.createElement("div");
		deadTime.className = "deadTime";
		deadTime.textContent = new Date(obj["deadTime"]).toLocaleString();
		var content = document.createElement("div");
		content.className = "content";
		var lines = obj["content"].split("\n");
		for(var i in lines)
		{
			content.appendChild(document.createTextNode(lines[i]));
			content.appendChild(document.createElement("br"));
		}
		block.appendChild(title);
		block.appendChild(source);
		block.appendChild(content);
		block.appendChild(deadTime);
		
		newBlocks.push(block);
	}
	//掃舊 新無舊就刪舊 有就替換
	for(var oldIndex in blocks)
	{
		var found = -1;
		for(var newIndex in newBlocks)
		{
			if(newBlocks[newIndex].id == blocks[oldIndex].id)
			{
				found = newIndex;
				break;
			}
		}
		if(found >= 0)
		{
			main.replaceChild(newBlocks[found],blocks[oldIndex]);
			blocks[oldIndex] = newBlocks[found];
			continue;
		}
		else
		{
			blocks[oldIndex].remove();
			delete blocks[oldIndex];
		}
	}
	//掃新 舊無新就加新
	for(var newIndex in newBlocks)
	{
		var found = false;
		for(var oldIndex in blocks)
		{
			if(blocks[oldIndex].id == newBlocks[newIndex].id)
			{
				found = true;
				break;
			}
		}
		if(!found)
		{
			blocks.push(newBlocks[newIndex]);
			main.appendChild(newBlocks[newIndex]);
			continue;
		}
		else
		{
			continue;
		}
	}
	var page = Math.floor(blocks.length / 6);
	pageLb.textContent = "page " + (leftCss + 1) + " of " + (page + 1);
}
function left()
{
	leftCss -= 1;
	flip();
}
function right()
{
	leftCss += 1;
	flip();
}
function flip()
{
	blocks = blocks.filter(n => n);
	var page = Math.floor(blocks.length / 6);
	if(leftCss < 0)
	{
		leftCss = page;
	}
	if(leftCss > page)
	{
		leftCss = 0;
	}
	main.style.left = "calc((100vw - 80px) * " + -1*leftCss + ")";
	pageLb.textContent = "page " + (leftCss + 1) + " of " + (page + 1);
}