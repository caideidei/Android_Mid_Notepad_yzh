
package com.example.android.notepad;
import com.example.android.notepad.application.MY_Application;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class NotesList extends ListActivity implements View.OnClickListener {


    private EditText et_Search;//搜索框控件实例
    private ImageView iv_searchnotes;//搜索按钮实例
    private ListView lv_notesList;
    private NotesListAdapter adapter;
    private ImageView iv_addnotes;//添加按钮
    private LinearLayout ll_noteList;
    // For logging and debugging
    private static final String TAG = "NotesList";


    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_CREATE_DATE,//2
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE//3
    };


    private static final int COLUMN_INDEX_TITLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noteslist_layout);
        initView();
        // The user does not need to hold down the key to use menu shortcuts.
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        Intent intent = getIntent();
        // If there is no data associated with the Intent, sets the data to the default URI, which
        // accesses a list of notes.
        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }


        getListView().setOnCreateContextMenuListener(this);


        Cursor cursor = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note.
                null,                             // No where clause, return all records.
                null,                             // No where clause, therefore no where column values.
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );

        adapter=new NotesListAdapter(getApplicationContext(),cursor,getIntent().getData(),getIntent().getAction());
        lv_notesList.setAdapter(adapter);

    }

    /*
    绑定id
     */
    private void initView() {
        ll_noteList= (LinearLayout) findViewById(R.id.noteList_layout);
        iv_addnotes= (ImageView) findViewById(R.id.fab);
        lv_notesList= (ListView) findViewById(android.R.id.list);//绑定listView;
        et_Search= (EditText) findViewById(R.id.et_Search);
        iv_searchnotes= (ImageView) findViewById(R.id.iv_searchnotes);

        iv_addnotes.setOnClickListener(this);
        iv_searchnotes.setOnClickListener(this);
        ll_noteList.setBackgroundColor(Color.parseColor(MY_Application.getBackground()));
        lv_notesList.setBackgroundColor(Color.parseColor(MY_Application.getBackground()));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
                break;
            case R.id.iv_searchnotes:
                showOrhide();
                if(et_Search.getText().toString().equals("")){
                    Cursor cursor1 = managedQuery(
                            getIntent().getData(),            // Use the default content URI for the provider.
                            PROJECTION,                       // Return the note ID and title for each note.
                            null,                             // No where clause, return all records.
                            null,                             // No where clause, therefore no where column values.
                            NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
                    );
                    adapter.readDate(cursor1);
                    adapter.notifyDataSetChanged();
                }else{
                    adapter.Search(et_Search.getText().toString());
                }

                break;
        }
    }

    /*
    软硬盘的显示和隐藏
     */
    private void showOrhide(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Cursor cursor1 = managedQuery(
                getIntent().getData(),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note.
                null,                             // No where clause, return all records.
                null,                             // No where clause, therefore no where column values.
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );
        adapter.readDate(cursor1);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_options_menu, menu);

        // 添加 IntentOptions（原有逻辑）
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // The paste menu item is enabled if there is data on the clipboard.
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);


        MenuItem mPasteItem = menu.findItem(R.id.menu_paste);

        // If the clipboard contains an item, enables the Paste option on the menu.
        if (clipboard.hasPrimaryClip()) {
            mPasteItem.setEnabled(true);
        } else {
            // If the clipboard is empty, disables the menu's Paste option.
            mPasteItem.setEnabled(false);
        }

        // Gets the number of notes currently being displayed.
        final boolean haveItems = adapter.getCount() > 0;


        if (haveItems) {


            Uri uri = ContentUris.withAppendedId(getIntent().getData(), getSelectedItemId());

            Intent[] specifics = new Intent[1];

            specifics[0] = new Intent(Intent.ACTION_EDIT, uri);

            MenuItem[] items = new MenuItem[1];

            Intent intent = new Intent(null, uri);


            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);


            menu.addIntentOptions(
                    Menu.CATEGORY_ALTERNATIVE,
                    Menu.NONE,
                    Menu.NONE,
                    null,
                    specifics,
                    intent,
                    Menu.NONE,
                    items
            );

            if (items[0] != null) {


                items[0].setShortcut('1', 'e');
            }
        } else {
            // If the list is empty, removes any existing alternative actions from the menu
            menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
        }

        // Displays the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_paste:


                startActivity(new Intent(Intent.ACTION_PASTE, getIntent().getData()));
                return true;

            case R.id.bg_change:
                // 调用背景更换方法
                showpopSelectBgWindows();
                return true;

            case R.id.menu_filter_category:
                // 调用分类筛选对话框方法
                showCategoryFilterDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void showCategoryFilterDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("选择分类");
