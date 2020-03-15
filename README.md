# announcement-board

公告版程式

若要直接使用就去release下載並啟動最新版的announcementBoard.jar，並用瀏覽器連上該台電腦的port 80來瀏覽公告

在伺服器端程式視窗中即可管理公告

公告資訊會存放於./data.json中

若未有已存在的data.json便會自動建立一個空的

--data.json格式待補齊--

---原始碼的使用---

若要在修改原始碼後build，需要有org.json類別庫

在發行的jar檔中已經包含了org.json

打包jar時需要把網頁檔案（預設為webPage/* ）和allowFiles.txt以及org.json都包進去

若有其他的檔案要讓客戶端存取的話也要包進jar並在allowFiles.txt中新增允許存取列表

--allowFiles.txt格式--

一行代表一個允許存取的檔案，路徑字首必要有/，例如/webPage/index.html或/main.js

目前不支援萬用字元*
