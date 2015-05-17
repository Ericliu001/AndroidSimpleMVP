package com.ericliudeveloper.mvpevent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.greenrobot.event.EventBus;

/**
 * haven't created a presenter for this Activity yet.
 * For simple Activities or Fragments, there is no need to implement MVP
 */
public class SetNameActivity extends ActionBarActivity {

    public static final String NAME_FIELD = "name";
    Button btSetName;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        btSetName = (Button) findViewById(R.id.btSetName);
        editText = (EditText) findViewById(R.id.editText);

        btSetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString();
                if (TextUtils.isEmpty(userInput)){
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SetNameActivity.this);

                    dialogBuilder.setMessage("Name can't be empty");
                    dialogBuilder.setNeutralButton("Ok", null);
                    Dialog dialog = dialogBuilder.create();
                    dialog.show();
                    return;
                }

//                Intent intent = getIntent(); // recycle intent
//                intent.putExtra(NAME_FIELD, userInput);
//                setResult(RESULT_OK, intent);

                EventBus.getDefault().postSticky(new MyEvents.NameSetEvent(userInput));
                finish();
            }
        });
    }


}
