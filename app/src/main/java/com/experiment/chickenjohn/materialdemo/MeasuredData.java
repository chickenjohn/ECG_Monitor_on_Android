package com.experiment.chickenjohn.materialdemo;

/**
 * Below is the copyright information.
 * <p/>
 * Copyright (C) 2016 chickenjohn
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * You may contact the author by email:
 * chickenjohn93@outlook.com
 * <p/>
 * Created by chickenjohn on 2016/3/8.
 */

public class MeasuredData {
    private String typeName;
    private int value;

    public MeasuredData(String typeName, int value) {
        super();
        this.typeName = typeName;
        this.value = value;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getValueInString() {
        return String.valueOf(value);
    }

    public int getValueInInt() {
        return value;
    }
}
