/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.pucrs.mpsocdebugger.source;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import br.pucrs.mpsocdebugger.util.MPSoCConfig;

/**
 *
 * @author faccenda
 */
public class OpaqueSecureZones {
    private RandomAccessFile logOSZ;
    private MPSoCConfig mPSoCConfig;
    private MPSoCInformation mPSoCInformation;
    private int nextTimeReading;
    private RouterNeighbors n; 
    public int getNextTimeReading() {
        return nextTimeReading;
    }
    private ArrayList<SecureZone> allOSZ;
    //private long currentLine;
    
//hemps/hemps_OSZ/testcases/TaskMigration2a/debug/platform.cfg
//hemps/hemps_OSZ/testcases/Overlay/debug/platform.cfg
//hemps/hemps_OSZ/testcases/DynamicMapping2/debug/platform.cfg
//testcase prodcons
//testcase AES  cut maior
  
    
    public OpaqueSecureZones(MPSoCConfig mPSoCConfig, MPSoCInformation mPSoCInformation) throws FileNotFoundException{
        this.mPSoCConfig = mPSoCConfig;
        this.mPSoCInformation = mPSoCInformation;
        this.n = new RouterNeighbors(mPSoCConfig);
        this.nextTimeReading = -1;
        allOSZ = new ArrayList<>();
        
        File f = new File(this.mPSoCConfig.getDebugDirPath()+"/traffic_brnoc.txt");
        try{
            this.logOSZ = new RandomAccessFile(f, "r");
        }catch (IOException e){}

        //this.currentLine = 0;

    }
    
    public int getOSZid(int x, int y) 
    {   
        for (SecureZone sz : allOSZ){
            if(sz.getStatus() == 12)
                continue;
            if(sz.belongOSZ(x, y))
                return sz.getAppId();
        }
        return -1;
    }
    
    private SecureZone getSZbyIndex(int oszID){
        for (SecureZone sz : allOSZ){
            if(oszID == sz.getAppId()){      
                    return sz;
            }
        }
        return null;   
    }
    
//    private void updateSZ(String line) {
    public void updateSZ(int currentTime) {
        SecureZone currentSZ = null;
        
        if (currentTime < 0)
                currentTime = Integer.MAX_VALUE;

        // EAST 0 WEST 1 NORTH 2 SOUTH 3
        String lineTmp = null;
        while (currentTime > nextTimeReading || nextTimeReading == -1){
            try {
                lineTmp = logOSZ.readLine();
                if (lineTmp != null){
                    //updateSZ(lineTmp);
                    this.updateNextTimeReading();
                } else{
                    return;
                }
            } catch (IOException ex) {
                System.out.println("EoF");
                return;
            }
        
        int time = 0;
        String line = lineTmp;
        //int lineId;
        int linesSource, lineTarget, linePay, lineService;
        String[] splitedLine = line.split("\t");
        //[0]TEMPO [1]SOURCE [2]TARGET [3]PAYLOAD [4]SERVICE
        try{
            time = Integer.parseInt(splitedLine[0]);
            linesSource = Integer.parseInt(splitedLine[1],16);
            lineTarget = Integer.parseInt(splitedLine[2],16);
            linePay = Integer.parseInt(splitedLine[3],16);
            lineService = Integer.parseInt(splitedLine[4]);
//            System.out.println("Linha: "+line);
//            System.out.println("TEMPO: "+time);
//            System.out.println("SOURCE: "+linesSource);
//            System.out.println("TARGET: "+lineTarget);
//            System.out.println("PAYLOAD: "+linePay);
//            System.out.println("SERVICE: "+lineService);
            switch (lineService){
                case 7: //case "SET":
                    currentSZ = new SecureZone();
                    currentSZ.setAppId(linesSource >> (24));
                    currentSZ.setStatus(lineService);
                    currentSZ.setRightHigh(Integer.parseInt(splitedLine[3]));
                    currentSZ.setLowLeft(Integer.parseInt(splitedLine[2]));
//                    System.out.println("Linha: "+line);
//                    System.out.println("TEMPO: "+time);
//                    System.out.println("TARGET: "+lineTarget);
//                    System.out.println("PAYLOAD: "+linePay);
//                    System.out.println("SERVICE: "+lineService);
//                    System.out.println("rhAddr_X = "+ (currentSZ.getRightHigh()/10));
//                    System.out.println("rhAddr_Y = "+ (currentSZ.getRightHigh() % 10));
//                    System.out.println("llAddr_X = "+ (currentSZ.getLowLeft() /10)); 
//                    System.out.println("llAddr_Y = "+ (currentSZ.getLowLeft() % 10));
//                    
//                    System.out.println("NEW OSZ");
                    allOSZ.add(currentSZ);
                    break;
                case 29: //"SET RECIEVED":
                    break;
                case 30: //"EXCESS":
                    for (SecureZone sz : allOSZ){
                        if (sz.getAppId() ==  (linesSource >> (24))){
                            if (lineTarget != linePay){
                                sz.setCutLL(Integer.parseInt(splitedLine[3]));
                                sz.setCutRH(Integer.parseInt(splitedLine[2]));
                                break;
                            }
                        }
                    }
                    break;
                case 11: //"CLOSED":
                    break;
                case 10: //"OPEN":
                    break;
                case 6: //"END_TASK_SERVICE": 
                   int appIDtaskID = (((linePay << 4) & 0xFF00) | (linePay & 0x000F));
                    int ham_addr = n.xy_to_ham_addr(linesSource);
                  
                    //System.out.println("linesSource: "+ linesSource);
                    //System.out.println("linesSource: "+ linePay);
                    System.out.println("END TASK: appIDtaskID: "+ appIDtaskID);
                    //System.out.println("hamAddr: "+ ham_addr);

                    //linesSource
                    //public TaskInformation(int id, String service, int time){
                    this.mPSoCInformation.getRouterInformation(ham_addr).getTasksInformation().add(new TaskInformation(appIDtaskID, "TERMINATED", time));
                case 12: //"OPENED":
                    //System.out.println("PAYLOAD: "+linePay);
                    for (SecureZone sz : allOSZ){
                        //System.out.println("ID Atual: "+sz.getAppId());
                        if (sz.getAppId() ==  (linePay)){
                            //System.out.println("Abriu:");
                            sz.setStatus(lineService);
                            break;
                        }
                    }
                    break;
            }
        } catch (NumberFormatException e){
            System.out.println("WARNING: Wrong packet format at time "+time+" of traffic_brnoc.txt");
        }
        
        }
    }

