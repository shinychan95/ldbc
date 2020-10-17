/* 
 Copyright (c) 2013 LDBC
 Linked Data Benchmark Council (http://www.ldbcouncil.org)
 
 This file is part of ldbc_snb_datagen.
 
 ldbc_snb_datagen is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 ldbc_snb_datagen is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with ldbc_snb_datagen.  If not, see <http://www.gnu.org/licenses/>.
 
 Copyright (C) 2011 OpenLink Software <bdsmt@openlinksw.com>
 All Rights Reserved.
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation;  only Version 2 of the License dated
 June 1991.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/
package ldbc.snb.datagen.entities.statictype.place;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Place implements Serializable {

    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String CONTINENT = "continent";
    public static final String AREA = "world";

    private int id;
    private int zId;

    private String name;
    private double latt;
    private double longt;
    private long population;
    private String type;

    public int getzId() {
        return zId;
    }

    public void setzId(int zId) {
        this.zId = zId;
    }

    public Place() {
    }

    public Place(int _id, String _name, double _longt, double _latt, int _population, String _type) {
        this.id = _id;
        this.name = _name;
        this.longt = _longt;
        this.latt = _latt;
        this.population = _population;
        this.type = _type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongt() {
        return longt;
    }

    public void setLongt(double longt) {
        this.longt = longt;
    }

    public double getLatt() {
        return latt;
    }

    public void setLatt(double latt) {
        this.latt = latt;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
