/***********************************************************************************
 * Copyright (C) 2025 Abiddarris
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
package com.abiddarris.vnpyemulator.patches;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Patch {

    private final String name;
    private final String renpyVersion;
    private final Patcher[] patchers;

    Patch(JSONObject object) throws JSONException {
        name = object.getString("name");
        renpyVersion = object.getString("version");

        List<Patcher> patchers = new ArrayList<>();
        JSONArray patchersJSON = object.getJSONArray("patch_version");
        for (int i = 0; i < patchersJSON.length(); i++) {
            patchers.add(new Patcher(this, patchersJSON.getJSONObject(i)));
        }

        this.patchers = patchers.toArray(new Patcher[0]);
    }

    /**
     * Returns array of patches
     *
     * @return Array of patches
     */
    public Patcher[] getPatchers() {
        return patchers;
    }

    /**
     * Returns target Ren'Py version
     *
     * @return Target Ren'Py version
     */
    public String getRenPyVersion() {
        return renpyVersion;
    }

    public String getName() {
        return name;
    }
}
