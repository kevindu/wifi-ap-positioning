package xuandu.location.genoa.fingerprintcollector.localizer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.Set;

import xuandu.location.indoormapapi.RuntimeConfig;

public class RssSQL {
	private String path = RuntimeConfig.getApplicationPath() +
			RuntimeConfig.getProjectDir() + "/rss.db";
	private SQLiteDatabase db = null;
	public static String APADDRESSTABLE = "ApAddress";
	public static String AVERAGERSSTABLE = "AverageRss";
	public static String DEVIATIONTABLENAME = "Deviation";
	public RssSQL(){
		File dbfile = new File(path);
		db = SQLiteDatabase.openOrCreateDatabase(dbfile.getAbsolutePath(), null);
	}
	public boolean isApTableExist()
	{
		return isExistTable(APADDRESSTABLE);
	}
	/**
	 * @param name 需要检测的表的名字
	 * @return 存在true 否则false
	 */
	public boolean isExistTable(String name){
		boolean flag = false;
		if(name == null){
            return flag;
		}
		Cursor cursor = null;
		try {           
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+ name.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            if(count>0){
                flag = true;
            }            
		} catch (Exception e) {
            // TODO: handle exception
		}                
		return flag;
	}
	/**
	 * 保存检测到的AP列表名称，名称以MAC地址形式存在
	 * @param macSet MAC地址集合
	 */
	public void storeMacAddr(Set<String> macSet) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE IF NOT EXISTS " + APADDRESSTABLE + 
				" (id INTEGER PRIMARY KEY AUTOINCREMENT, bssid TEXT DEFAULT NULL," +
				" ssid TEXT DEFAULT NULL, capabilities TEXT DEFAULT NULL," +
				" frequency TEXT DEFAULT NULL, lon FLOAT, lat FLOAT, locationId INTEGER);";
		db.execSQL(sql);
		
		String mac[] = getMacAddr();		
		for(String add : macSet){
			boolean existFlag = false;
			for(int i = 0; i < mac.length && !existFlag; i++){
				if(mac[i].equals(add))
					existFlag = true;
			}
			if(!existFlag){
				ContentValues cv = new ContentValues();
				cv.put("bssid", add);
				db.insert(APADDRESSTABLE, null, cv);
			}
		}
	}
	public void setApLocation(int ApID, int locationID, double x, double y){
		String sql = "UPDATE " + APADDRESSTABLE + " SET locationId = " + 
				locationID + ", lon = " + y + ",lat = " + x +
				" WHERE id = " + ApID;
		db.execSQL(sql);
	}
	/**
	 * 获取数据库中的AP MAC地址列表
	 * @return 数据库中的AP MAC地址列表
	 */
	public String[] getMacAddr(){
		String[] columns = {"bssid"};
		Cursor c = db.query(APADDRESSTABLE, columns, null, null, null, null, null);
		c.moveToFirst();
		String mac[] = new String[c.getCount()];
		for(int i = 0; i < c.getCount(); i++){
			mac[i] = c.getString(0);
			c.moveToNext();
		}
		c.close();
		return mac;
	}
	/**
	 * 保存训练得到的RSS值以及与之相关的房间信息，坐标，方差信息
	 * @param roomId 房间ID
	 * @param x 坐标信息
	 * @param y 坐标信息
	 * @param rss RSS值
	 * @param defaultRss 默认最小的WIFI信号强度
	 * @param dev　方差
	 */
	public void storeAveRss(int roomId, int vertexId, double x, double y,double[] rss, double defaultRss/*, double dev[]*/) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE IF NOT EXISTS " + AVERAGERSSTABLE + 
				" (id INTEGER PRIMARY KEY AUTOINCREMENT, roomId INTEGER NOT NULL," +
				" vertexId INTEGER NOT NULL, x FLOAT DEFAULT 0, y FLOAT DEFAULT 0, head INTEGER";
		for(int i = 1; i <= rss.length; i++){
			sql += ", AP" + i + " FLOAT DEFAULT " + defaultRss;
		}
		sql += ");";
		db.execSQL(sql);
		ContentValues cv = new ContentValues();
		for(int j = 1; j <= rss.length; j++){
			cv.put("AP"+j, rss[j-1]);
		}
		cv.put("roomId", roomId);
		cv.put("vertexId", vertexId);
		cv.put("x", x);
		cv.put("y", y);
		db.insert(AVERAGERSSTABLE, null, cv);
		sql = "SELECT max(id) FROM " + AVERAGERSSTABLE;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
