package com.ericliudeveloper.mvpevent;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ericliudeveloper.mvpevent.android_object_wrapper.ContextWrapper;
import com.ericliudeveloper.mvpevent.presenter.MainActPresenter;



/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements MainActPresenter.MainFace, View.OnClickListener {

    TextView tvTop, tvDisplayName;
    Button btLeft, btRight, btGotoSecond, btGotoDoNothing, btIncrease;
    ProgressBar pbMain;
    private MainActPresenter mPresenter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Initialise the Presenter and pass in data if any
        mPresenter = new MainActPresenter(MainFragment.this, null, new ContextWrapper(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_main, container, false);
        initViews(root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unregister();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onPostViewCreated();
    }

    private void initViews(View root) {
        tvTop = (TextView) root.findViewById(R.id.tvTop);
        tvDisplayName = (TextView) root.findViewById(R.id.tvDisplayName);

        btLeft = (Button) root.findViewById(R.id.btLeft);
        btRight = (Button) root.findViewById(R.id.btRight);
        btGotoSecond = (Button) root.findViewById(R.id.btGoToSecond);
        btGotoDoNothing = (Button) root.findViewById(R.id.btGotoDoNothing);
        btIncrease = (Button) root.findViewById(R.id.btIncrease);

        pbMain = (ProgressBar) root.findViewById(R.id.progressBar);

        btLeft.setOnClickListener(this);
        btRight.setOnClickListener(this);
        btGotoSecond.setOnClickListener(this);
        btGotoDoNothing.setOnClickListener(this);
        btIncrease.setOnClickListener(this);
    }


    /**
     * Called by Presenter to change displaying content
     * @param directionMessage
     */
    @Override
    public void showDirection(String directionMessage) {
        tvTop.setText(directionMessage);
    }



    @Override
    public void showProgress(int progress) {
        pbMain.setProgress(progress);
    }

    @Override
    public void showName(String name) {
        tvDisplayName.setText(name);
    }


    @Override
    public void onClick(View v) {
        // Button click events, forwarding all actions to Presenter
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btLeft:
                mPresenter.buttonLeftClicked();
                break;
            case R.id.btRight:
                mPresenter.buttonRightClicked();
                break;
            case R.id.btIncrease:
                mPresenter.buttonIncreaseClicked();
                break;
            case R.id.btGoToSecond:
                mPresenter.buttonGoToSecondClicked();
                break;
            case R.id.btGotoDoNothing:
                mPresenter.buttonGoToDoNothingClicked();
                break;
            default:
                return;
        }
    }
}
