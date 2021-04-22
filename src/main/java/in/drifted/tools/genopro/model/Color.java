/*
 * Copyright (c) 2021 Jan Tošovský <jan.tosovsky.cz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.drifted.tools.genopro.model;

public class Color {

    private final int r;
    private final int g;
    private final int b;
    private final double a;

    public Color(int r, int g, int b) {
        this(r,g,b,1.0);
    }
    
    public Color(int r, int g, int b, double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(String hex) { 
        this(
            Integer.valueOf(hex.substring(1, 3), 16),
            Integer.valueOf(hex.substring(3, 5), 16),
            Integer.valueOf(hex.substring(5, 7), 16),
            (hex.length() == 9) ? Integer.valueOf(hex.substring(7, 9), 16) / 255 : 1.0);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.r;
        hash = 71 * hash + this.g;
        hash = 71 * hash + this.b;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Color other = (Color) obj;
        if (this.r != other.r) {
            return false;
        }
        if (this.g != other.g) {
            return false;
        }
        if (this.b != other.b) {
            return false;
        }
        if (Double.doubleToLongBits(this.a) != Double.doubleToLongBits(other.a)) {
            return false;
        }
        return true;
    }
    
    
    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }
    
    public double getA() {
        return a;
    }
    
    public String getHex() {
        return (a < 1) ? String.format("#%02x%02x%02x%02x", r, g, b, a * 255) : String.format("#%02x%02x%02x", r, g, b);  
    }
    
}
