
# Android Notepad 项目文档

## 一、项目概述

这是一个 Android 笔记应用（Notepad），旨在为用户提供一个简洁、易用的记事工具。项目基于基础的笔记管理功能（如创建、编辑、删除笔记），并在此基础上进行了拓展和优化。目标是提高用户体验和提升功能的多样性。

### 基础功能
1.笔记条目时间戳显示 2.笔记查询

### 拓展功能
1.UI美化（更换背景） 2.排序功能 3.代办功能

## 二、功能描述

### 1. **笔记条目增加时间戳显示**
在 **NoteList** 界面，每一条笔记都会显示一个时间戳，表示笔记的创建时间。用户可以通过这个时间戳判断笔记的添加顺序。

- **实现方式**：使用当前系统时间（`System.currentTimeMillis()`）作为笔记的创建时间戳，并将其保存到数据库中。
- **展示效果**：时间戳会以格式化后的日期时间（如 `2024-11-26 14:30:33`）展示在笔记条目旁。
- **关键代码**：
```java
public static String StringToDate(String str_data)
    {
        String beginDate=str_data;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(new Date(Long.parseLong(beginDate)));
        return  sd;
    }
```
- **截图展示**：  
  <img src="screenshots/1.png" width="200" height="auto"/>

### 2. **笔记查询功能**
用户可以通过标题或内容对笔记进行模糊查询，快速找到需要的笔记。

- **实现方式**：在数据库中使用 `LIKE` 查询语句，分别对笔记的标题和内容进行匹配，支持模糊搜索。
- **查询界面**：在笔记列表页面顶部添加一个搜索框，用户输入查询内容后，会实时更新显示符合条件的笔记条目。
- **关键代码**：
  在onClick方法中，当用户点击搜索按钮时，会执行查询操作，在这里，如果搜索框为空，会执行与onCreate中类似的查询，查询所有笔记并显示。如果搜索框有内容，则会通过adapter.Search()方法进行自定义的搜索查询。
```java
if (et_Search.getText().toString().equals("")) {
    // 如果搜索框为空，查询所有笔记
    Cursor cursor1 = managedQuery(
            getIntent().getData(),            // 使用默认的内容URI
            PROJECTION,                       // 查询的列
            null,                             // 无where条件，返回所有记录
            null,                             // 无where参数
            NotePad.Notes.DEFAULT_SORT_ORDER  // 默认排序规则
    );
    adapter.readDate(cursor1);  // 更新适配器数据
    adapter.notifyDataSetChanged();  // 通知适配器更新
} else {
    // 如果搜索框不为空，根据搜索内容进行查询
    adapter.Search(et_Search.getText().toString());
}
```
- **截图展示**：  
  <img src="screenshots/2.png" width="200" height="auto"/>


### 3. **UI 美化**
在基础界面的设计上进行了美化，使用简易的黑白默认风格，改进了用户体验。

- **实现方式**：
    - 更改了记事本的默认背景颜色，采用更为黑白的简单的颜色搭配。
    - 更新了按钮样式，增加了阴影和圆角效果，使界面更加现代化。
    - 改善了字体和布局，使信息展示更为清晰。
- **关键代码**：
  在onCreate方法中，有一段代码通过MY_Application.getBackground()设置了LinearLayout和ListView的背景颜色，ll_noteList（笔记列表的父布局）和lv_notesList（笔记列表）背景颜色会被设置为MY_Application.getBackground()返回的颜色值。MY_Application.getBackground()可能是一个字符串，表示颜色的十六进制值（如#FFFFFF）或者其它有效的颜色格式。
