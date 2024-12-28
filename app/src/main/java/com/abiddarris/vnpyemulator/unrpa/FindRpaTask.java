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

import static com.abiddarris.common.files.Files.getFilesTree;

import androidx.fragment.app.DialogFragment;

import com.abiddarris.common.android.tasks.TaskDialog;

import java.io.File;
import java.util.List;

public class FindRpaTask extends TaskDialog {
    
    private File gameFolder;
    
    public FindRpaTask(String gameFolder) {
        this.gameFolder = new File(gameFolder, "game");
    }
    
    @Override
    public String getTag() {
        return "findRpaDialog";
    }
    
    @Override
    public DialogFragment newDialog() {
        return new FindRpaDialog();
    }
    
    @Override
    public void execute() {
        List<File> files = getFilesTree(gameFolder, (file) -> 
            file.isDirectory() || file.getName().endsWith(".rpa"));
        
        File[] archives = files.stream()
            .filter(File::isFile)
            .toArray(File[]::new);
        
        UnpackArchiveOptionsDialog.newInstance(gameFolder.getPath(), archives)
            .show(getFragmentManager(), null);
    }
}