//
//        // 分类选项，与 strings.xml 中的分类选项保持一致
//        final String[] categories = getResources().getStringArray(R.array.note_categories);
//
//        builder.setItems(categories, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // 获取选择的分类
//                String selectedCategory = categories[which];
//
//                // 更新笔记列表显示
//                filterNotesByCategory(selectedCategory);
//            }
//        });
//
//        builder.setNegativeButton("取消", null);
//        builder.show();
//    }
private void showCategoryFilterDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    // 使用自定义的标题视图
    TextView titleView = new TextView(this);
    titleView.setText("选择分类");
    titleView.setTextSize(25); // 设置标题字体大小
    titleView.setTextColor(Color.BLACK); // 设置标题文字颜色为黑色
    titleView.setBackgroundColor(Color.WHITE); // 设置标题背景为白色
    titleView.setPadding(20, 20, 20, 20); // 设置标题的内边距

    builder.setCustomTitle(titleView); // 设置自定义标题

    // 分类选项，与 strings.xml 中的分类选项保持一致
    final String[] categories = getResources().getStringArray(R.array.note_categories);

    builder.setItems(categories, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 获取选择的分类
            String selectedCategory = categories[which];

            // 更新笔记列表显示
            filterNotesByCategory(selectedCategory);
        }
    });

    // 设置取消按钮
    builder.setNegativeButton("取消", null);

    // 创建并显示对话框
    AlertDialog dialog = builder.create();

    // 设置对话框的其他组件样式
    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
        @Override
        public void onShow(DialogInterface dialogInterface) {
            AlertDialog d = (AlertDialog) dialogInterface;

            // 设置取消按钮的文本颜色为黑色，背景为白色
            Button cancelButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);
            if (cancelButton != null) {
                cancelButton.setTextColor(Color.BLACK); // 黑色按钮文字
                cancelButton.setBackgroundColor(Color.WHITE); // 白色按钮背景
            }

            // 设置选项列表背景为白色，文字为黑色
            ListView listView = d.getListView();
            listView.setBackgroundColor(Color.WHITE); // 白色背景
            listView.setDivider(new ColorDrawable(Color.BLACK)); // 黑色分隔线
            listView.setDividerHeight(1);

            listView.setDividerHeight(0);
            // 修改每个选项的文字颜色为黑色
            for (int i = 0; i < listView.getChildCount(); i++) {
                TextView item = (TextView) listView.getChildAt(i);
                item.setTextColor(Color.BLACK); // 黑色选项文字
            }
        }
    });

    // 显示对话框
    dialog.show();
}



    // 在 NoteList 类中，筛选笔记并更新显示
    private void filterNotesByCategory(String category) {
        Cursor cursor;

        if ("All".equals(category)) { // 修改为统一的英文分类名
            // 如果选择的是“全部”，查询所有笔记
            cursor = getContentResolver().query(
                    NotePad.Notes.CONTENT_URI,  // 笔记的 URI
                    PROJECTION,                // 查询的列
                    null,                      // 无筛选条件
                    null,                      // 无筛选参数
                    null                       // 默认排序
            );
        } else {
            // 如果选择的是某个具体分类
            String selection = NotePad.Notes.COLUMN_NAME_CATEGORY + "=?";
            String[] selectionArgs = new String[]{category};

            cursor = getContentResolver().query(
                    NotePad.Notes.CONTENT_URI,  // 笔记的 URI
                    PROJECTION,                // 查询的列
                    selection,                 // 筛选条件
                    selectionArgs,             // 筛选参数
                    null                       // 默认排序
            );
        }

        if (cursor != null) {
            // 使用查询结果更新界面
            adapter.changeCursor(cursor); // 替换 mAdapter 为 adapter
        }
    }







    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        // Tries to get the position of the item in the ListView that was long-pressed.
        try {
            // Casts the incoming data object into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            // If the menu object can't be cast, logs an error.
            Log.e(TAG, "bad menuInfo", e);
            return;
        }


        Cursor cursor = managedQuery(
                Uri.parse(getIntent().getData()+"/"+adapter.getmDate().get(info.position).getCursor_id()),            // Use the default content URI for the provider.
                PROJECTION,                       // Return the note ID and title for each note.
                null,                             // No where clause, return all records.
                null,                             // No where clause, therefore no where column values.
                NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
        );

        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }else{
            cursor.moveToNext();
        }
        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_context_menu, menu);

        // Sets the menu header to be the title of the selected note.
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));


        Intent intent = new Intent(null, Uri.withAppendedPath(getIntent().getData(),
                adapter.getmDate().get(info.position).getCursor_id()) );
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        try {
            // Casts the data object in the item into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {

            // If the object can't be cast, logs an error
            Log.e(TAG, "bad menuInfo", e);

            // Triggers default processing of the menu item.
            return false;
        }
        // Appends the selected note's ID to the URI sent with the incoming Intent.
        Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), Integer.parseInt(adapter.getmDate().get(info.position).getCursor_id()));
        /*
         * Gets the menu item's ID and compares it to known actions.
         */
        switch (item.getItemId()) {
            case R.id.context_open:
                // Launch activity to view/edit the currently selected item
                startActivity(new Intent(Intent.ACTION_EDIT, noteUri));
                return true;
            //BEGIN_INCLUDE(copy)
            case R.id.context_copy:
                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

                // Copies the notes URI to the clipboard. In effect, this copies the note itself
                clipboard.setPrimaryClip(ClipData.newUri(   // new clipboard item holding a URI
                        getContentResolver(),               // resolver to retrieve URI info
                        "Note",                             // label for the clip
                        noteUri)                            // the URI
                );


                return true;
            //END_INCLUDE(copy)
            default:
                return super.onContextItemSelected(item);
        }
    }
    /*
       背景颜色选择框
        */
    private  void showpopSelectBgWindows(){
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

    /*
    背景改变的监听
     */
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



}


