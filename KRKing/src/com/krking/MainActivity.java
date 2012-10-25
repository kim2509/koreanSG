package com.krking;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
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

public class MainActivity extends BaseActivity implements AnimationListener, OnItemClickListener{

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
            
            initMenuList();
            
            FragmentManager fragmentManager = getSupportFragmentManager();
            
        	if ( fragmentManager != null )
        	{
        		LinearLayout l = (LinearLayout) app;
        		l.removeAllViews();
        		
        		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        		PredictionFragment fragment = new PredictionFragment( this, "K" );
        		fragmentTransaction.add(R.id.app, fragment);
        		fragmentTransaction.commit();
        	}
        	
        	initializeControls();
    	}
    	catch( Exception ex )
    	{
    		
    	}
    }
    
    public void initializeControls()
    {
    	TextView tv = (TextView) findViewById(R.id.menuTitle);
    	ViewGroup.LayoutParams lp = tv.getLayoutParams();
    	
    	Display display = getWindowManager().getDefaultDisplay();
    	lp.width = (int) (display.getWidth() * 0.8);
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
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "회원가입");
    	data.add( item );
    	item = new JSONObject();
    	item.put("TYPE", "ITEM");
    	item.put("NAME", "로그인");
    	data.add( item );
    	
    	ListView listView = (ListView) menu.findViewById(R.id.menuList);
    	listView.setAdapter( new MenuAdapter(this, data));
    	
    	listView.setOnItemClickListener( this );
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
        			vi = inflater.inflate(R.layout.list_menu_item, null);

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
        	
        	if ( "경마왕".equals( obj.getString("NAME") ) )
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
        	else if ("예상/복기".equals( obj.getString("NAME")) )
        		activeFragment = new HorseRaceExpectationFragment( this );
        	else if ("로그인".equals( obj.getString("NAME")) )
        		activeFragment = new LoginFragment( this );
        	else if ("회원가입".equals( obj.getString("NAME")) )
        		activeFragment = new RegisterMemberFragment( this );
        	
        	showMainMenu( null );
    	}
    	catch( Exception ex )
    	{
    		writeLog( ex.getMessage() );
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
        	fragmentTransaction.add(R.id.app, activeFragment);
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
}
