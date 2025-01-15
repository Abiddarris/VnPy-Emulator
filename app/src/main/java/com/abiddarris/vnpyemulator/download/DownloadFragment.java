/***********************************************************************************
 * Copyright (C) 2024 Abiddarris
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 ***********************************************************************************/
package com.abiddarris.vnpyemulator.download;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.abiddarris.common.android.fragments.AdvanceFragment;
import com.abiddarris.vnpyemulator.R;
import com.abiddarris.vnpyemulator.databinding.FragmentDownloadBinding;

public class DownloadFragment extends AdvanceFragment {

    private FragmentDownloadBinding ui;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().setTitle(R.string.download);

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(this, new BackPressedListener());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewPagerAdapter = new ViewPagerAdapter(this);

        ui = FragmentDownloadBinding.inflate(inflater);
        ui.viewPager.setAdapter(viewPagerAdapter);
        ui.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0 :
                        ui.bottomNavigation.setSelectedItemId(R.id.plugins);
                        break;
                    case 1 :
                        ui.bottomNavigation.setSelectedItemId(R.id.patches);
                }
            }
        });

        ui.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.plugins) {
                ui.viewPager.setCurrentItem(0);
                return true;
            } else if (item.getItemId() == R.id.patches) {
                ui.viewPager.setCurrentItem(1);
                return true;
            }
            return false;
        });

        return ui.getRoot();
    }

    private class BackPressedListener extends OnBackPressedCallback {

        public BackPressedListener() {
            super(true);
        }

        @Override
        public void handleOnBackPressed() {
            getParentFragmentManager().popBackStack();
        }
    }
}
