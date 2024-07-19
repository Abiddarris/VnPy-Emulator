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
package com.abiddarris.vnpyemulator.unrpa;

import com.abiddarris.common.android.dialogs.ProgressDialog;
import static com.abiddarris.common.stream.InputStreams.writeAllTo;

import androidx.fragment.app.DialogFragment;

import com.abiddarris.common.android.tasks.TaskDialog;
import com.abiddarris.common.renpy.archives.RpaEntry;
import com.abiddarris.common.renpy.archives.Unrpa;

import com.abiddarris.vnpyemulator.R;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class UnrpaTask extends TaskDialog {

    private File[] archives;
    private String dest;
    private boolean deleteAfterUnpack;

    public UnrpaTask(File[] archives, String dest, boolean deleteAfterUnpack) {
        this.archives = archives;
        this.dest = dest;
        this.deleteAfterUnpack = deleteAfterUnpack;
    }
    
    @Override
    protected String getTag() {
        return "unrpaDialog";
    }

    @Override
    protected DialogFragment newDialog() {
        return new UnpackArchiveDialog();
    }

    @Override
    public void execute() throws Exception {
        setMessage(getString(R.string.unpacking_archive) + "\u2026");
        
        for(var archive : archives) {
            Unrpa unrpa;
            try (FileInputStream stream = new FileInputStream(archive)){
                unrpa = Unrpa.create(stream);
                try (FileInputStream stream2 = new FileInputStream(archive)){
                    unrpa.init(stream2);
                
                    unpack(unrpa);
                }
            }
            if(deleteAfterUnpack) {
                archive.delete();
            }
        }
    }
    
    private void unpack(Unrpa unrpa) throws Exception {
        RpaEntry entry;
        while((entry = unrpa.nextEntry()) != null) {
            File dest = new File(this.dest, entry.getName());
            File parent = dest.getParentFile();
            if(!parent.exists()) {
                parent.mkdirs();
            }
            
            try (OutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(dest))) {
                writeAllTo(entry, stream);
            }
        }
    }
    
    private void setMessage(String message) {
        ProgressDialog dialog = getDialog();
        dialog.setMessage(message);
    }
}
