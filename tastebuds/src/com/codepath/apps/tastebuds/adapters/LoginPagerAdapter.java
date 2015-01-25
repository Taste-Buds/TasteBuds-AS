package com.codepath.apps.tastebuds.adapters;

import com.codepath.apps.tastebuds.fragments.LoginSwipePageFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class LoginPagerAdapter extends FragmentStatePagerAdapter {

	public LoginPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	 @Override
	    public Fragment getItem(int i) {
	        Fragment fragment = new LoginSwipePageFragment();
	        Bundle args = new Bundle();
	        // Our object is just an integer :-P
	        args.putInt(LoginSwipePageFragment.ARG_OBJECT, i + 1);
	        fragment.setArguments(args);
	        return fragment;
	    }

	    @Override
	    public int getCount() {
	        return 4;
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	        return "OBJECT " + (position + 1);
	    }
}
