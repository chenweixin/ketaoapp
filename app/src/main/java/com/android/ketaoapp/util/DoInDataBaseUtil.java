package com.android.ketaoapp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.ketaoapp.entity.Search;

public class DoInDataBaseUtil {

	public static void insertSearchRecord(SQLiteDatabase db, Search search){
		ContentValues values = new ContentValues();
		values.put("user_id", search.getUser_id());
		values.put("search", search.getSearch());
		values.put("timestamp", search.getTimestamp());
		try {
			db.insert("search_record", null, values);
		} catch (Exception e) {
			db.update("search_record", values, "search=?", new String[] {search.getSearch()});
		}
		values.clear();
		db.close();
	}

	public static void deleteSearchRecord(SQLiteDatabase db, String user_id){
		try {
			db.delete("search_record", "user_id=?", new String[]{user_id});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.close();
	}

	public static List<Search> getSearch(SQLiteDatabase db, String user_id){
		Cursor c = db.rawQuery("select * from search_record where user_id=?",
				new String[] { user_id});
		List<Search> searches = new ArrayList<Search>();
		if(c != null){
			while(c.moveToNext()){
				Search search = new Search();
				search.setSearch(c.getString(c.getColumnIndex("search")));
				search.setTimestamp(c.getInt(c.getColumnIndex("timestamp")));
				search.setUser_id(c.getString(c.getColumnIndex("user_id")));
				searches.add(search);
			}
		}
		return searches;
	}
}
