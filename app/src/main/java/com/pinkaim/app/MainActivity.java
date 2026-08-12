package com.pinkaim.app;
import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_CODE = 100;
    private static final int SCREEN_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = findViewById(R.id.startBtn);
        start.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), OVERLAY_CODE);
                return;
            }
            MediaProjectionManager pm = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            startActivityForResult(pm.createScreenCaptureIntent(), SCREEN_CODE);
        });
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == OVERLAY_CODE) {
            Toast.makeText(this, "Overlay дозволено", Toast.LENGTH_SHORT).show();
            return;
        }
        if (req == SCREEN_CODE && res == Activity.RESULT_OK) {
            Intent svc = new Intent(this, OverlayService.class);
            svc.putExtra("resultCode", res);
            svc.putExtra("data", data);
            startService(svc);
            finish();
        } else {
            Toast.makeText(this, "Потрібен скрінкаст", Toast.LENGTH_SHORT).show();
        }
    }
}
