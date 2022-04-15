package com.example.clientapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import SeperatePackage.aidlInterface;
import SeperatePackage.aidlInterface.Stub;


public class MainActivity extends Activity implements View.OnClickListener {

    private TextView Display_result;
    private EditText First_value,Second_value;
    private Button Add,Subtract,Multiply,Divide,Clear;
    private aidlInterface aidlObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display_result=findViewById(R.id.disply_result);
        First_value=findViewById(R.id.first_value);
        Second_value=findViewById(R.id.second_value);
        Add=findViewById(R.id.addition);
        Subtract=findViewById(R.id.subtract);
        Multiply=findViewById(R.id.multiply);
        Divide=findViewById(R.id.division);
        Clear=findViewById(R.id.clear_data);

        Add.setOnClickListener(this);
        Subtract.setOnClickListener(this);
        Multiply.setOnClickListener(this);
        Divide.setOnClickListener(this);
        Clear.setOnClickListener(this);

        bindToAIDLService();
    }

    private void bindToAIDLService() {
        Intent aidlServiceaIntent=new Intent("connect_to_aidl_service");

        bindService( implicitIntentToExplicitIntent(aidlServiceaIntent,this),
                serviceConnectionObject,BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnectionObject=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
           aidlObject=SeperatePackage.aidlInterface.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public Intent implicitIntentToExplicitIntent(Intent implicitIntent, Context context)
    {
        PackageManager packageManager=context.getPackageManager();
        List<ResolveInfo> resolveInfoList=packageManager.queryIntentServices(implicitIntent,0);
        if(resolveInfoList==null || resolveInfoList.size()!=1)
        {
            return null;
        }
        ResolveInfo serviceInfo=resolveInfoList.get(0);
        ComponentName component =new ComponentName(serviceInfo.serviceInfo.packageName,
                serviceInfo.serviceInfo.name);
        Intent explicitIntent=new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.addition:
                verifyAndCalculate(1);
                break;

            case R.id.subtract:
                verifyAndCalculate(2);
                break;

            case R.id.multiply:
                verifyAndCalculate(3);
                break;

            case R.id.division:
                verifyAndCalculate(4);
                break;

            case R.id.clear_data:
                First_value.setText(null);
                Second_value.setText(null);
                Display_result.setText(null);
                break;

            default:
                Log.i("Error","Default case");
        }
    }

    private void verifyAndCalculate(int operationType)
    {
        if(isAnyValueMissing())
        {
            Toast.makeText(this,"Please Enter Both the values",Toast.LENGTH_SHORT).show();
        }
        else
        {
            int result,firstValue,secondValue;
            firstValue=Integer.parseInt(First_value.getText().toString());
            secondValue=Integer.parseInt(Second_value.getText().toString());

            try {
               result= aidlObject.calculateData(firstValue, secondValue, operationType);
                Display_result.setText(""+result);
            }catch (RemoteException e)
            {
                e.printStackTrace();
            }

        }
    }

//Se

    private boolean isAnyValueMissing()
    {
        return First_value.getText().toString().isEmpty() && Second_value.getText().
                toString().isEmpty();
    }
}
