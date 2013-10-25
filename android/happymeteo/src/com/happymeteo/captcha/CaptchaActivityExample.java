package com.happymeteo.captcha;

import android.app.Activity;
import android.os.Bundle;

public class CaptchaActivityExample extends Activity {

//	ImageView im = null;
//	Button btn = null;
//	TextView ans = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*setContentView(R.layout.main);
        im = (ImageView)findViewById(R.id.imageView1);
        btn = (Button)findViewById(R.id.button1);
        ans = (TextView)findViewById(R.id.textView1);
        
        btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Captcha c = new MathCaptcha(300, 100, MathOptions.PLUS_MINUS_MULTIPLY); 
				
				Log.i("Captcha", "width: "+v.getWidth());
				
				Captcha c = new TextCaptcha(v.getWidth(), 50, 5, TextOptions.NUMBERS_AND_LETTERS);
				im.setImageBitmap(c.image);
				im.setLayoutParams(new LinearLayout.LayoutParams(c.getWidth() *2, c.getHeight() *2));
				ans.setText(c.answer);
			}
		});*/
    }
}