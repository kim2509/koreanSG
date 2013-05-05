package com.krking;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends BaseActivity implements AnimationListener, OnItemClickListener, OnClickListener{

	View menu;
	View app;
	BaseFragment activeFragment = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	try
    	{
    		super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
    		
    		menu = findViewById(R.id.menu);
            app = findViewById(R.id.app);
            
            FragmentManager fragmentManager = getSupportFragmentManager();
            
        	if ( fragmentManager != null )
        	{
        		LinearLayout l = (LinearLayout) app;
        		l.removeAllViews();
        		
        		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        		PredictionFragment fragment = new PredictionFragment( this, "K" );
        		activeFragment = fragment;
        		fragmentTransaction.add(R.id.app, fragment);
        		fragmentTransaction.commit();
        	}
        	
        	initializeControls();
        	
        	execTransReturningString("krSetting/setMenu.aspx", 1, null );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    public void initializeControls()
    {
    	Display display = getWindowManager().getDefaultDisplay();
    	menu.getLayoutParams().width = (int) (display.getWidth() * 0.8);
    	
    	ImageView imgBanner = (ImageView) findViewById(R.id.imgBanner);
    	imgBanner.setOnClickListener( this );
    	
    	ArrayList<JSONObject> data = new ArrayList<JSONObject>();
    	ListView listView = (ListView) menu.findViewById(R.id.menuList);
    	listView.setAdapter( new MenuAdapter(this, data));
    	listView.setOnItemClickListener( this );
    }

    @Override
    public void doPostTransaction(int requestCode, String result) {
    	// TODO Auto-generated method stub
    	try
    	{
    		super.doPostTransaction(requestCode, result);
    	
    		JSONObject jsonObj = new JSONObject( result );
    		jsonObj = jsonObj.getJSONObject("TopBanner");
        	
        	ImageView imgBanner = (ImageView) findViewById(R.id.imgBanner);
        	
        	// Get singletone instance of ImageLoader
    		ImageLoader imageLoader = ImageLoader.getInstance();
    		// Initialize ImageLoader with configuration. Do it once.
    		imageLoader.init(ImageLoaderConfiguration.createDefault( this ));
    		// Load and display image asynchronously
    		imageLoader.displayImage( jsonObj.getString("i"), imgBanner);
    		
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void initMenuList() throws Exception
    {
    	ArrayList<JSONObject> data = new ArrayList<JSONObject>();
    	
    	JSONObject item = new JSONObject();
    	item.put("TYPE", "HEADER");
    	item.put("NAME", "쇼킹TV(방송)");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "금주 방송");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "지난주 방송");
    	data.add( item );
    	
    	item = new JSONObject();
    	item.put("TYPE", "HEADER");
    	item.put("NAME", "새벽조교");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "새벽조");
    	data.add( item );
    	
    	item = new JSONObject();
    	item.put("TYPE", "HEADER");
    	item.put("NAME", "전문가 예상");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "경마왕");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "프리");
    	data.add( item );
    	
    	item = new JSONObject();
    	item.put("TYPE", "HEADER");
    	item.put("NAME", "SMS 적중 TOP");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "일간 BEST");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "주간 BEST");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "월간 BEST");
    	data.add( item );
    	
    	item = new JSONObject();
    	item.put("TYPE", "HEADER");
    	item.put("NAME", "예상지");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "예상지");
    	data.add( item );
    	
    	item = new JSONObject();
    	item.put("TYPE", "HEADER");
    	item.put("NAME", "출마 인기도");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "금요");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "토요");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "일요");
    	data.add( item );
    	
    	item = new JSONObject();
    	item.put("TYPE", "HEADER");
    	item.put("NAME", "경마자료");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "출마표");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "경주결과");
    	data.add( item );
    	
    	item = new JSONObject();
    	item.put("TYPE", "HEADER");
    	item.put("NAME", "커뮤니티");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "자유게시판");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "예상/복기");
    	data.add( item );
    	
    	item = new JSONObject();
    	item.put("TYPE", "HEADER");
    	item.put("NAME", "기타");
    	data.add( item );
    	
    	if ( "".equals( getMetaInfoString("uid") ) )
    	{
    		item = new JSONObject();
        	item.put("TYPE", "ITEM");
        	item.put("NAME", "회원가입");
        	data.add( item );
        	item = new JSONObject();
        	item.put("TYPE", "ITEM");
        	item.put("NAME", "로그인");
        	data.add( item );
    	}
    	else
    	{
    		item = new JSONObject();
        	item.put("TYPE", "ITEM");
        	item.put("NAME", "로그아웃");
        	data.add( item );
    	}
    	
    	ListView listView = (ListView) menu.findViewById(R.id.menuList);
    	MenuAdapter adapter = (MenuAdapter) listView.getAdapter();
    	adapter.setData( data );
    	adapter.notifyDataSetChanged();
    }
    
    public class MenuAdapter extends BaseAdapter {
        
        private Activity activity;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater=null;
        
        public MenuAdapter(Activity a, ArrayList<JSONObject> d) {
            activity = a;
            data=d;
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return data.size();
        }

        public ArrayList<JSONObject> getData()
        {
        	return data;
        }
        
        public void setData( ArrayList<JSONObject> d )
        {
        	this.data = d;
        }
        
        public Object getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent) {
        	try
        	{
        		View vi=convertView;
        		
        		JSONObject jsonObj = data.get(position);

        		if ( "HEADER".equals( jsonObj.getString("TYPE") ))
        			vi = inflater.inflate(R.layout.list_menu_header, null);
        		else
        		{
        			vi = inflater.inflate(R.layout.list_menu_item, null);
        			
        			ImageView iv = (ImageView) vi.findViewById(R.id.list_image);
        			
        			if ( "금주 방송".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.broadcasting_this_week);
        			else if ( "지난주 방송".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.broadcasting_last_week);
        			else if ( "경마왕".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu01_1_prediction_pro2);
        			else if ( "프리".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu01_2_prediction_free2);
        			else if ( "일간 BEST".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu02_1_best_day2);
        			else if ( "주간 BEST".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu02_2_best_week2);
        			else if ( "월간 BEST".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu02_3_best_month2);
        			else if ( "예상지".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu03_paper);
        			else if ( "금요".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu04_1_friday2);
        			else if ( "토요".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu04_2_saturday2);
        			else if ( "일요".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu04_3_sunday2);
        			else if ( "출마표".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu05_1_schedule2);
        			else if ( "경주결과".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu05_2_result2);
        			else if ( "자유게시판".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu06_1_bulletin2);
        			else if ( "예상/복기".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu06_2_remem2);
        			else if ( "회원가입".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu08_1_signin2);
        			else if ( "로그인".equals( jsonObj.getString("NAME") ) || "로그아웃".equals( jsonObj.getString("NAME") ))
        				iv.setImageResource(R.drawable.menu08_2_login2);
        			else if ( "새벽조".equals( jsonObj.getString("NAME") ) )
        				iv.setImageResource(R.drawable.menu_dawn);
        		}

                TextView title = (TextView)vi.findViewById(R.id.title); // title
                
                vi.setTag( jsonObj );
                
                title.setText( jsonObj.getString("NAME") );
                
                return vi;	
        	}
        	catch( Exception ex )
        	{
        	}
        	
        	return null;
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        
    	try
    	{
    		ListView lv = (ListView ) parent;
        	
        	MenuAdapter adapter = (MenuAdapter) lv.getAdapter();
        	JSONObject obj = (JSONObject) adapter.getItem(position);
        	
        	if ( "금주 방송".equals( obj.getString("NAME") ) )
        	{
        		Intent intent = new Intent( this , ShowkingActivity.class );
        		intent.putExtra("requestCode", "1");
        		startActivity(intent);
        		return;
        	}
    		else if ( "지난주 방송".equals( obj.getString("NAME") ) )
        	{
    			Intent intent = new Intent( this , ShowkingActivity.class );
        		intent.putExtra("requestCode", "2");
        		startActivity(intent);
        		return;
        	}
    		else if ( "경마왕".equals( obj.getString("NAME") ) )
        	{
        		activeFragment = new PredictionFragment( this, "K" );
        	}
        	else if ( "프리".equals( obj.getString("NAME") ))
        		activeFragment = new PredictionFragment( this, "F" );
        	else if ( "일간 BEST".equals( obj.getString("NAME")) )
        		activeFragment = new SMSTopFragment( this, 1 );
        	else if ("주간 BEST".equals( obj.getString("NAME")) )
        		activeFragment = new SMSTopFragment( this, 2 );
        	else if ("월간 BEST".equals( obj.getString("NAME")) )
        		activeFragment = new SMSTopFragment( this, 3 );
        	else if ("예상지".equals( obj.getString("NAME")) )
        		activeFragment = new PaperViewFragment( this );
        	else if ("금요".equals( obj.getString("NAME")) )
        		activeFragment = new HorseRacePopularityFragment( this , 1 );
        	else if ("토요".equals( obj.getString("NAME")) )
        		activeFragment = new HorseRacePopularityFragment( this , 2 );
        	else if ("일요".equals( obj.getString("NAME")) )
        		activeFragment = new HorseRacePopularityFragment( this , 3 );
        	else if ("출마표".equals( obj.getString("NAME")) )
        		activeFragment = new HorseRaceScheduleFragment( this, 1 );
        	else if ("경주결과".equals( obj.getString("NAME")) )
        		activeFragment = new HorseRaceResultFragment( this, 1 );
        	else if ("자유게시판".equals( obj.getString("NAME")) )
        		activeFragment = new FreeBoardFragment( this , 1 ); 
        	else if ("예상/복기".equals( obj.getString("NAME")) )
        		activeFragment = new FreeBoardFragment( this , 2 ); 
        	else if ("로그인".equals( obj.getString("NAME")) )
        		activeFragment = new LoginFragment( this );
        	else if ("로그아웃".equals( obj.getString("NAME")) )
        	{
        		setMetaInfo("uid", "");
        		setMetaInfo("upwd", "");
        		showToastMessage("로그아웃되었습니다.");
        		showMainMenu( null );
        		return;
        	}
        	else if ("회원가입".equals( obj.getString("NAME")) )
        		activeFragment = new RegisterMemberFragment( this );
        	else if ("새벽조".equals( obj.getString("NAME")) )
        	{
        		Intent intent = new Intent( this , DawnActivity.class );
        		intent.putExtra("requestCode", "2");
        		startActivity(intent);
        		return;
        	}
        	
        	showMainMenu( null );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
    	}
    }
    
    public void goToFreeBoard()
    {
    	activeFragment = new FreeBoardFragment( this, 1 );
    	
    	FragmentManager fragmentManager = getSupportFragmentManager();

        if ( activeFragment != null)
        {
        	LinearLayout l = (LinearLayout) app;
        	l.removeAllViews();
        	
        	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	fragmentTransaction.replace(R.id.app, activeFragment);
        	fragmentTransaction.commit();	
        }
    }
    
    public void goToHorseRaceSchedule( int param )
    {
    	activeFragment = new HorseRaceScheduleFragment( this, param );
    	
    	FragmentManager fragmentManager = getSupportFragmentManager();

        if ( activeFragment != null)
        {
        	LinearLayout l = (LinearLayout) app;
        	l.removeAllViews();
        	
        	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	fragmentTransaction.replace(R.id.app, activeFragment);
        	fragmentTransaction.commit();	
        }
    }
    
    public void goToHorseRacePopularity( int param )
    {
    	activeFragment = new HorseRacePopularityFragment( this, param );
    	
    	FragmentManager fragmentManager = getSupportFragmentManager();

        if ( activeFragment != null)
        {
        	LinearLayout l = (LinearLayout) app;
        	l.removeAllViews();
        	
        	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	fragmentTransaction.replace(R.id.app, activeFragment);
        	fragmentTransaction.commit();	
        }
    }
    
    boolean menuOut = false;
    
    static class AnimParams {
        int left, right, top, bottom;

        void init(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }
    
    AnimParams animParams = new AnimParams();
    
    public void showMainMenu( View v )
    {
    	try
    	{
    		if ( v != null )
    			activeFragment = null;
    		
            Animation anim;

            int w = app.getMeasuredWidth();
            int h = app.getMeasuredHeight();
            int left = (int) (app.getMeasuredWidth() * 0.8);

            if (!menuOut) {
            	
            	initMenuList();
            	
                // anim = AnimationUtils.loadAnimation(context, R.anim.push_right_out_80);
                anim = new TranslateAnimation(0, left, 0, 0);
                menu.setVisibility(View.VISIBLE);
                animParams.init(left, 0, left + w, h);
            } else {
                // anim = AnimationUtils.loadAnimation(context, R.anim.push_left_in_80);
                anim = new TranslateAnimation(0, -left, 0, 0);
                animParams.init(0, 0, w, h);
            }

            anim.setDuration(300);
            anim.setAnimationListener( this );
            app.startAnimation(anim);
    	}
    	catch( Exception ex )
    	{
    	}
    }
    
    @Override
    public void onAnimationEnd(Animation animation) {
        System.out.println("onAnimationEnd");
        menuOut = !menuOut;
        if (!menuOut) {
            menu.setVisibility(View.INVISIBLE);
        }
        layoutApp(menuOut);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if ( !menuOut && fragmentManager != null && activeFragment != null)
        {
        	LinearLayout l = (LinearLayout) app;
        	l.removeAllViews();
        	
        	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        	fragmentTransaction.replace(R.id.app, activeFragment);
        	fragmentTransaction.commit();	
        }
    }
    
    
    @Override
    public void onAnimationRepeat(Animation animation) {
        System.out.println("onAnimationRepeat");
    }

    @Override
    public void onAnimationStart(Animation animation) {
        System.out.println("onAnimationStart");
    }
    
    void layoutApp(boolean menuOut) {
        System.out.println("layout [" + animParams.left + "," + animParams.top + "," + animParams.right + ","
                + animParams.bottom + "]");
        app.layout(animParams.left, animParams.top, animParams.right, animParams.bottom);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		activeFragment = new ShockingFragment( this );
//		showMainMenu( null );
		
		activeFragment = new ShowKingHomeFragment( this );
		showMainMenu( null );
	}
}
