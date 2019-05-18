package mkk.thinkunlike.aidetour;

import android.app.*;
import android.content.*;
import android.os.*;

public class SplashActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
		
		new Handler().postDelayed(mRunnable,4000);
    }
	
	private void wizz() {
		startActivity(new Intent(this,MainActivity.class));
		finish();
	}
	
	Runnable mRunnable = new Runnable(){

		@Override
		public void run()
		{
			// TODO: Implement this method
			wizz();
		}
	};
}
