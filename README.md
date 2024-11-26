
# Android Notepad 项目文档

## 项目概述

这是一个 Android 笔记应用（Notepad），旨在为用户提供一个简洁、易用的记事工具。项目基于基础的笔记管理功能（如创建、编辑、删除笔记），并在此基础上进行了拓展和优化。扩展功能包括笔记条目时间戳显示、笔记查询、UI美化、代办功能和反向排序，目标是提高用户体验和提升功能的多样性。

## 功能描述

### 1. **笔记条目增加时间戳显示**
   在 **NoteList** 界面，每一条笔记都会显示一个时间戳，表示笔记的创建时间。用户可以通过这个时间戳判断笔记的添加顺序。

   - **实现方式**：使用当前系统时间（`System.currentTimeMillis()`）作为笔记的创建时间戳，并将其保存到数据库中。
   - **展示效果**：时间戳会以格式化后的日期时间（如 `2024-11-26 14:30`）展示在笔记条目旁。

### 2. **笔记查询功能**
   用户可以通过标题或内容对笔记进行模糊查询，快速找到需要的笔记。

   - **实现方式**：在数据库中使用 `LIKE` 查询语句，分别对笔记的标题和内容进行匹配，支持模糊搜索。
   - **查询界面**：在笔记列表页面顶部添加一个搜索框，用户输入查询内容后，会实时更新显示符合条件的笔记条目。

### 3. **UI 美化**
   在基础界面的设计上进行了美化，改进了用户体验。

   - **实现方式**：
     - 更改了记事本的背景颜色，采用更为柔和的颜色搭配。
     - 更新了按钮样式，增加了阴影和圆角效果，使界面更加现代化。
     - 改善了字体和布局，使信息展示更为清晰。

### 4. **代办功能**
   用户可以标记笔记为代办事项，或者直接删除某些笔记。

   - **实现方式**：添加一个代办按钮，用户点击后该笔记会被标记为待办状态，可以显示在专门的“待办”列表中，或者可以直接删除笔记。
   - **功能实现**：
     - 在每条笔记旁增加一个删除按钮，点击时会询问用户是否确认删除。
     - 添加代办状态的切换功能，标记为代办的笔记会以不同的样式展示。

### 5. **反向排序**
   笔记列表会根据时间戳进行反向排序，最新创建的笔记显示在最上面。

   - **实现方式**：查询笔记时，按照时间戳进行降序排序。
   - **展示效果**：用户能够看到最新的笔记始终在列表的顶部，而旧的笔记则排在下面。

## 功能演示

### 笔记列表界面
- 显示所有笔记的标题、内容摘要和创建时间（时间戳）。
- 用户可以点击每一条笔记，进入 **NoteDetailActivity** 页面查看或编辑。

### 添加笔记
- 用户点击添加按钮进入笔记编辑页面。
- 在编辑页面，可以输入标题、内容，并保存或删除笔记。

### 笔记查询
- 在笔记列表页面，用户可以通过搜索框输入查询条件，实时过滤符合条件的笔记。

### 代办功能
- 每条笔记都有一个删除按钮，用户可以删除某条笔记。
- 可以将笔记标记为代办，代办笔记将显示在一个单独的列表或高亮显示。

---

## 技术栈

- **编程语言**: Java
- **开发环境**: Android Studio
- **数据库**: SQLite
- **界面设计**: XML 布局文件
- **依赖库**:
  - RecyclerView: 用于展示笔记列表。
  - SQLite: 用于存储和查询笔记数据。

