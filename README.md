      
# PathView
Android draw svg vector
#![image](https://github.com/crossll/PathView/blob/master/pathview.gif) 
# HOW TO USE
## xml
     <cross.ui.PathView
        android:id="@+id/pathView"
        android:layout_width="match_parent"
        android:layout_height="400dip"
        android:background="#ffee00"
        android:padding="10dip"
        app:gravity="center" />   
        
   more
       
        app:gravity
        app:boundary_color
        app:click_color
## java
       setOnAreaLoadedCallback();//监听view初始化成
       addOnAreaClick();//区域点击效果
 #网络请求库
#简介
本库基于[OKHTTP]框架进行的Android网络请求基本封装

        
