package food_system.project.stiw2044.com.stiw2044_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class BuyerMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "BuyerMenuActivity";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        Log.i(TAG, "Message : " + getIntent().getStringExtra("NAME"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_menu);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_buyer);
        drawerLayout.closeDrawers();
        navigationView = (NavigationView)findViewById(R.id.nav_view_buyer);
        toolbar = (Toolbar)findViewById(R.id.toolbar_buyer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Buyer View");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //------------------------- Send Message -------------------------
        View headerView = navigationView.getHeaderView(0);
        textView = (TextView) headerView.findViewById(R.id.header_title);
        textView.setText(getIntent().getStringExtra("NAME"));

        //------------------------- Set Default Fragment -------------------------
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_buyer, new BuyerHomeFragment());
        fragmentTransaction.commit();

        //------------------------- Listener -------------------------
        navigationView.setNavigationItemSelectedListener(this);

    }//------------------------- OnCreate Method -------------------------

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();


    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int menuItemID = menuItem.getItemId();
        Fragment buyerFragment = null;
        // Check selected menu item's id and replace a Fragment Accordingly
        if(menuItemID == R.id.nav_buyer_home){
            Log.i(TAG, "Drawer Navigate Clicked : Home");
            buyerFragment = new BuyerHomeFragment();
        } else if (menuItemID == R.id.nav_buyer_shopping){
            Log.i(TAG, "Drawer Navigate Clicked : Discover Food Shop");
            Bundle bundle = new Bundle();
            bundle.putString("NAME", getIntent().getStringExtra("NAME"));
            buyerFragment = new BuyerHuntFoodFragment();
            buyerFragment.setArguments(bundle);
        } else if (menuItemID == R.id.nav_buyer_cart){
            Log.i(TAG, "Drawer Navigate Clicked : Buyer Cart");
            Bundle bundle = new Bundle();
            bundle.putString("NAME", getIntent().getStringExtra("NAME"));
            buyerFragment = new BuyerOrderedListFragment();
            buyerFragment.setArguments(bundle);
        } else if (menuItemID == R.id.nav_buyer_about) {
            Log.i(TAG, "Drawer Navigate Clicked : About Us");
            buyerFragment = new BuyerAboutFragment();
        } else if (menuItemID == R.id.nav_buyer_signout){
            Log.i(TAG, "Drawer Navigate Clicked : Sign Out");
            signOut();
        }

        if(buyerFragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_buyer, buyerFragment);
            fragmentTransaction.commit();
            drawerLayout.closeDrawers();
        }
        //menuItem.setChecked(true);
        return true;
    }

    public void signOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to sign out ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Sign Out : Yes");
                        Intent intent = new Intent(BuyerMenuActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Sign Out : No");
                        dialog.cancel();
                    }
                })
                .show();
    }

}
