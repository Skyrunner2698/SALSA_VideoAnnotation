package com.example.salsa_videoannotation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MultiSelectionSpinner extends androidx.appcompat.widget.AppCompatSpinner implements DialogInterface.OnMultiChoiceClickListener {

    String[] mItems = null;
    boolean[] mSelection = null;

    ArrayAdapter<String> simple_adapter;

    public MultiSelectionSpinner(Context context)
    {
        super(context);
        simple_adapter = new ArrayAdapter<String>(context, R.layout.spinner_item);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        simple_adapter = new ArrayAdapter<String>(context, R.layout.spinner_item);
        super.setAdapter(simple_adapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) 
    {
        if(mSelection != null && which < mSelection.length)
        {
            mSelection[which] = isChecked;
            simple_adapter.clear();
            simple_adapter.add(buildSelectedItemString());
        }
        else
        {
            throw new IllegalArgumentException("Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(mItems, mSelection, this);
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(String[] items)
    {
        mItems = items;
        mSelection = new boolean[mItems.length];
        simple_adapter.clear();
        Arrays.fill(mSelection, false);
    }

    public void setItems(List<String> items)
    {
        mItems = items.toArray(new String[items.size()]);
        mSelection = new boolean[mItems.length];
        simple_adapter.clear();
        Arrays.fill(mSelection, false);
    }

    public void setSelection(String[] selection)
    {
        for (String cell : selection)
        {
            for (int i = 0; i < mItems.length; ++i)
            {
                if(mItems[i].equals(cell))
                    mSelection[i] = true;
            }
        }
    }

    public void setSelection(List<String> selection)
    {
        for(int i = 0; i < mSelection.length; ++i)
        {
            mSelection[i] = false;
        }
        for (String cell : selection)
        {
            for (int i = 0; i < mItems.length; ++i)
            {
                if(mItems[i].equals(cell))
                    mSelection[i] = true;
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int index)
    {
        for(int i = 0; i < mSelection.length; ++i)
        {
            mSelection[i] = false;
        }
        if(index >= 0 && index < mSelection.length)
        {
            mSelection[index] = true;

        }
        else
        {
            throw new IllegalArgumentException("Index " + index + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndices)
    {
        for(int i = 0; i < mSelection.length; ++i)
        {
            mSelection[i] = false;
        }
        for (int index : selectedIndices)
        {
            if(index >= 0 && index < mSelection.length)
            {
                mSelection[index] = true;

            }
            else
            {
                throw new IllegalArgumentException("Index " + index + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings()
    {
        List<String> selection = new LinkedList<String>();
        for(int i = 0;  i < mItems.length; ++i)
        {
            if(mSelection[i])
                selection.add(mItems[i]);
        }
        return selection;
    }

    public List<Integer> getSelectedIndices()
    {
        List<Integer> selection = new LinkedList<Integer>();
        for(int i = 0; i < mItems.length; ++i)
        {
            if(mSelection[i])
                selection.add(i);
        }
        return selection;
    }

    private String buildSelectedItemString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        boolean foundOne = false;

        for (int i =0; i < mItems.length; ++i)
        {
            if(mSelection[i])
            {
                if(foundOne)
                {
                    stringBuilder.append(", ");
                }
                foundOne = true;
                stringBuilder.append(mItems[i]);
            }
        }
        return stringBuilder.toString();
    }

    public String getSelectedItemsAsString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        boolean foundOne = false;

        for (int i =0; i < mItems.length; ++i)
        {
            if(mSelection[i])
            {
                if(foundOne)
                {
                    stringBuilder.append(", ");
                }
                foundOne = true;
                stringBuilder.append(mItems[i]);
            }
        }
        return stringBuilder.toString();
    }
}
