package com.merchant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dinpay.plugin.activity.DinpayChannelActivity;
import com.itrus.util.sign.RSAWithHardware;
import com.itrus.util.sign.RSAWithSoftware;
import com.merchant.R;
import com.merchant.model.OrderInfo;
import com.merchant.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class MainActivity extends Activity {

    private EditText edt_merchantcode, edt_notifyurl, edt_version, edt_orderno,
            edt_ordertime, edt_orderamount, edt_productname, edt_redoflag, edt_productcode, edt_productnum,
            edt_produnctdesc, edt_extra_return_param;
    private Button btn_submit;
    private RadioGroup radiogroup;
    private RadioButton radioRSA_S, radioRSA;

    private String merchant_code, notify_url, interface_version, sign_type,
            order_no, order_time, order_amount, product_name, redo_flag, product_code,
            product_num, product_desc, extra_return_param;
    private Context context;
    private String UserKey = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        edt_merchantcode = (EditText) this.findViewById(R.id.edt_merchantcode);
        edt_notifyurl = (EditText) this.findViewById(R.id.edt_notifyurl);
        edt_version = (EditText) this.findViewById(R.id.edt_version);

        edt_orderno = (EditText) this.findViewById(R.id.edt_orderno);
        edt_ordertime = (EditText) this.findViewById(R.id.edt_ordertime);
        edt_orderamount = (EditText) this.findViewById(R.id.edt_orderamount);
        edt_productname = (EditText) this.findViewById(R.id.edt_productname);
        edt_redoflag = (EditText) this.findViewById(R.id.edt_redoflag);
        edt_productcode = (EditText) this.findViewById(R.id.edt_productcode);
        edt_productnum = (EditText) this.findViewById(R.id.edt_productnum);
        edt_produnctdesc = (EditText) this.findViewById(R.id.edt_produnctdesc);
        edt_extra_return_param = (EditText) this.findViewById(R.id.edt_extra_return_param);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioRSA_S = (RadioButton) findViewById(R.id.radioRSA_S);
        radioRSA = (RadioButton) findViewById(R.id.radioRSA);
        btn_submit = (Button) this.findViewById(R.id.btn_submit);

        edt_merchantcode.setText("");//商户号(商户需替换成自己的商户号)
        edt_notifyurl.setText("http://192.168.1.178:3080/return/return.jsp");//服务器异步通知地址(商户需替换成自己的服务通知地址)
//		edt_notifyurl.setText("http://192.168.1.80:8080/index.jsp");//服务器异步通知地址(商户需替换成自己的服务通知地址)
        edt_version.setText("V3.0");//接口版本
        edt_orderno.setText(String.valueOf(System.currentTimeMillis()));//商户网站唯一订单号
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        edt_ordertime.setText(formatter.format(new Date()));//商户订单时间
        edt_orderamount.setText("0.01");//商户订单总金额
        edt_productname.setText("productname");//商品名称
        edt_redoflag.setText("1");//订单是否允许重复标识  可选

        edt_productcode.setText("");//商品编号   可选
        edt_productnum.setText("");//商品数量   可选
        edt_produnctdesc.setText("");//商品描述   可选
        edt_extra_return_param.setText("");//公用回传参数    可选

        sign_type="MD5";//初始化
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == radioRSA_S.getId()) {
                    sign_type = "RSA-S";
                } else {
                    sign_type = "RSA";
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pay();
            }
        });
    }

    @Override
    protected void onResume() {
        edt_orderno.setText(String.valueOf(System.currentTimeMillis()));//商户网站唯一订单号
        super.onResume();
    }
    private String MERCHANT_TOKEN = "2000633325";
    private String MERCHANT_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOG7lRM12rg/UgRe" +
            "G8fD35DLVEY80igKSt6ytxmudzd25y/eqUd2q3/SBIxL1WD6xx7SCPymkU8WYJBn" +
            "Phy8GbJxrZJjfcMVvkOICz5fjocSE+/+FD0kVGxaaacYthITIJfaEdUdh5XyYdZv" +
            "2NEjK23YW7OsNHqW9zfFRPc+0/dtAgMBAAECgYEAylCHkgoomA4YglWebHK/w1SZ" +
            "mcIVUVG4PNTMirX1n75wAlV8PtK7bvpanSuyeRMKuDLjXx4jClEyBvqhjruAAuHH" +
            "vr+iVOBxRHTxD+2kv75scQ4DzXy0V0FRaWv6eGMcbthLvADcWnO/U8hVpEp1zLRA" +
            "ZBdm1jH0Igiw7bmA0jkCQQD7ov5GNfhTC47crEK9SXRmlRRXPkDK3alL6QHj4BOl" +
            "iZqdPd59EFFfwTxH/xmnWtblko4X4JuYP5y+1EN108eLAkEA5aWaMU6Aa97i+u4i" +
            "EkI5qccNQZ03WbVvXFJz1D82E8CPKQem5spbspnf/IW6lhqZDIM4WyRm3fectmz6" +
            "e/Xb5wJAdXIxs5tk932hhCVyUN6D0Y0rHT0VCJpEdgWeuDjRcqWy4EkMGtsO395U" +
            "wFW0J4QiYKvyXfqbJIgsJHT1t3zM2QJBANiuia/OX1Po6YhU2ucZf6kXPQXVHt3R" +
            "WPLmhY4V2qOGhE31f2CyPRVnJTXyxFxPWmu5AJrW3QzBTye96ha+o68CQQCFMVXL" +
            "1IrrN0d6n5DW4nK8ig/HBabUsVI2GODJyFNWpQ/peycJf8yaSf5NSiXOfOePo9lG" +
            "V/hUyP03UjK9hjeO";
    private void pay() {
        UserKey =MERCHANT_KEY;
        merchant_code = edt_merchantcode.getText().toString();
        notify_url = edt_notifyurl.getText().toString();
        interface_version = edt_version.getText().toString();
        order_no = edt_orderno.getText().toString();
        order_time = edt_ordertime.getText().toString();
        order_amount = edt_orderamount.getText().toString();
        product_name = edt_productname.getText().toString();
        redo_flag = edt_redoflag.getText().toString();
        product_code = edt_productcode.getText().toString();
        product_num = edt_productnum.getText().toString();
        product_desc = edt_produnctdesc.getText().toString();
        extra_return_param = edt_extra_return_param.getText().toString();
        merchant_code =MERCHANT_TOKEN;
        OrderInfo info = new OrderInfo();

        info.setMerchant_code(merchant_code);
        info.setNotify_url(notify_url);
        info.setInterface_version(interface_version);
        info.setOrder_no(order_no);
        info.setOrder_time(order_time);
        info.setOrder_amount(order_amount);
        info.setProduct_name(product_name);
//        info.setRedo_flag(redo_flag);//订单是否允许重复标识  可选
//        info.setProduct_code(product_code);//商品编号   可选
//        info.setProduct_num(product_num);//商品数量   可选
//        info.setProduct_desc(product_desc);//商品描述   可选
//        info.setExtra_return_param(extra_return_param);//公用回传参数    可选

        //组织签名规则格式
        Map<String, String> maps = info.getMap();
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            String value = entry.getValue();
            if (!TextUtils.isEmpty(value)) {
                sb.append(entry.getKey() + "=" + value + "&");
            }
        }

        //商户替换自己的key
        String sign = sb.toString()+"key=bd1e58b0d25bab1414e64c9403703da7";
        try {
            sign= DigestUtils.md5Hex(sign.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        Log.e("aaa","sign"+sign);

        if(merchant_code.equals("")){
            Toast.makeText(getApplicationContext(), "请输入您注册的商家号",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(UserKey.equals("")){
            Toast.makeText(getApplicationContext(), "请输入您从后台获取的秘钥信息",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(sign.equals("")){
            Toast.makeText(getApplicationContext(), "请填写您证书的服务器端地址",
                    Toast.LENGTH_SHORT).show();
           return;
        }

        //组织报文
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<dinpay><request><merchant_code>" + info.getMerchant_code() + "</merchant_code>" +
                "<notify_url>" + info.getNotify_url() + "</notify_url>" +
                "<interface_version>" + info.getInterface_version() + "</interface_version>" +
                "<sign_type>" + sign_type + "</sign_type>" +
                "<sign>" + sign + "</sign>" +
                "<trade><order_no>" + info.getOrder_no() + "</order_no>" +
                "<order_time>" + info.getOrder_time() + "</order_time>" +
                "<order_amount>" + info.getOrder_amount() + "</order_amount>" +
                "<product_name>" + info.getProduct_name() + "</product_name>" +
                "<redo_flag>" + info.getRedo_flag() + "</redo_flag>" +
                "<product_code>" + info.getProduct_code() + "</product_code>" +
                "<product_num>" + info.getProduct_num() + "</product_num>" +
                "<product_desc>" + info.getProduct_desc() + "</product_desc>" +
                "<extra_return_param>" + info.getExtra_return_param() + "</extra_return_param>" +
                "</trade></request></dinpay>";
        Log.i("xml=", xml);
        Intent intent = new Intent(this, DinpayChannelActivity.class);
        intent.putExtra("xml", xml);
        intent.putExtra("ActivityName", "com.merchant.activity.MerchantPayResultActivity");
        startActivity(intent);
    }
//--------------------数据签名，支持RSA，和RSA_S两种签名方式，在后台进行数据签名传给客户端调起插件-----------------------------
//--------------------具体后台签名规则，签名方法见文档及后台开发包---------------------------------------------------------
    /**
     * RSA-s签名
     */
    private String getRSASSignature(String plainText) throws Exception {

        //商户秘钥,可以自己生成，也可以调用这里面的方法生成
        //注意，必须将秘钥上传到我们的智付商家后台，不然无法调用插件
        // （具体操作请参看我们RSA-S秘钥对的生成与使用文档）

//        Map<String, Object> keyMap2 = RSAWithSoftware.genKeyPair();
//        String UserKey = RSAWithSoftware.getPrivateKey(keyMap2);

        if(UserKey.equals("")){
            return "";
        }
        String signData = RSAWithSoftware.signByPrivateKey(plainText, UserKey);

        signData = signData.replaceAll("\\+", "%2B");

        return signData;
    }

    /**
     * RSA签名
     */
    private String getRSASignature(String plainText) throws Exception {

//        String path = Environment
//                .getExternalStorageDirectory() + "/" + "1234567890.pfx";

        String path="";//商户自己填写服务端的证书路径，证书从我们的官方商家后台里面下载

        if(path.equals("")){
            return "";
        }

        RSAWithHardware mh = new RSAWithHardware();
        mh.initSigner(path, "");// 商家pfx文件密码
        String signedData = mh.signByPriKey(plainText);
        System.out.println("商家发送的签名：" + signedData.length() + " =>" + signedData);

        return signedData;
    }
}
