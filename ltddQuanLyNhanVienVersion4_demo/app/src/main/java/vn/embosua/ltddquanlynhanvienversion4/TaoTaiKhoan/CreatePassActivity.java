package vn.embosua.ltddquanlynhanvienversion4.TaoTaiKhoan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.huawei.agconnect.auth.AGConnectAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import vn.embosua.ltddquanlynhanvienversion4.ChiTiet.EditInforActivity;
import vn.embosua.ltddquanlynhanvienversion4.DangNhap.LogInActivity;
import vn.embosua.ltddquanlynhanvienversion4.DangNhap.SilentLoginActivity;
import vn.embosua.ltddquanlynhanvienversion4.DieuKien.CheckFormat;
import vn.embosua.ltddquanlynhanvienversion4.DieuKien.FormatCallBack;
import vn.embosua.ltddquanlynhanvienversion4.DuLieu.AGCManager;
import vn.embosua.ltddquanlynhanvienversion4.DuLieu.FbManeger;
import vn.embosua.ltddquanlynhanvienversion4.ManHinhChinh.UserActivity;
import vn.embosua.ltddquanlynhanvienversion4.Model.EditHistory;
import vn.embosua.ltddquanlynhanvienversion4.Model.PortraitActivity;
import vn.embosua.ltddquanlynhanvienversion4.Model.Staff;
import vn.embosua.ltddquanlynhanvienversion4.R;

import static com.huawei.agconnect.auth.VerifyCodeSettings.ACTION_REGISTER_LOGIN;

