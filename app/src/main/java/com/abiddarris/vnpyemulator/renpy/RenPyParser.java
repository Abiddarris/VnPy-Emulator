/***********************************************************************************
 * Copyright (C) 2024-2025 Abiddarris
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
package com.abiddarris.vnpyemulator.renpy;

import com.abiddarris.common.stream.InputStreams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that parse renpy library
 */
public class RenPyParser {
    
    /**
     * Pattern to parse renpy version
     */
    private static final Pattern VERSION_PATTERN = Pattern.compile(
        "version_tuple\\s*=\\s*\\(([^)]*)\\)");
    
    /**
     * Pattern to parse vc_version
     */
    private static final Pattern VC_VERSION_PATTERN = Pattern.compile(
        "vc_version\\s*=\\s*(.*)");
    
    /**
     * Constant that contains field named vc_version
     */
    private static final String VC_VERSION = "vc_version";
    
    /**
     * Returns Ren'Py version
     * 
     * @param gameFolder Folder that contains "renpy"
     * @return {@code null} if unable to parse the version
     *         otherwise return the version
     */
    public static String getVersion(File gameFolder) throws IOException {
        File initFile = new File(gameFolder, "renpy/__init__.py");
        if(!initFile.isFile()) {
            return null;
        }

        String version;
        try (BufferedReader reader = new BufferedReader(new FileReader(initFile))) {
            String content = InputStreams.readAll(reader);
            Matcher matcher = VERSION_PATTERN.matcher(content);
            if (!matcher.find()) {
                return null;
            }

            version = matcher.group(1);
        }

        if (version == null) {
            return null;
        }

        if (version.contains(VC_VERSION)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(
                    new File(gameFolder, "renpy/vc_version.py")))) {
                String content = InputStreams.readAll(reader);
                Matcher matcher = VC_VERSION_PATTERN.matcher(content);
                String matcherVersion = matcher.find() ? matcher.group(1) : "0";
                version = version.replace(
                        "vc_version",
                        matcherVersion == null ? "0" : matcherVersion
                );
            }
        }

        version = version.replace(",", "")
            .replace(" ", ".");
        return version;
    }
    
}
