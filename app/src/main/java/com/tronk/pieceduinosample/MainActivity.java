package com.tronk.pieceduinosample;
import android.app.Activity;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.tronk.PieceduinoRemote;

import org.json.JSONException;
import org.json.JSONObject;
public class MainActivity extends Activity {
    final String PieceduinoToken = "您的PiecedCloud Token";
    TextView dataMoniter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PieceduinoRemote.getInstance().Init(PieceduinoToken);
        dataMoniter = (TextView) this.findViewById(R.id.DataMoniter);

        Button GetClientIdBtn = (Button) this.findViewById(R.id.getClientId);
        GetClientIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取得目前在線的所有socket client id
                PieceduinoRemote.getInstance().GetClientsId(taskResultListener);
            }
        });
        Switch LEDSwitch = (Switch) this.findViewById(R.id.switch1);
        LEDSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //配合Pieceduino SocketIO 範例，控制pin13的led燈
                String value = isChecked ? "1" : "0";
                PieceduinoRemote.getInstance().SendDataAll("A", value, taskResultListener);
            }
        });
        Button uploadDataBtn = (Button) this.findViewById(R.id.saveData);
        uploadDataBtn.setTag(0);
        uploadDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //將json物件內的東西寫入到雲端
                JSONObject saveData = new JSONObject();
                try {
                    saveData.put("time", Long.toString(System.currentTimeMillis()));
                    saveData.put("clickTimes", v.getTag());
                    PieceduinoRemote.getInstance().SaveData(saveData, taskResultListener);

                    v.setTag((int) (v.getTag()) + 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Button getDataBtn = (Button) this.findViewById(R.id.getData);
        getDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //將雲端儲存的資料取回來
                PieceduinoRemote.getInstance().GetData(taskResultListener);
            }
        });
    }
    //Pieceduino API的監聽事件
    PieceduinoRemote.OnTaskResultListener taskResultListener  = new PieceduinoRemote.OnTaskResultListener() {
        @Override
        public void complete(JSONObject result) {
            dataMoniter.setText(result.toString());
        }
        @Override
        public void error(JSONObject result) {
            dataMoniter.setText(result.toString());
        }
    };
}
