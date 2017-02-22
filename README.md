# PathView
根据SVG矢量信息绘制图形，实现拖动、缩放、点击效果
#![image](https://github.com/crossll/PathView/blob/master/pathview.gif)   
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
    setOnAreaLoadedCallback();//监听view初始化完成
    addOnAreaClick();//区域点击效果
        
