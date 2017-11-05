package svkt.wallet.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import svkt.wallet.R;
import svkt.wallet.fragments.AllFragment;
import svkt.wallet.fragments.PaidFragment;
import svkt.wallet.fragments.ReceivedFragment;

public class PassbookActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passbook);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_passbook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_chat:

                startActivity(new Intent(PassbookActivity.this,ChatActivity.class));
                break;

            case R.id.action_passbook :

                startActivity(new Intent(PassbookActivity.this,PassbookActivity.class));
                break;

            case R.id.action_statement :

                startActivity(new Intent(PassbookActivity.this,WalletStatement.class));
                break;

            case R.id.action_logout :
                signOutDialog();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void signOutDialog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(PassbookActivity.this);
        builder.setMessage("Do you want to Sign Out").setTitle("Sign Out");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    if(isInternetConnected()) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(PassbookActivity.this,LoginActivity.class));
                    }
                }
                catch (Exception e) {
                    Toast.makeText(PassbookActivity.this,R.string.error,Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private boolean isInternetConnected()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(PassbookActivity.this,"No Internet Connectivity",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position)
            {
                case 0 :
                    AllFragment allFragment = new AllFragment();
                    return allFragment;
                case 1 :
                    PaidFragment paidFragment = new PaidFragment();
                    return paidFragment;
                case 2 :
                    ReceivedFragment receivedFragment = new ReceivedFragment();
                    return receivedFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All";
                case 1:
                    return "Paid";
                case 2:
                    return "Received";
            }
            return null;
        }
    }
}
