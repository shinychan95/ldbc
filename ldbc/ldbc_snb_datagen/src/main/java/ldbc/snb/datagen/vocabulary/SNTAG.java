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
package ldbc.snb.datagen.vocabulary;

/**
 * LDBC social network vocabulary namespace used in the serialization process.
 */
public class SNTAG {

    public static final String NAMESPACE = "http://www.ldbc.eu/ldbc_socialnet/1.0/tag/";
    public static final String PREFIX = "sntag:";


    /**
     * Gets the LDBC social network vocabulary prefix version of the input.
     */
    public static String prefixed(String string) {
        return PREFIX + string;
    }

    /**
     * Gets the LDBC social network vocabulary URL version of the input.
     */
    public static String getUrl(String string) {
        return NAMESPACE + string;
    }

    /**
     * Gets the LDBC social network vocabulary RDF-URL version of the input.
     */
    public static String fullPrefixed(String string) {
        return "<" + NAMESPACE + string + ">";
    }
}
