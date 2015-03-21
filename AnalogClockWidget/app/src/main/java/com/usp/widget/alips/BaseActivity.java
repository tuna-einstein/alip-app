package com.usp.widget.alips;

import com.usp.dagger.DaggerActivity;

import java.util.List;

/**
 * Created by umasankar on 3/8/15.
 */
public class BaseActivity extends DaggerActivity {

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new ActivityModule());
        return modules;
    }
}