//		storeDeviation(dev, c.getInt(0));
		c.close();
	}
	/**
	 * 更新RSS的分类首领
	 * @param memberId RSS的ID
	 * @param head 分类首领的ID
	 */
	public void storeClassHead(int memberId, int head){
		String sql = "UPDATE " + AVERAGERSSTABLE + " SET head = " + head + " WHERE id = " + memberId;
		db.execSQL(sql);
	}
	/**
	 * 存放RSS的方差信息
	 * @param dev 方差信息
	 * @param rssId 与之相关的方差信息
	 */
	public void storeDeviation(double[] dev, int rssId){
		String sql = "CREATE TABLE IF NOT EXISTS " + DEVIATIONTABLENAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT" +
				", rssId INTEGER NOT NULL";
		for(int i = 1; i <= dev.length; i++){
			sql += ", AP" + i + " FLOAT DEFAULT NULL";
		}
		sql += ");";
		db.execSQL(sql);
		ContentValues cv = new ContentValues();
		cv.put("rssId", rssId);
		for(int j = 1; j <= dev.length; j++){
			cv.put("AP"+j, dev[j-1]);
		}
		db.insert(DEVIATIONTABLENAME, null, cv);
	}	
	/**
	 * 获取所有的分类首领ID
	 * @return 所有的分类首领ID
	 */
	public int[] getAllHead(){
		String sql = "SELECT id from " + AVERAGERSSTABLE + " WHERE id = head";
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		int length = c.getCount();
		int head[] = new int[length];
		for(int i = 0; i < length; i++){
			head[i] = c.getInt(0);
			c.moveToNext();
		}
		c.close();
		return head;
	}
	/**
	 * 通过分类首领ID得到所有类成员的ID
	 * @param id　分类首领的ID
	 * @return　所有成员的ID
	 */
	public int[] getMembersByHeadId(int id){
		String sql = "SELECT id FROM " + AVERAGERSSTABLE +" WHERE HEAD = " + id;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		int length = c.getCount();
		int[] member = new int[length];
		for(int i = 0; i < c.getCount(); i++){
			member[i] = c.getInt(0);
			c.moveToNext();
		}
		c.close();
		return member;
	}
	/**
	 * 通过RSS的ID和掩码序列得到所需的RSS信息
	 * @param id RSS的ID
	 * @param mask　掩码序列
	 * @return　所需的RSS信息
	 */
	public double[] getMaskedRssByID(int id, int[] mask)
	{
		int length = mask.length;
		String[] columns = new String[length];
		for(int i = 0; i < length; i++){
			columns[i] = new String("AP" + mask[i]);
		}
		Cursor c = db.query(AVERAGERSSTABLE, columns, "id = " + id, null, null, null, null, null);
		c.moveToFirst();
		double[] rss = new double[length];
		for(int i = 0; i < length; i++){
			rss[i] = c.getFloat(i);
		}
		c.moveToNext();	
		c.close();
		return rss;
	}
	/**
	 * 通过RSS 的ID获取位置信息
	 * @param id　RSS的ID
　	 * @return　RSS的相关位置信息
	 */
	public double[] getCoordinateById(int id){
		double[] coordinate = new double[2];
		String sql = "SELECT x, y FROM " + AVERAGERSSTABLE + " WHERE id = " + id;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		coordinate[0] = c.getDouble(0);
		coordinate[1] = c.getDouble(1);
		c.close();
		return coordinate;
	}
	/**
	 * 通过RSS的ID获取房间ID
	 * @param id RSS的ID
	 * @return 房间的ID
	 */
	public int getRoomIdByRssId(int id){
		String sql = "SELECT roomId FROM " + AVERAGERSSTABLE + " WHERE id = " + id;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		int roomId = c.getInt(0);
		c.close();
		return roomId;
	}
	public int getVertexIdByRssId(int id){
		String sql = "SELECT vertexId FROM " + AVERAGERSSTABLE + " WHERE id = " + id;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		int vertexId = c.getInt(0);
		c.close();
		return vertexId;
	}
	/**
	 * 获取所有RSS的ID
	 * @return　所有RSS的ID
	 */
	public int[] getAllRssId(){
		String sql = "SELECT id FROM " + AVERAGERSSTABLE;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		int length = c.getCount();
		int id[] = new int[length];
		for(int i = 0; i < length; i++, c.moveToNext()){
			id[i] = c.getInt(0);
		}
		c.close();
		return id;
	}
	/**
	 * 通过RSS的ID获取RSS值
	 * @param id　RSS的ID
	 * @return　RSS值
	 */
	public double[] getRssById(int id){
		int length = getMacAddr().length;
		String[] columns = new String[length];
		for(int i = 1; i <= length; i++){
			columns[i-1] = new String("AP" + i);
		}
		Cursor c = db.query(AVERAGERSSTABLE, columns, "id = " + id, null, null, null, null);
		c.moveToFirst();
		double[] rss = new double[length];
		for(int i = 0; i < length; i++){
			rss[i] = c.getDouble(i);
		}
		c.close();
		return rss;
	}
	/**
	 * 通过RSS的ID获取RSS的方差
	 * @param id　RSS的ID
	 * @return　RSS的方差
	 */
	public double[] getDeviationById(int id){
		int length = getMacAddr().length;
		String[] columns = new String[length];
		for(int i = 1; i <= length; i++){
			columns[i-1] = new String("AP" + i);
		}
		Cursor c = db.query(DEVIATIONTABLENAME, columns, "rssId = " + id, null, null, null, null);
		double[] dev = new double[length];
		c.moveToFirst();
		for(int i = 0; i < length; i++){
			dev[i] = c.getDouble(i);
		}
		c.close();
		return dev;
	}
	public int[] getMatchedRssIDByVertexId(int vertex) {
		// TODO Auto-generated method stub
		Cursor c = null;
		String sql = "SELECT id FROM " + AVERAGERSSTABLE +" WHERE vertexId = " + vertex;
		c = db.rawQuery(sql, null);
		c.moveToFirst();
		int length = c.getCount();
		int[] ids = new int[length];
		for(int i = 0; i < length; i++){
			ids[i] = c.getInt(0);
			c.moveToNext();
		}
		if(c != null)
			c.close();
		return ids;
	}
	public void removeRpTable() {
		// TODO Auto-generated method stub
		if(isExistTable(AVERAGERSSTABLE))
			db.execSQL("DROP TABLE " + AVERAGERSSTABLE);		
	}
	public void removeApTable() {
		// TODO Auto-generated method stub
		if(isExistTable(APADDRESSTABLE))
			db.execSQL("DROP TABLE " + APADDRESSTABLE);		
	}
	public void removeDevTable() {
		// TODO Auto-generated method stub
		if(isExistTable(DEVIATIONTABLENAME))
			db.execSQL("DROP TABLE " + DEVIATIONTABLENAME);		
	}
	public void removeAllTable() {
		// TODO Auto-generated method stub
		removeApTable();
		removeRpTable();
		removeDevTable();
	}
	
}
