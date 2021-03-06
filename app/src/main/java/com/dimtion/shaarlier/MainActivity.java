package com.dimtion.shaarlier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make links clickable :
        ((TextView) findViewById(R.id.about_details)).setMovementMethod(LinkMovementMethod.getInstance());

        loadSettings();

        // Load custom design :
        TextView textVersion = (TextView) findViewById(R.id.text_version);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            textVersion.setText("Version : " + versionName + " (" + Integer.toString(versionCode) + ")");

        } catch (PackageManager.NameNotFoundException e) {
            textVersion.setText(getText(R.string.text_version));
        }

        ///////////
        // DEBUG //
        ///////////

//        try {
//            SecretKey key = EncryptionHelper.generateKey();
//            String message = "Hé toi, à qui est ce & ?";
//            byte[] encoded = message.getBytes("UTF-8");
//            byte[] encrypted = EncryptionHelper.encrypt(encoded, key);
//
//            // On décode :
//            byte[] decrypted = EncryptionHelper.decrypt(encrypted, key);
//            String finale = new String(decrypted, "UTF-8");
//            Log.d("YEAH", finale);
//        } catch (Exception e){
//            Log.e("HELLLO", e.getMessage());
//        }

    }

    @Override
    public void onPause() {
        super.onPause();
        saveSettings();
    }

    void saveSettings() {
        // Get user inputs :
        boolean isPrivate = ((CheckBox) findViewById(R.id.default_private)).isChecked();
        boolean isShareDialog = ((CheckBox) findViewById(R.id.show_share_dialog)).isChecked();
        boolean isAutoTitle = ((CheckBox) findViewById(R.id.auto_load_title)).isChecked();
        // Save data :
        SharedPreferences pref = getSharedPreferences(getString(R.string.params), MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(getString(R.string.p_default_private), isPrivate)
                .putBoolean(getString(R.string.p_show_share_dialog), isShareDialog)
                .putBoolean(getString(R.string.p_auto_title), isAutoTitle)
                .apply();

    }

    public void openAccountsManager(View view) {
        Intent intent = new Intent(this, AccountsManagementActivity.class);
        startActivity(intent);
    }

    void loadSettings() {
        // Retrieve user previous settings
        SharedPreferences pref = getSharedPreferences(getString(R.string.params), MODE_PRIVATE);
//        updateSettingsFromUpdate(pref);

        boolean prv = pref.getBoolean(getString(R.string.p_default_private), false);
        boolean sherDial = pref.getBoolean(getString(R.string.p_show_share_dialog), true);
        boolean isAutoTitle = pref.getBoolean(getString(R.string.p_auto_title), true);

        // Retrieve interface :
        CheckBox privateCheck = (CheckBox) findViewById(R.id.default_private);
        CheckBox shareDialogCheck = (CheckBox) findViewById(R.id.show_share_dialog);
        CheckBox autoTitleCheck = (CheckBox) findViewById(R.id.auto_load_title);

        // Display user previous settings :
        privateCheck.setChecked(prv);
        autoTitleCheck.setChecked(isAutoTitle);
        shareDialogCheck.setChecked(sherDial);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_go_to_shaarli:
                SharedPreferences pref = getSharedPreferences(getString(R.string.params), MODE_PRIVATE);
//                updateSettingsFromUpdate(pref);

                String url = pref.getString(getString(R.string.p_url_shaarli), getString(R.string.developer_shaarli));

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                break;
            case R.id.action_share:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle(getString(R.string.share));

                // TODO : move this to a xml file :
                final LinearLayout layout = new LinearLayout(this);

                final TextView textView = new TextView(this);
                textView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                textView.setText(getText(R.string.text_new_url));

                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                input.setHint(getText(R.string.hint_new_url));

                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(10, 10, 20, 20);

                layout.addView(textView);
                layout.addView(input);
                alert.setView(layout);

                alert.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        Intent intent = new Intent(getBaseContext(), AddActivity.class);
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, value);
                        startActivity(intent);
                    }
                });

                alert.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}