```java
ll_noteList.setBackgroundColor(Color.parseColor(MY_Application.getBackground()));
lv_notesList.setBackgroundColor(Color.parseColor(MY_Application.getBackground()));
```
在这个方法中，showpopSelectBgWindows会显示一个对话框，允许用户选择不同的背景主题。对话框的布局是通过R.layout.dialog_bg_select_layout实现的，标题也被自定义为请挑选你的背景主题。当用户选择某个背景主题时，实际的背景更改逻辑是在ColorSelect方法中实现的。这个方法会根据用户点击的背景选项更改LinearLayout和ListView的背景。
```java
private void showpopSelectBgWindows(){
  LayoutInflater inflater = LayoutInflater.from(this);
  View view = inflater.inflate(R.layout.dialog_bg_select_layout, null);
  AlertDialog.Builder builder = new AlertDialog.Builder(this);
  builder.setTitle("请挑选你的背景主题");//设置标题

  TextView titleTextView = new TextView(this);
  titleTextView.setText("请挑选你的背景主题");
  titleTextView.setTextSize(20);
  titleTextView.setTextColor(Color.BLACK);  // 设置标题字体颜色为黑色
  titleTextView.setPadding(50, 50, 50, 50);  // 设置标题的内边距
  titleTextView.setBackgroundColor(Color.WHITE);
  builder.setCustomTitle(titleTextView);  // 使用自定义的标题

  builder.setView(view);
  AlertDialog dialog = builder.create();//获取dialog
  dialog.show();//显示对话框
}
public void ColorSelect(View view){
  String color;
  switch(view.getId()){
    case R.id.zero:
      Drawable btnDrawable1 = getResources().getDrawable(R.drawable.img_6);
      ll_noteList.setBackgroundDrawable(btnDrawable1);
      lv_notesList.setBackgroundDrawable(btnDrawable1);
      break;
    case R.id.one:
      Drawable btnDrawable2 = getResources().getDrawable(R.drawable.img_5);
      ll_noteList.setBackgroundDrawable(btnDrawable2);
      lv_notesList.setBackgroundDrawable(btnDrawable2);
      break;
    case R.id.two:
      Drawable btnDrawable3 = getResources().getDrawable(R.drawable.img_4);
      ll_noteList.setBackgroundDrawable(btnDrawable3);
      lv_notesList.setBackgroundDrawable(btnDrawable3);
      break;
    case R.id.three:
      Drawable btnDrawable4 = getResources().getDrawable(R.drawable.img_3);
      ll_noteList.setBackgroundDrawable(btnDrawable4);
      lv_notesList.setBackgroundDrawable(btnDrawable4);
      break;
    case R.id.four:
      Drawable btnDrawable5 = getResources().getDrawable(R.drawable.img_2);
      ll_noteList.setBackgroundDrawable(btnDrawable5);
      lv_notesList.setBackgroundDrawable(btnDrawable5);
      break;
    case R.id.five:
      Drawable btnDrawable6 = getResources().getDrawable(R.drawable.img_1);
      ll_noteList.setBackgroundDrawable(btnDrawable6);
      lv_notesList.setBackgroundDrawable(btnDrawable6);
      break;
    case R.id.six:
      Drawable btnDrawable7 = getResources().getDrawable(R.drawable.img);
      ll_noteList.setBackgroundDrawable(btnDrawable7);
      lv_notesList.setBackgroundDrawable(btnDrawable7);
      break;
  }
}

```

- **截图展示**：  
  <img src="screenshots/3.png" width="200" height="auto"/>
  <img src="screenshots/4.png" width="200" height="auto"/>
  <img src="screenshots/5.png" width="200" height="auto"/>


### 4. **代办功能**
用户可以标记笔记为代办事项，或者直接删除某些笔记。

- **实现方式**：每个笔记都有匹配一个代办属性，点击删除代办的对应按钮能删除某篇笔记
- **功能实现**：
    - 在每条笔记旁增加一个代办按钮来进行删除笔记
- **关键代码**：
```java
```
- **截图展示**：  
  <img src="screenshots/9.png" width="200" height="auto"/>


### 5. **反向排序**
笔记列表会根据时间戳进行反向排序，之前创建的笔记显示在最上面。

- **实现方式**：查询笔记时，按照时间戳进行降序排序。
- **展示效果**：用户能够看到原先的笔记始终在列表的顶部，而新的笔记则排在下面。
- **关键代码**：
  查询笔记时的排序： 在managedQuery方法中，查询笔记时使用了NotePad.Notes.DEFAULT_SORT_ORDER作为排序依据，这意味着笔记列表会按照该默认排序规则返回数据。可以在NotePad.Notes.DEFAULT_SORT_ORDER中修改排序规则来实现倒序排序。
```java
Cursor cursor = managedQuery(
        getIntent().getData(),            // 使用默认的内容URI
        PROJECTION,                       // 返回笔记ID和标题
        null,                             // 没有where条件，返回所有记录
        null,                             // 没有where列值
        NotePad.Notes.DEFAULT_SORT_ORDER  // 使用默认排序
);
```
- **截图展示**：  
  <img src="screenshots/6.png" width="200" height="auto"/>
  <img src="screenshots/7.png" width="200" height="auto"/>
  <img src="screenshots/8.png" width="200" height="auto"/>

---

## 三、技术栈

- **编程语言**: Java
- **开发环境**: Android Studio
- **数据库**: SQLite
- **界面设计**: XML 布局文件
- **依赖库**:
    - RecyclerView: 用于展示笔记列表。
    - SQLite: 用于存储和查询笔记数据。

