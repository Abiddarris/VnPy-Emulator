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
package com.abiddarris.vnpyemulator.patches;

import java.io.IOException;

/**
 * Class that provides patches
 */
public interface PatchSource {
    
    /**
     * For testing on local repo
     */
    static final boolean LOCAL_SOURCE = true;
    
    /**
     * Returns versions that have a patch
     *
     * @throws IOException if unable to fetch versions
     * @return Versions that have a patch
     */
    public String[] getVersions() throws IOException;
    
    /**
     * Returns {@code Patcher} from given version
     *
     * @param version Patcher's version
     * @throws IOException if unable to fetch the patcher
     * @return {@code Patcher} from given version
     */
    public Patcher getPatcher(String version) throws IOException;
    
    /**
     * Returns {@code PatchSource} that provides {@code Patcher}
     *
     * @return {@code PatchSource} that provides {@code Patcher}
     */
    public static PatchSource getPatcher() {
    	if(LOCAL_SOURCE) {
            return new LocalPatchSource();
        }
        return null;
    }
}
