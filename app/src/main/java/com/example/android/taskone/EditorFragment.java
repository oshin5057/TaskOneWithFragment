package com.example.android.taskone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.taskone.Data.ItemContract;
import com.google.android.material.textfield.TextInputEditText;

public class EditorFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_NOTE_LOADER = 0;

    private Uri mCurrentItemUri = null;

    private TextInputEditText mFirstNameEditTextEditorFragment;
    private TextInputEditText mLastNameEditTextEditorFragment;
    private TextInputEditText mEmailEditTextEditorFragment;
    private TextInputEditText mAddressEditTextEditorFragment;
    private TextInputEditText mPhoneEditTextEditorFragment;

    private DatePicker mDatePicker;
    private Button mBTNGetDate;
    private TextView mTVDateFromDatePicker;
    private TextView mWeightTextView;
    private TextView mTVHeight;

    private Double mWeight = 0.0;
    private Double mHeight = 0.0;

    private boolean mItemHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_editor, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null){
            mCurrentItemUri = Uri.parse(bundle.getString("cursorUri"));
        }

        if (mCurrentItemUri != null){

            getActivity().setTitle("Edit Item");

            LoaderManager.getInstance(this).initLoader(EXISTING_NOTE_LOADER, null,this);

        }
        else {
            getActivity().setTitle("Add Item");

            getActivity().invalidateOptionsMenu();
        }

        mFirstNameEditTextEditorFragment = view.findViewById(R.id.text_ip_et_first_name_editor_fragment);
        mLastNameEditTextEditorFragment = view.findViewById(R.id.text_ip_et_last_name_editor_fragment);
        mEmailEditTextEditorFragment = view.findViewById(R.id.text_ip_et_email_editor_fragment);
        mAddressEditTextEditorFragment = view.findViewById(R.id.text_ip_et_address_editor_fragment);
        mPhoneEditTextEditorFragment = view.findViewById(R.id.text_ip_et_phone_no_editor_fragment);

        mDatePicker = view.findViewById(R.id.date_picker);
        mBTNGetDate = view.findViewById(R.id.btn_get_date);
        mTVDateFromDatePicker = view.findViewById(R.id.tv_date_from_date_picker);
        mWeightTextView = view.findViewById(R.id.weight_text_view);
        Button mBTNWeightIncrement = view.findViewById(R.id.btn_weight_increment);
        Button mBTNWeightDecrement = view.findViewById(R.id.btn_weight_decrement);

        mTVHeight = view.findViewById(R.id.tv_height_value);
        Button mBTNHeightIncrement = view.findViewById(R.id.btn_height_increment);
        Button mBTNHeightDecrement = view.findViewById(R.id.btn_height_decrement);

        mFirstNameEditTextEditorFragment.setOnTouchListener(mTouchListener);
        mLastNameEditTextEditorFragment.setOnTouchListener(mTouchListener);
        mEmailEditTextEditorFragment.setOnTouchListener(mTouchListener);
        mAddressEditTextEditorFragment.setOnTouchListener(mTouchListener);
        mPhoneEditTextEditorFragment.setOnTouchListener(mTouchListener);

        mDatePicker.setOnTouchListener(mTouchListener);

        mBTNGetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTVDateFromDatePicker.setText(getCurrentDate());
            }
        });

        mBTNWeightIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeight = mWeight + 1.0;
                displayWeight(mWeight);
            }
        });
        mBTNWeightDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWeight > 0.0){
                    mWeight = mWeight - 1.0;
                    displayWeight(mWeight);
                }
            }
        });

        mBTNHeightIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeight = mHeight + 1.0;
                displayHeight(mHeight);
            }
        });
        mBTNHeightDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHeight >0.0){
                    mHeight = mHeight - 1.0;
                    displayHeight(mHeight);
                }
            }
        });

        return view;
    }

    private void displayWeight(Double weight) {
        mWeightTextView.setText(String.valueOf(weight));
    }

    private void displayHeight(Double height){
        mTVHeight.setText(String.valueOf(height));
    }

    public String getCurrentDate(){
        StringBuilder builder = new StringBuilder();
        builder.append(mDatePicker.getDayOfMonth()+"/");
        builder.append((mDatePicker.getMonth() + 1) + "/");
        builder.append(mDatePicker.getYear());
        return builder.toString();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_editor, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                saveNote();
                return true;
            case android.R.id.home:
                if(mItemHasChanged){
                    NavUtils.navigateUpFromSameTask(requireActivity());
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        String firstName = mFirstNameEditTextEditorFragment.getText().toString().trim();
        String lastName = mLastNameEditTextEditorFragment.getText().toString().trim();
        String email = mEmailEditTextEditorFragment.getText().toString().trim();
        String address = mAddressEditTextEditorFragment.getText().toString().trim();
        String weight = mWeightTextView.getText().toString().trim();
        String height = mTVHeight.getText().toString().trim();
        String phone = mPhoneEditTextEditorFragment.getText().toString().trim();
        String date = mTVDateFromDatePicker.getText().toString().trim();

        if (mCurrentItemUri == null && TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName)
                && TextUtils.isEmpty(email) && TextUtils.isEmpty(address) && TextUtils.isEmpty(weight)
                && TextUtils.isEmpty(height) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(date)){
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_FIRST_NAME, firstName);
        values.put(ItemContract.ItemEntry.COLUMN_LAST_NAME, lastName);
        values.put(ItemContract.ItemEntry.COLUMN_EMAIL_ID, email);
        values.put(ItemContract.ItemEntry.COLUMN_ADDRESS, address);
        values.put(ItemContract.ItemEntry.COLUMN_WEIGHT, weight);
        values.put(ItemContract.ItemEntry.COLUMN_HEIGHT, height);
        values.put(ItemContract.ItemEntry.COLUMN_PHONE, phone);
        values.put(ItemContract.ItemEntry.COLUMN_DATE, date);

        ContentResolver resolver = getActivity().getContentResolver();
        if (mCurrentItemUri == null){
            Uri newUri = resolver.insert(ItemContract.ItemEntry.CONTENT_URI, values);
            if (newUri == null){
                Toast.makeText(getActivity(), "Error with saving Items", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Item Saved", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            int rowAffected = resolver.update(mCurrentItemUri, values, null, null);
            if (rowAffected == 0){
                Toast.makeText(getActivity(), "Error with updating Item", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Item Updated", Toast.LENGTH_SHORT).show();
            }
        }
        ((MainActivity) requireActivity()).dropFragment(this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_FIRST_NAME,
                ItemContract.ItemEntry.COLUMN_LAST_NAME,
                ItemContract.ItemEntry.COLUMN_EMAIL_ID,
                ItemContract.ItemEntry.COLUMN_ADDRESS,
                ItemContract.ItemEntry.COLUMN_WEIGHT,
                ItemContract.ItemEntry.COLUMN_HEIGHT,
                ItemContract.ItemEntry.COLUMN_PHONE,
                ItemContract.ItemEntry.COLUMN_DATE
        };

        return new CursorLoader(getActivity(),
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1){
            return;
        }

        if (cursor.moveToFirst()){
            int firstNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_FIRST_NAME);
            int lastNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_LAST_NAME);
            int emailColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_EMAIL_ID);
            int addressColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ADDRESS);
            int weightColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_WEIGHT);
            int heightColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_HEIGHT);
            int phoneColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PHONE);
            int dateColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_DATE);

            String firstName = cursor.getString(firstNameColumnIndex);
            String lastName = cursor.getString(lastNameColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            Double weight = cursor.getDouble(weightColumnIndex);
            Double height = cursor.getDouble(heightColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String dateTime = cursor.getString(dateColumnIndex);

            mFirstNameEditTextEditorFragment.setText(firstName);
            mLastNameEditTextEditorFragment.setText(lastName);
            mEmailEditTextEditorFragment.setText(email);
            mAddressEditTextEditorFragment.setText(address);
            mWeightTextView.setText(Double.toString(weight));
            mTVHeight.setText(Double.toString(height));
            mPhoneEditTextEditorFragment.setText(phone);
            mTVDateFromDatePicker.setText(dateTime);

        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        mFirstNameEditTextEditorFragment.setText("");
        mLastNameEditTextEditorFragment.setText("");
        mEmailEditTextEditorFragment.setText("");
        mAddressEditTextEditorFragment.setText("");
        mWeightTextView.setText("");
        mTVHeight.setText("");
        mPhoneEditTextEditorFragment.setText("");
        mTVDateFromDatePicker.setText("");

    }
}