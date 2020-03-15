# announcement-board

公告版程式

若要直接使用就啟動announcementBoard.jar並用瀏覽器連上該台電腦的port 80來瀏覽公告
在伺服器端程式視窗中即可管理公告
公告資訊會存放於./data.json中

--data.json格式待補齊--

若要在修改java部分後build，需要有org.json類別庫

在announcementBoard.jar中已經包含了org.json

打包jar時需要把網頁檔案(預設為webPage/*)和allowFiles.txt都包進去
若有其他的檔案要讓客戶端存取的話也要包進jar並在allowFiles.txt中新增允許存取列表
