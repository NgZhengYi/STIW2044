package food_system.project.stiw2044.com.stiw2044_project;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TabFragmentLogin tabFragmentLogin = new TabFragmentLogin();
                return tabFragmentLogin;
            case 1:
                TabFragmentSignUp tabFragmentSignUp = new TabFragmentSignUp();
                return tabFragmentSignUp;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "LOGIN";
        }
        else if (position == 1)
        {
            title = "SIGN UP";
        }

        return title;
    }
}
