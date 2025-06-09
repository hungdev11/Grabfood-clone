package com.app.grabfoodapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.grabfoodapp.R;

public class MomoReturnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_momo_return);

        ImageView imgResult = findViewById(R.id.resultIcon);
        TextView resultMessage = findViewById(R.id.resultMessage);
        TextView orderIdText = findViewById(R.id.orderIdText);
        TextView btnBackHome = findViewById(R.id.btnBackHome);

        Intent intent  = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String orderId = data.getQueryParameter("orderId");
            String resultCodeString = data.getQueryParameter("resultCode");
            int resultCode = (resultCodeString != null) ? Integer.parseInt(resultCodeString) : -1;

            if (resultCode == 0) {
                imgResult.setImageResource(R.drawable.success);
                resultMessage.setText("Thanh toán thành công!");
            } else {
                imgResult.setImageResource(R.drawable.unsuccess);
                resultMessage.setText("Thanh toán thất bại!");
            }

            if (orderId != null) {
                orderIdText.setText("Mã đơn hàng: " + orderId);
            }
        }

        btnBackHome.setOnClickListener(v -> {
            Intent intent1 = new Intent(MomoReturnActivity.this, MainActivity.class);
            startActivity(intent1);
        });

    }
}