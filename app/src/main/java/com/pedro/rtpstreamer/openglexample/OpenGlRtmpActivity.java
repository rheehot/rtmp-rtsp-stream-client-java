package com.pedro.rtpstreamer.openglexample;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.pedro.encoder.input.gl.render.filters.AndroidViewFilterRender;
import com.pedro.encoder.input.gl.render.filters.BasicDeformationFilterRender;
import com.pedro.encoder.input.gl.render.filters.BeautyFilterRender;
import com.pedro.encoder.input.gl.render.filters.BlurFilterRender;
import com.pedro.encoder.input.gl.render.filters.BrightnessFilterRender;
import com.pedro.encoder.input.gl.render.filters.CartoonFilterRender;
import com.pedro.encoder.input.gl.render.filters.ColorFilterRender;
import com.pedro.encoder.input.gl.render.filters.ContrastFilterRender;
import com.pedro.encoder.input.gl.render.filters.DuotoneFilterRender;
import com.pedro.encoder.input.gl.render.filters.EarlyBirdFilterRender;
import com.pedro.encoder.input.gl.render.filters.EdgeDetectionFilterRender;
import com.pedro.encoder.input.gl.render.filters.ExposureFilterRender;
import com.pedro.encoder.input.gl.render.filters.FireFilterRender;
import com.pedro.encoder.input.gl.render.filters.GammaFilterRender;
import com.pedro.encoder.input.gl.render.filters.GreyScaleFilterRender;
import com.pedro.encoder.input.gl.render.filters.HalftoneLinesFilterRender;
import com.pedro.encoder.input.gl.render.filters.Image70sFilterRender;
import com.pedro.encoder.input.gl.render.filters.LamoishFilterRender;
import com.pedro.encoder.input.gl.render.filters.MoneyFilterRender;
import com.pedro.encoder.input.gl.render.filters.NegativeFilterRender;
import com.pedro.encoder.input.gl.render.filters.NoFilterRender;
import com.pedro.encoder.input.gl.render.filters.PixelatedFilterRender;
import com.pedro.encoder.input.gl.render.filters.PolygonizationFilterRender;
import com.pedro.encoder.input.gl.render.filters.RainbowFilterRender;
import com.pedro.encoder.input.gl.render.filters.RippleFilterRender;
import com.pedro.encoder.input.gl.render.filters.SaturationFilterRender;
import com.pedro.encoder.input.gl.render.filters.SepiaFilterRender;
import com.pedro.encoder.input.gl.render.filters.SharpnessFilterRender;
import com.pedro.encoder.input.gl.render.filters.TemperatureFilterRender;
import com.pedro.encoder.input.gl.render.filters.ZebraFilterRender;
import com.pedro.encoder.utils.gl.GifStreamObject;
import com.pedro.encoder.utils.gl.ImageStreamObject;
import com.pedro.encoder.utils.gl.TextStreamObject;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.view.OpenGlView;
import com.pedro.rtpstreamer.R;
import java.io.IOException;
import net.ossrs.rtmp.ConnectCheckerRtmp;

