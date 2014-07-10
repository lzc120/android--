package cn.itcast.mobilesafe.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;


/**
 * 保证这个类只存在一个实例 
 * 
 *
 */
public class GPSInfoProvider {
	LocationManager manager;
	private static GPSInfoProvider mGPSInfoProvider;
	private static Context context;
	private static MyLoactionListener listener;
  //1.私有化构造方法
	
	private GPSInfoProvider(){};
	
  //2. 提供一个静态的方法 可以返回他的一个实例
	public static synchronized GPSInfoProvider getInstance(Context context){
		if(mGPSInfoProvider==null){
			mGPSInfoProvider = new GPSInfoProvider();
			GPSInfoProvider.context = context;
		}
		return mGPSInfoProvider;
	}
	
	
	// 获取gps 信息 
	public String getLocation(){
	//获取系统服务需要用到Context,比如manager之类
		manager =(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//manager.getAllProviders(); // gps //wifi //
		String provider = getProvider(manager);
		// 注册位置的监听器 
		//(提供方式,更新频率,位移频率,监听器)
		manager.requestLocationUpdates(provider,60000, 50, getListener());
		//取得位置信息
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		String location = sp.getString("location", "");
		return location;
	}
	

	
	// 停止gps监听
	public void stopGPSListener(){
		manager.removeUpdates(getListener());
	}
	
	//单例监听器
	private synchronized MyLoactionListener getListener(){
		if(listener==null){
			listener = new MyLoactionListener();
		}
		return listener;
	}
	
	private class MyLoactionListener implements LocationListener{

		/**
		 * 当手机位置发生改变的时候 调用的方法
		 */
		public void onLocationChanged(Location location) {
			String latitude ="latitude "+ location.getLatitude(); //weidu 维度
			String longtitude = "longtitude "+ location.getLongitude(); //jingdu 经度
			//保存位置信息
			SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("location", latitude+" - "+ longtitude);
			editor.commit(); //最后一次获取到的位置信息 存放到sharedpreference里面
		}

		/**
		 * 某一个设备的状态发生改变的时候 调用 可用->不可用  不可用->可用
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		/**
		 * 某个设备(gps,网络等)被打开
		 */
		public void onProviderEnabled(String provider) {
			
		}

		/**某个设备被禁用
		 * 
		 */
		public void onProviderDisabled(String provider) {
			
		}
		
	}
	
	/**\
	 * 
	 * @param manager 位置管理服务
	 * @return 最好的位置提供者
	 */
	private String getProvider(LocationManager manager){
		Criteria criteria = new Criteria();//查询条件的类,相当于集合
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//精度标准
		criteria.setAltitudeRequired(false);//不用已关闭设备
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);//耗电量
		criteria.setSpeedRequired(true);//速度敏感
		criteria.setCostAllowed(true);//其它开销(gps,网络等)
		return  manager.getBestProvider(criteria, true);
	}
}

/*权限:<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
	//ACCESS_MOCK_LOCATION  模拟器上用
    <uses-permission android:name="android.permission.ACCESS_MOCK_LOCATION" />
	
*/















