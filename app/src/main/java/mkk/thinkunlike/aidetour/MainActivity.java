package mkk.thinkunlike.aidetour;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.mkk.aidecodeformatter.*;
import java.io.*;
import android.widget.AbsListView.*;
import android.view.animation.*;
import android.content.*;

public class MainActivity extends Activity 
{
	LinearLayout codeView;
	TextView talksTv;
	Button previousBtn, talksBtn, nextBtn;
	
	AIDECodeFormatter mCodeHighlighter;
	
	int currentLesson = 1;
	int LAST_LESSON = 14;
	SharedPreferences mSharedPreferences;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		codeView = from(R.id.codeView);
		talksTv = from(R.id.talksTv);
		previousBtn = from(R.id.previousBtn);
		talksBtn = from(R.id.talksBtn);
		nextBtn = from(R.id.nextBtn);
		
		talksTv.setBackgroundColor(Color.argb(200,0,0,0));
		
		mCodeHighlighter = new AIDECodeFormatter(this);
		mSharedPreferences = getSharedPreferences("Data",MODE_PRIVATE);
		currentLesson = mSharedPreferences.getInt("lesson",1);
		createLesson();
    }
	
	public void previous(View v) {
		if(currentLesson > 1) {
			currentLesson--;
			createLesson();
		}
	}
	
	public void talks(View v) {
		if(talksTv.getVisibility() == View.GONE) {
			talksTv.setVisibility(View.VISIBLE);
			talksBtn.setText("Hide Talks");
		} else {
			talksTv.setVisibility(View.GONE);
			talksBtn.setText("Show Talks");
		}
	}
	
	public void next(View v) {
		if(currentLesson < LAST_LESSON) {
			currentLesson++;
			createLesson();
		}
	}
	
	AlphaAnimation aa = new AlphaAnimation(0,1);
	private void createLesson() {
		
		aa.setDuration(1500);
		talksTv.setAnimation(aa);
		aa.start();
		
		if(currentLesson == 1) {
			previousBtn.setAlpha(0.5f);
		} else {
			previousBtn.setAlpha(1f);
		}
		if(currentLesson == LAST_LESSON) {
			nextBtn.setAlpha(0.5f);
		} else {
			nextBtn.setAlpha(1f);
		}
		createTalks();
		createCodes();
	}
	
	
	
	private void createTalks() {
		try
		{
			talksTv.setText(readFileContents("talks/lesson" + currentLesson));
		}
		catch (IOException e)
		{
			talksTv.setText(e.getMessage());
		}
	}
	
	// 0 for .java file, 1 for .xml file, 2 for (AndroidManifest.xml) non-extension
	int attempt = 0;
	
	private void createCodes() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)talksTv.getLayoutParams();
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		
		InputStream is = null;
		String codefile = " ";
		
		// In every lesson we attempt to open code snippets file three times for code hightlighting.
		// First attempt tries to read the "lesson(number).java". 
		// If successful, do java code highlighting. 
		// If failed, go on second attempt for file with ".xml", and so on ...
		// If all three attempts failed, we know the lesson refers or contains no codes file to highlight.
		
		try {
			if(attempt == 0) {
				codefile = "codes/lesson" + currentLesson + ".java";
				is = getAssets().open(codefile);
				mCodeHighlighter.formatJava(codeView,codefile);
			} else if(attempt == 1) {
				codefile = "codes/lesson" + currentLesson + ".xml";
				is = getAssets().open(codefile);
				mCodeHighlighter.formatXML(codeView, codefile);
			} else if(attempt == 2) {
				codefile = "codes/lesson" + currentLesson;
				is = getAssets().open(codefile);
				mCodeHighlighter.formatAndroidManifest(codeView, codefile);
			} 
			attempt = 0;
			lp.removeRule(RelativeLayout.CENTER_IN_PARENT);
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			talksTv.setLayoutParams(lp);
		} catch (IOException ioe) {
			attempt++;
			if(attempt < 3) {
				createCodes();
			} else {
				lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				lp.addRule(RelativeLayout.CENTER_IN_PARENT);
				talksTv.setLayoutParams(lp);
				attempt = 0;
				codeView.removeAllViews();
			}
			
		}
	}
	
	private String readFileContents(String filename) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
		String line;
		while( (line = br.readLine()) != null) {
			sb.append(line + System.lineSeparator());
		}
		return sb.toString();
	}
	
	
	// Do you know we don't need "casting" in newer AIDE versions anymore.
	private <T extends View> T from(int id) {
		return (T) findViewById(id);
	}

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		mSharedPreferences.edit()
		.putInt("lesson",currentLesson)
		.commit();
		
		super.onDestroy();
	}
	
}