/**
 * More documentation see:
 * {@link com.pedro.rtplibrary.base.Camera1Base}
 * {@link com.pedro.rtplibrary.rtmp.RtmpCamera1}
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OpenGlRtmpActivity extends AppCompatActivity
    implements ConnectCheckerRtmp, View.OnClickListener {

  private RtmpCamera1 rtmpCamera1;
  private Button button;
  private EditText etUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.activity_open_gl);
    OpenGlView openGlView = findViewById(R.id.surfaceView);
    button = findViewById(R.id.b_start_stop);
    button.setOnClickListener(this);
    etUrl = findViewById(R.id.et_rtp_url);
    etUrl.setHint(R.string.hint_rtmp);
    rtmpCamera1 = new RtmpCamera1(openGlView, this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.gl_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (rtmpCamera1.isStreaming()) {
      switch (item.getItemId()) {
        case R.id.e_d_fxaa:
          rtmpCamera1.enableAA(!rtmpCamera1.isAAEnabled());
          Toast.makeText(this, "FXAA " + (rtmpCamera1.isAAEnabled() ? " enabled" : "disabled"),
              Toast.LENGTH_SHORT).show();
          return true;
        //stream object
        case R.id.text:
          setTextToStream();
          return true;
        case R.id.image:
          setImageToStream();
          return true;
        case R.id.gif:
          setGifToStream();
          return true;
        case R.id.clear:
          rtmpCamera1.clearStreamObject();
          return true;
        //filters. NOTE: You can change filter values on fly without re set the filter.
        // Example:
        // ColorFilterRender color = new ColorFilterRender()
        // rtmpCamera1.setFilter(color);
        // color.setRGBColor(255, 0, 0); //red tint
        case R.id.no_filter:
          rtmpCamera1.setFilter(new NoFilterRender());
          return true;
        case R.id.android_view:
          AndroidViewFilterRender androidViewFilterRender = new AndroidViewFilterRender();
          androidViewFilterRender.setView(findViewById(R.id.activity_example_rtmp));
          rtmpCamera1.setFilter(androidViewFilterRender);
          return true;
        case R.id.basic_deformation:
          rtmpCamera1.setFilter(new BasicDeformationFilterRender());
          return true;
        case R.id.beauty:
          rtmpCamera1.setFilter(new BeautyFilterRender());
          return true;
        case R.id.blur:
          rtmpCamera1.setFilter(new BlurFilterRender());
          return true;
        case R.id.brightness:
          rtmpCamera1.setFilter(new BrightnessFilterRender());
          return true;
        case R.id.cartoon:
          rtmpCamera1.setFilter(new CartoonFilterRender());
          return true;
        case R.id.color:
          rtmpCamera1.setFilter(new ColorFilterRender());
          return true;
        case R.id.contrast:
          rtmpCamera1.setFilter(new ContrastFilterRender());
          return true;
        case R.id.duotone:
          rtmpCamera1.setFilter(new DuotoneFilterRender());
          return true;
        case R.id.early_bird:
          rtmpCamera1.setFilter(new EarlyBirdFilterRender());
          return true;
        case R.id.edge_detection:
          rtmpCamera1.setFilter(new EdgeDetectionFilterRender());
          return true;
        case R.id.exposure:
          rtmpCamera1.setFilter(new ExposureFilterRender());
          return true;
        case R.id.fire:
          rtmpCamera1.setFilter(new FireFilterRender());
          return true;
        case R.id.gamma:
          rtmpCamera1.setFilter(new GammaFilterRender());
          return true;
        case R.id.grey_scale:
          rtmpCamera1.setFilter(new GreyScaleFilterRender());
          return true;
        case R.id.halftone_lines:
          rtmpCamera1.setFilter(new HalftoneLinesFilterRender());
          return true;
        case R.id.image_70s:
          rtmpCamera1.setFilter(new Image70sFilterRender());
          return true;
        case R.id.lamoish:
          rtmpCamera1.setFilter(new LamoishFilterRender());
          return true;
        case R.id.money:
          rtmpCamera1.setFilter(new MoneyFilterRender());
          return true;
        case R.id.negative:
          rtmpCamera1.setFilter(new NegativeFilterRender());
          return true;
        case R.id.pixelated:
          rtmpCamera1.setFilter(new PixelatedFilterRender());
          return true;
        case R.id.polygonization:
          rtmpCamera1.setFilter(new PolygonizationFilterRender());
          return true;
        case R.id.rainbow:
          rtmpCamera1.setFilter(new RainbowFilterRender());
          return true;
        case R.id.ripple:
          rtmpCamera1.setFilter(new RippleFilterRender());
          return true;
        case R.id.saturation:
          rtmpCamera1.setFilter(new SaturationFilterRender());
          return true;
        case R.id.sepia:
          rtmpCamera1.setFilter(new SepiaFilterRender());
          return true;
        case R.id.sharpness:
          rtmpCamera1.setFilter(new SharpnessFilterRender());
          return true;
        case R.id.temperature:
          rtmpCamera1.setFilter(new TemperatureFilterRender());
          return true;
        case R.id.zebra:
          rtmpCamera1.setFilter(new ZebraFilterRender());
          return true;
        default:
          return false;
      }
    } else {
      return false;
    }
  }

  private void setTextToStream() {
    try {
      TextStreamObject textStreamObject = new TextStreamObject();
      textStreamObject.load("Hello world", 22, Color.RED);
      rtmpCamera1.setTextStreamObject(textStreamObject);
    } catch (IOException e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void setImageToStream() {
    try {
      ImageStreamObject imageStreamObject = new ImageStreamObject();
      imageStreamObject.load(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
      rtmpCamera1.setImageStreamObject(imageStreamObject);
    } catch (IOException e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  private void setGifToStream() {
    try {
      GifStreamObject gifStreamObject = new GifStreamObject();
      gifStreamObject.load(getResources().openRawResource(R.raw.banana));
      rtmpCamera1.setGifStreamObject(gifStreamObject);
    } catch (IOException e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onConnectionSuccessRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onConnectionFailedRtmp(final String reason) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
            .show();
        rtmpCamera1.stopStream();
        rtmpCamera1.stopPreview();
        button.setText(R.string.start_button);
      }
    });
  }

  @Override
  public void onDisconnectRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthErrorRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthSuccessRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(OpenGlRtmpActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onClick(View view) {
    if (!rtmpCamera1.isStreaming()) {
      if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
        button.setText(R.string.stop_button);
        rtmpCamera1.startStream(etUrl.getText().toString());
      } else {
        Toast.makeText(this, "Error preparing stream, This device cant do it", Toast.LENGTH_SHORT)
            .show();
      }
    } else {
      button.setText(R.string.start_button);
      rtmpCamera1.stopStream();
      rtmpCamera1.stopPreview();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (rtmpCamera1.isStreaming()) {
      rtmpCamera1.stopStream();
      rtmpCamera1.stopPreview();
    }
  }
}
