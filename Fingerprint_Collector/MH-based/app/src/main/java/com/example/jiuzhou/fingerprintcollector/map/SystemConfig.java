package com.example.jiuzhou.fingerprintcollector.map;

import android.content.Context;

import java.io.File;

import xuandu.location.indoormapapi.MapManager;
import xuandu.location.indoormapapi.RuntimeConfig;

/**
 * 给程序提供静态方法以及静态的全局变量

 * 
 */
public class SystemConfig {


	/**
	 * XD: declare the map-related objects
	 */
	public static MapManager mMapManager;

	/** 数据地址 */
	private static String positionDataDir;

    private static String mapDBFile;
    private static String wifiDBFile;

	/**
	 * 重置二次变量，以免切换项目等操作时地址不更新
	 */
	protected static void init(Context context) {

		//XD: initialise map engine
		mMapManager = new MapManager();
		mMapManager.init("UK", context);

		positionDataDir = appendDir(RuntimeConfig.getProjectDir(), "pdata");

		mapDBFile = RuntimeConfig.getDatabaseFile();

		wifiDBFile = appendDir(positionDataDir, "radioMap.db");
	}

    public static String getMapDBFile() {
        return mapDBFile;
    }

    public static String getWifiDBFile() {
        return wifiDBFile;
    }


    /**
	 * 拼接Dir
	 *
	 * @param args	Dir分段参数
	 * @return String
	 */
	public static String appendDir(String... args) {
		StringBuffer sb = new StringBuffer();
		sb.setLength(0);
		int len = args.length;
		if (len > 1) {
			sb.append(args[0]);
			for (int i = 1; i < len; i++) {
				sb.append(File.separator);
				sb.append(args[i]);
			}
		} else if (len == 1) {
			return args[0];
		} else {
			return "connectDir 参数为空";
		}
		return sb.toString();
	}
}
