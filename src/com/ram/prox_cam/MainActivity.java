package com.ram.prox_cam;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.media.MediaPlayer;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;


public class MainActivity extends Activity implements SensorEventListener {
	private static final String TAG = "CamTestActivity";
	Preview preview;
	
	Button buttonClick;
	Camera camera;
	String fileName;
	MediaPlayer mp;
	int resId=R.raw.cam;
	Context con;
	Activity act;
	Context ctx;
	public int ctrl;
	private SensorManager mSensorManager;
	  private Sensor mProximity;
     
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		act = this;
		ctrl=0;
		//own
		
		
		
		//own ends
		
		mp=MediaPlayer.create(this, resId);
		 mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		    mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		
		preview = new Preview(this, (SurfaceView)findViewById(R.id.surfaceView));
		preview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		preview.setKeepScreenOn(true);
		
		
			
		buttonClick = (Button) findViewById(R.id.buttonClick);
		
		buttonClick.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});
		
		buttonClick.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View arg0) {
				camera.autoFocus(new AutoFocusCallback(){
					@Override
					public void onAutoFocus(boolean arg0, Camera arg1) {
						//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
					}
				});
				return true;
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//      preview.camera = Camera.open();
		camera = Camera.open();
		camera.startPreview();
		preview.setCamera(camera);
		ctrl=0;
		 mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	
	
	private void resetCam() {
		camera.startPreview();
		preview.setCamera(camera);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Log.d(TAG, "onShutter'd");
			mp.start();
			//mp.stop();
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// Log.d(TAG, "onPictureTaken - raw");
			
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			try {
				
				// Write to SD Card
				long time=System.currentTimeMillis();
				//String sd = Environment.getExternalStorageDirectory().getPath();
				fileName = String.format("/sdcard/camtest/%d.jpg",time);
				outStream = new FileOutputStream(fileName);
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
				
				
				
				
			
				
				
				
				
				
				
				resetCam();

			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
	
	@Override
	  public final void onAccuracyChanged(Sensor sensor, int accuracy) {
	    // Do something here if sensor accuracy changes.
	  }
//main proc
	  @Override
	  public final void onSensorChanged(SensorEvent event) {
	    
	    ctrl++;
	    if(ctrl<2)
	    {
	    	
	    	
	    	camera.takePicture(shutterCallback, rawCallback, jpegCallback);
	    }
	    if(ctrl>=2)
	    	ctrl=0;
	  }
//ends
	 
}