    private void updateNextTimeReading(){
        long currentLine;
        String line;
        try{
            currentLine = logOSZ.getFilePointer();
            line = logOSZ.readLine();
            if (line != null){
                String[] splitedLine = line.split("\t");
                this.nextTimeReading = Integer.parseInt(splitedLine[0]);
                logOSZ.seek(currentLine);
            }
        }catch (IOException ex) {}
    }
  
    
    public int getSZBorder(int oszID, int x, int y){
        int rhAddr_X;       // RH Addr
        int rhAddr_Y;
        int llAddr_X;       // LL Addr
        int llAddr_Y;
        int cutrhAddr_X;       // RH Addr, Arquivo criado pelo Kernel? Na BrNoc é dificil pq falta observar000
        int cutrhAddr_Y;
        int cutllAddr_X;       // LL Addr
        int cutllAddr_Y;

        int tmpBorder = 0; 
        // EAST 0 WEST 1 NORTH 2 SOUTH 3
        SecureZone sz;
        
        sz = this.getSZbyIndex(oszID);
        
        rhAddr_X = sz.getRightHigh() /10;
        rhAddr_Y = sz.getRightHigh() %10;
        llAddr_X = sz.getLowLeft() /10; 
        llAddr_Y = sz.getLowLeft() %10;
        cutrhAddr_X = sz.getCutRH() /10;
        cutrhAddr_Y = sz.getCutRH() %10;
        cutllAddr_X = sz.getCutLL() /10; 
        cutllAddr_Y = sz.getCutLL() %10;
        
        if(y <= rhAddr_Y && y >= llAddr_Y ){
            if(x == rhAddr_X)//EAST
                tmpBorder = tmpBorder | 0x01;
            if(x == llAddr_X || ((x-1) == cutrhAddr_X && y <= cutrhAddr_Y))//WEST
            //if(x == llAddr_X ) 
                tmpBorder = tmpBorder | 0x02;
        }

        //NORTH or SOUTH test
        if(x >= llAddr_X && x <= rhAddr_X){
            if(y == rhAddr_Y)//NORTH
                tmpBorder = tmpBorder | 0x04;
            if(y == llAddr_Y || ((y-1) == cutrhAddr_Y && x <= cutrhAddr_X))//SOUTH
            //if(y == llAddr_Y)//SOUTH
                tmpBorder = tmpBorder | 0x08;
        }
        //System.out.println("X:  "+ x + "  Y:" + y +"Borda  " + tmpBorder);
        return tmpBorder;
    }

}
