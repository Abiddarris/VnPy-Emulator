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

import androidx.activity.OnBackPressedCallback;

import com.abiddarris.common.android.fragments.AdvanceFragment;
import com.abiddarris.vnpyemulator.R;

public class DownloadFragment extends AdvanceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().setTitle(R.string.download);

        requireActivity().getOnBackPressedDispatcher()
                .addCallback(this, new BackPressedListener());
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
