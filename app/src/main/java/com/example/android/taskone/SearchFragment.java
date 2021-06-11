package com.example.android.taskone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.android.taskone.Data.ItemContract;
import com.example.android.taskone.adapter.ItemAdapter;
import com.example.android.taskone.listener.ItemListener;
import com.example.android.taskone.model.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SearchFragment extends Fragment implements ItemListener {

    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    List<Item> items = new ArrayList<>();
    EditText mETSearch;
    FloatingActionButton fabSearchFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAdapter = new ItemAdapter(items, this);
        mRecyclerView.setAdapter(mAdapter);

        fabSearchFragment = view.findViewById(R.id.fab_search_fragment);
        fabSearchFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditorFragment editorFragment = new EditorFragment();
                ((MainActivity) requireActivity()).loadFragment(editorFragment, true);
            }
        });

        mETSearch = view.findViewById(R.id.et_search_fragment);
        mETSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.getFilter().filter(s);
            }
        });

        fetchAllItems();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchAllItems();
    }

    private void fetchAllItems() {

        Cursor cursor = null;

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

        try {
            items = new ArrayList<>();
            ContentResolver resolver = getActivity().getContentResolver();
            cursor = resolver.query(ItemContract.ItemEntry.CONTENT_URI, projection, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                int idColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry._ID);
                int firstNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_FIRST_NAME);
                int lastNameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_LAST_NAME);
                int emailColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_EMAIL_ID);
                int addressColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ADDRESS);
                int weightColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_WEIGHT);
                int heightColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_HEIGHT);
                int phoneColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PHONE);
                int dateColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_DATE);

                int id = cursor.getInt(idColumnIndex);
                String firstName = cursor.getString(firstNameColumnIndex);
                String lastName = cursor.getString(lastNameColumnIndex);
                String email = cursor.getString(emailColumnIndex);
                String address = cursor.getString(addressColumnIndex);
                Double weight = cursor.getDouble(weightColumnIndex);
                Double height = cursor.getDouble(heightColumnIndex);
                String phone = cursor.getString(phoneColumnIndex);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = dateFormat.parse(cursor.getString(dateColumnIndex));

                Item item = new Item();
                item.cursorId = id;
                item.mFirstName = firstName;
                item.mLastName = lastName;
                item.mEmail = email;
                item.mAddress = address;
                item.weight = weight;
                item.height = height;
                item.phoneNo = phone;
                item.date = date;
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        mAdapter.setData(items);

    }

    @Override
    public void onDelete(int position, int cursorId) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri mCurrentItemUir = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, cursorId);
        contentResolver.delete(mCurrentItemUir, null, null);
        items.remove(position);
        mAdapter.setData(items);

    }

    @Override
    public void onEdit(int position, int cursorId) {

        EditorFragment editorFragment = new EditorFragment();
        Uri mCurrentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, cursorId);
        Bundle bundle = new Bundle();
        bundle.putString("cursorUri", mCurrentItemUri.toString());
        editorFragment.setArguments(bundle);
        ((MainActivity) requireActivity()).loadFragment(editorFragment, true);
    }
}