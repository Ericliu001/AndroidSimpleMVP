package com.ericliudeveloper.mvpevent;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ericliudeveloper.mvpevent.presenter.DisplayInfoPresenter;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayInfoFragment extends Fragment implements DisplayInfoPresenter.DisplayInfoFace, View.OnClickListener {
    TextView tvDirecton, tvProgress, tvName;
    Button btSetDefault, btResetDisplay;
    private DisplayInfoPresenter mPresenter;


    public DisplayInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


        Intent startedIntent = getActivity().getIntent();
        Bundle data = startedIntent.getExtras();
        mPresenter = new DisplayInfoPresenter(DisplayInfoFragment.this, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_do_nothing, container, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        tvDirecton = (TextView) root.findViewById(R.id.tvDirection);
        tvProgress = (TextView) root.findViewById(R.id.tvProgress);
        tvName = (TextView) root.findViewById(R.id.tvName);

        btResetDisplay = (Button) root.findViewById(R.id.btResetDisplay);
        btSetDefault = (Button) root.findViewById(R.id.btSetDefault);

        btResetDisplay.setOnClickListener(this);
        btSetDefault.setOnClickListener(this);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onPostViewCreated();
    }


    @Override
    public void showDirection(String direction) {
        tvDirecton.setText(direction);
    }

    @Override
    public void showProgress(String progress) {
        tvProgress.setText(progress);
    }

    @Override
    public void showName(String name) {
        tvName.setText(name);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            default:
                return;

            case R.id.btSetDefault:
                mPresenter.buttonSetDefaultClicked();
                break;

            case R.id.btResetDisplay:
                mPresenter.buttonResetDisplayClicked();
                break;
        }
    }
}
