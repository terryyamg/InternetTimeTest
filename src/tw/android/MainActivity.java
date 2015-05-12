package tw.android;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.time.TimeTCPClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView tvPhoneTime, tvInternetTime;
	private Button btUpdateTime, btChangeTime;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());

		tvPhoneTime = (TextView) findViewById(R.id.tvPhoneTime);
		tvInternetTime = (TextView) findViewById(R.id.tvInternetTime);
		btUpdateTime = (Button) findViewById(R.id.btUpdateTime);
		btChangeTime = (Button) findViewById(R.id.btChangeTime);

		/* 更新時間 */
		btUpdateTime.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				updateTime();
			}
		});

		/* 前往修改手機時間 */
		btChangeTime.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
				startActivity(intent);
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private void updateTime() {
		/* 手機時間 */
		SimpleDateFormat sTime = new SimpleDateFormat("HH:mm:ss");
		String time = sTime.format(new java.util.Date()); // 現在時間
		tvPhoneTime.setText(time);

		/* 網路時間 */
		try {
			TimeTCPClient client = new TimeTCPClient();
			try {
				client.setDefaultTimeout(60000); // 設定最長等待時間為60秒
				/* 可以前往 http://tf.nist.gov/tf-cgi/servers.cgi 選擇可用的連結點 */
				client.connect("time-nw.nist.gov"); // 取得網路時間
				String iT = sTime.format(client.getDate());// 格式處理

				tvInternetTime.setText(iT);
			} finally {
				client.disconnect(); // 關掉連結
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}