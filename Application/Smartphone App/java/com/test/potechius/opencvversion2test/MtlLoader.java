package com.test.potechius.opencvversion2test;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class MtlLoader {

    /**************************************************************************
     * variables
     **************************************************************************/
    public final List<Material> materialList = new ArrayList<Material>();

    /**************************************************************************
     * constructor
     **************************************************************************/
    public MtlLoader(Context context, String fileName) {

        Material material = new Material();
        BufferedReader reader = null;

        try {
            int objID = context.getResources().getIdentifier(fileName + "_mat","raw", context.getPackageName());
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(objID));
            reader = new BufferedReader(in);

            // read file until EOF
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "newmtl":
                        material = new Material(parts[1]);
                        materialList.add(material);
                        break;
                    case "Ns":
                        material.setNs(Float.parseFloat(parts[1]));
                        break;
                    case "Ka":
                        material.setKa(new float[]{Float.parseFloat(parts[1]),Float.parseFloat(parts[2]),Float.parseFloat(parts[3])});
                        break;
                    case "Kd":
                        material.setKd(new float[]{Float.parseFloat(parts[1]),Float.parseFloat(parts[2]),Float.parseFloat(parts[3])});
                        break;
                    case "Ks":
                        material.setKs(new float[]{Float.parseFloat(parts[1]),Float.parseFloat(parts[2]),Float.parseFloat(parts[3])});
                        break;
                    case "Ke":
                        material.setKe(new float[]{Float.parseFloat(parts[1]),Float.parseFloat(parts[2]),Float.parseFloat(parts[3])});
                        break;
                    case "Ni":
                        material.setNi(Float.parseFloat(parts[1]));
                        break;
                    case "d":
                        material.setD(Float.parseFloat(parts[1]));
                        break;
                    case "illum":
                        material.setNs(Integer.parseInt(parts[1]));
                        break;
                    case "map_Kd":
                        material.setTextureName(parts[1]);
                        break;
                }
            }
        } catch (IOException e) {
            // cannot load or read file
            System.out.println("ERROR: CANNOT LOAD OR READ FILE");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }

    /**************************************************************************
     * ???
     **************************************************************************/
    public List<Material> getMaterialList() {
        return materialList;
    }

    /**************************************************************************
     * ???
     **************************************************************************/

}