public class CreatePassActivity extends PortraitActivity implements AGCManager.CreateAccountCallBack,
        AGCManager.SigInCallBack, FbManeger.FBCallBack, FormatCallBack {
    private String TAG = "CreatePassActivity";

    static final int FINISH_ACTIVITY = 1001;

    TextInputLayout tilVercode, tilPass, tilConfirmPass;
    ImageView imgBack;
    EditText edtVerifeCode, edtPass, edtConfirmPass;
    Button btOK, btResendCode;

    String uPass, uConfirPass, uVercode, uEmail;
    Staff staff;

    AGCManager agcManager;
    FbManeger fbManeger;
    
    String uid, upass, uemail;

    CheckFormat checkFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pass);

        getLinkViews();

        getDataStaff();

        agcManager.addCallBacks(CreatePassActivity.this);
        agcManager.sendConfirmCode(uEmail,ACTION_REGISTER_LOGIN);

        fbManeger.addCallBacks(CreatePassActivity.this);
        
        uid = AGConnectAuth.getInstance().getCurrentUser().getUid();
        fbManeger.QueryStaffInFo("dbUser",uid);

        getControls();
    }

    // nh???n th??ng tin nh??n vi??n ???? nh???p t??? b??n person infor
    private void getDataStaff() {
        Intent intent = getIntent();
        staff = (Staff) intent.getSerializableExtra("staff");
        uEmail = staff.getEmail();
    }


    private void getControls() {
        //tho??t
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // g???i d??? li???u ??i
        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dieu kien thanh cong
                // neu thanh cong thi finish() activity nay va cac activity phia truoc
                getDataFromView();
                checkFormat.addCallBack(CreatePassActivity.this);
                //checkDataInfor() && checkConfirPass()
                if (checkFormat.checkVercodeAndPass(uVercode,VERIFI,uPass,PASSWORD,uConfirPass,CONFIRMPASSWORD)){
                    setDataStaff();

                    agcManager.creatWithEmail(uEmail, uVercode, uPass,"dbUser", staff);
                }

            }
        });

        // g???i l???i m?? x??c nh???n
        btResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agcManager.sendConfirmCode(staff.getEmail(),ACTION_REGISTER_LOGIN);
            }
        });
    }

    // ki???m tra d??? li???u c?? b??? tr???ng kh??ng
    private boolean checkConfirPass() {
        if (uPass.equals(uConfirPass))
            return true;
        else {
            Toast.makeText(CreatePassActivity.this, "Passwords are not the same !", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // set d?? li???u cho nh??n vi??n t??? c??c gi?? tr??? l???y ???????c tr??n view
    private void setDataStaff() {
        staff.setPassword(uPass);
        staff.setStaff(true);
    }

    // ki???m tra d??? li???u c?? b??? tr???ng kh??ng
    private boolean checkDataInfor(){
        if (checkData(uConfirPass) || checkData(uPass) || checkData(uConfirPass)){
            Toast.makeText(CreatePassActivity.this, "Not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private boolean checkData(String abc){
        if (abc.isEmpty() || abc == null){
            return true;
        }else{
            return false;
        }
    }

    // l???y d??? li???u tr??n view
    private void getDataFromView() {
        uVercode = edtVerifeCode.getText().toString().trim();
        uConfirPass = edtConfirmPass.getText().toString().trim();
        uPass = edtPass.getText().toString().trim();
    }

    // li??n c??c ?????i t?????ng hi???n th??? tr??n view
    private void getLinkViews() {
        tilVercode = findViewById(R.id.til_VerCode);
        tilPass = findViewById(R.id.til_password);
        tilConfirmPass = findViewById(R.id.til_confirm_password);
        imgBack = findViewById(R.id.img_back);
        edtVerifeCode = findViewById(R.id.edt_verife_code);
        edtPass = findViewById(R.id.edt_pass);
        edtConfirmPass = findViewById(R.id.edt_confirm_pass);
        btOK = findViewById(R.id.bt_ok);
        btResendCode = findViewById(R.id.bt_resend_code);
        staff = new Staff();
        agcManager = new AGCManager();
        fbManeger = new FbManeger();
        checkFormat = new CheckFormat();
    }

    // l??u l???ch s??? t???o t??i kho???n
    private void saveHistory(String reason) {
        // dinh dang thoi gian
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm");
        // tao lich su
        EditHistory history = new EditHistory();
        history.setDateTime(simpleDateFormat.format(new Date().getTime()));
        history.setIDStaff(staff.getId());
        history.setIDEditor(uid);
        history.setReason(reason);
        history.setInfoCorrected(staff);
        // luu lich su
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String random = databaseReference.child("dbHistory").push().getKey();
        databaseReference.child("dbHistory").child(random).setValue(history);
        finish();
    }

    // ???n b??n ph??m
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

    @Override // n???u g???i m?? x??c nh???n th??nh c??ng
    public void AfterSendSuccess() {
        Toast.makeText(this, "Code sent to Email.", Toast.LENGTH_SHORT).show();
        Log.e(TAG,"???? g???i m?? code v??o Email.");
    }

    @Override // n???u g???i m?? x??c nh???n th???t b???i
    public void AfterSendFail(String e) {
        Toast.makeText(this, "Way when sending confirmation code:\n"+e, Toast.LENGTH_SHORT).show();
        Log.e(TAG,"L???i khi g???i m?? x??c nh???n: "+e);
    }

    @Override // n???u t???o t??i kho???n th??nh c??ng
    public void AfterCreateAccountSuccess() {
        // ????ng c??c activity ph??a tr?????c
        setResult(FINISH_ACTIVITY);
        Toast.makeText(CreatePassActivity.this, "Success", Toast.LENGTH_SHORT).show();
        // l??u l???ch s???
        saveHistory("create01101000");
        // tr??nh v??ng t??i kho???n
        AGConnectAuth.getInstance().signOut();
        agcManager.addSigInCallBack(CreatePassActivity.this);
        agcManager.signInWitEmailAndPassword(uemail,upass);

        //startActivity(new Intent(CreatePassActivity.this, SilentLoginActivity.class));
        //finish();
    }

    @Override // n???u t???o t??i kho???n th???t b???i b??o l???i
    public void AfterCreateAccountFail(String e) {
        if (e.equals(" code: 203818130 message: The user has been registered")){
            Toast.makeText(this, "The user has been registered", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }

        Log.e(TAG,"L???i khi t???o t??i kho???n: "+e);
    }

    @Override
    public void ResetPassSuccess() {

    }

    @Override
    public void ResetPassFail(String e) {

    }

    @Override
    public void QueryStaffInFoSuccess(Staff staff) {
        upass = staff.getPassword();
        uemail = staff.getEmail();
    }

    @Override
    public void QueryStaffInFoFail(String e) {

    }

    @Override
    public void QueryCalenderSuccess(int off, int work, List<String> list) {

    }

    @Override
    public void SinginSuccess(String id) {
        finish();
    }

    @Override
    public void SinginFail(String e) {
        Toast.makeText(CreatePassActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(CreatePassActivity.this,SilentLoginActivity.class));
        finish();
    }

    // kiem tra dieu kien
    @Override
    public void FormatTrue(String check) {
        switch (check){
            case VERIFI: tilVercode.setHelperText(null); break;
            case PASSWORD: tilPass.setHelperText(null); break;
            case CONFIRMPASSWORD: tilConfirmPass.setHelperText(null); break;
        }
    }

    @Override
    public void FormatFail(String messenger, String check) {
        switch (check){
            case VERIFI: tilVercode.setHelperText(messenger); break;
            case PASSWORD: tilPass.setHelperText(messenger); break;
            case CONFIRMPASSWORD: tilConfirmPass.setHelperText(messenger); break;
        }
    }

}