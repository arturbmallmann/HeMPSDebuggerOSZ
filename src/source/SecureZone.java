/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

import java.awt.Color;

/**
 *
 * @author faccenda
 */
public class SecureZone {
    private int id; // OSZ index
    private int status; // SET, UNSET,  FREE
    private int appId; // AppID referred to it
    private int rightHigh;
    private int lowLeft;
    private int cutLL;
    private int cutRH;

    public void setId(int id) {
        this.id = id;
    }

    public SecureZone() {
        this.id = -1;
        this.status = -1;
        this.appId = -1;
        this.rightHigh = -1;
        this.lowLeft = -1;
        this.cutLL = -1;
        this.cutRH = -1;            
    }

    public boolean belongOSZ(int x, int y){
        int rhAddr_X;       // RH Addr, Arquivo criado pelo Kernel? Na BrNoc é dificil pq falta observar000
        int rhAddr_Y;
        int llAddr_X;       // LL Addr
        int llAddr_Y;
        int cutrhAddr_X;       // RH Addr, Arquivo criado pelo Kernel? Na BrNoc é dificil pq falta observar000
        int cutrhAddr_Y;
        int cutllAddr_X;       // LL Addr
        int cutllAddr_Y;

        rhAddr_X = rightHigh /10;
        rhAddr_Y = rightHigh %10;
        llAddr_X = lowLeft /10; 
        llAddr_Y = lowLeft %10;
        cutrhAddr_X = cutRH /10;
        cutrhAddr_Y = cutRH %10;
        cutllAddr_X = cutLL /10; 
        cutllAddr_Y = cutLL %10;
        if(y <= rhAddr_Y && y >= llAddr_Y && x >= llAddr_X && x <= rhAddr_X){
            //System.out.println("Está na OSZ");
            if(!(y <= cutrhAddr_Y && y >= cutllAddr_Y && x >= cutllAddr_X && x <= cutrhAddr_X)){
                //System.out.println("Não é CUT");
                return true;
            }
            System.out.println("Mas é CUT");
        }
        
        return false;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setRightHigh(int rightHigh) {
        this.rightHigh = rightHigh;
    }

    public void setLowLeft(int lowLeft) {
        this.lowLeft = lowLeft;
    }

    public void setCutLL(int cutLL) {
        this.cutLL = cutLL;
    }

    public void setCutRH(int cutRH) {
        this.cutRH = cutRH;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getId() {
        return id;
    }

    public int getAppId() {
        return appId;
    }

    public int getRightHigh() {
        return rightHigh;
    }

    public int getLowLeft() {
        return lowLeft;
    }

    public int getCutLL() {
        return cutLL;
    }

    public int getCutRH() {
        return cutRH;
    }
    
    
}
