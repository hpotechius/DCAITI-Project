package com.test.potechius.opencvversion2test;

public class Material {

    /**************************************************************************
     * variables
     **************************************************************************/
    private String materialName;
    private float ns;
    private float[] ka;
    private float[] kd;
    private float[] ks;
    private float[] ke;
    private float ni;
    private float d;
    private int illum;
    private String textureName;

    /**************************************************************************
     * constructors
     **************************************************************************/
    public Material(){

    }

    public Material(String materialName){
        this.materialName = materialName;
    }

    /**************************************************************************
     * getter & setter
     **************************************************************************/
    public String getMaterialName() {
        return materialName;
    }

    public float getNs() {
        return ns;
    }

    public float[] getKa() {
        return ka;
    }

    public float[] getKd() {
        return kd;
    }

    public float[] getKs() {
        return ks;
    }

    public float[] getKe() {
        return ke;
    }

    public float getNi() {
        return ni;
    }

    public int getIllum() {
        return illum;
    }

    public String getTextureName() {
        return textureName;
    }

    public void setNs(float ns) {
        this.ns = ns;
    }

    public void setKa(float[] ka) {
        this.ka = ka;
    }

    public void setKd(float[] kd) {
        this.kd = kd;
    }

    public void setKs(float[] ks) {
        this.ks = ks;
    }

    public void setKe(float[] ke) {
        this.ke = ke;
    }

    public void setNi(float ni) {
        this.ni = ni;
    }

    public void setIllum(int illum) {
        this.illum = illum;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public float getD() {
        return d;
    }

    public void setD(float d) {
        this.d = d;
    }
}
