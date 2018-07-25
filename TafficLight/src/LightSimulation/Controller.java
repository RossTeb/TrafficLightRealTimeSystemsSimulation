package LightSimulation;// OOSimL v 1.1 File: Controller.java, Wed Dec 02 23:36:30 2015

import java.util.*;
import psimjava.*;
public  class Controller  extends psimjava.Process     {
    static Scanner scan = new Scanner (System.in);
    private double  early_com;
    // earliest time for communication
    private double  late_com;
    // latest time for communication
    private double  c_timeout;
    private int  status;
    private double  green_wait;
    //indicates how long the controller should wait before changing a green light to yellow
    private double  yellow_wait;
    //indicates how long the controller should wait before changing a yellow light to red
    private double  red_wait;
    //length of time both lights remain red when changing
    private Urand Comm_Light;
    // random time for comm with gate
    private Channel M_Cchan;
    //Channel to recieve arrival signal from monitor
    private Channel C_Mchan;
    //Channel to reset Monitor
    private Channel contMchan;
    //Channel to reset Main street sensor
    private Channel contSchan;
    //Channel to reset side street sensor
    private Channel MainLChan;
    //Channel to change main light
    private Channel SideLChan;
    //channel to change side light
    private Message ReceiveMsg;
    private Message SendMsg;
    final public int  G_R = 1;
    final public int  SWITCH_SIDE = 2;
    final public int  Y_R = 3;
    final public int  R_R = 4;
    final public int  R_G = 5;
    final public int  SWITCH_MAIN = 6;
    final public int  R_Y = 7;
    //
    public Controller(  String  cname, double  deadline) {
        super(cname);
        early_com =  (0.10) * (deadline);
        // earliest time for communication
        late_com =  (1.03) * (deadline);
        // latest time for comm with Light
        Comm_Light = new Urand(early_com, late_com);
        // uniform random period
        status =  G_R;
        green_wait =  25;
        yellow_wait =  5;
        red_wait =  1;
        c_timeout =  deadline;
    } // end initializer
    //
    public void  Main_body(   ) {
        double  simclock;
        simclock = StaticSync.get_clock();
        M_Cchan =  Car.notify_con;
        C_Mchan =  Car.notify_mon;
        contMchan =  Car.MainChan;
        contSchan =  Car.SideChan;
        MainLChan =  Car.change_main;
        SideLChan =  Car.change_side;
        SendMsg = new Message("1");
        while ( simclock <= Car.simperiod ) {
            delay (0.0);
            while ( status == G_R ) {
                if ( M_Cchan.input_ready()) {
                    tracedisp("Controller change signal from monitor");
                    delay (0.0);
                    ReceiveMsg =  M_Cchan.receive();
                    status =  SWITCH_SIDE;
                    tracedisp("Controller arrival signal from Monitor");
                    delay(green_wait);
                }
                else {
                    tracedisp("Controller suspends");
                    deactivate(this);
                    tracedisp("Controller reactivated by Monitor");
                } // endif
                delay (0.0);
            } // endwhile
            switch_sideY();
            //
            while ( status == R_G ) {
                if ( M_Cchan.input_ready()) {
                    tracedisp("Controller change signal from monitor");
                    delay (0.0);
                    ReceiveMsg =  M_Cchan.receive();
                    status =  SWITCH_SIDE;
                    tracedisp("Controller arrival signal from Monitor");
                    delay(green_wait);
                }
                else {
                    tracedisp("Controller suspends");
                    deactivate(this);
                } // endif
                delay (0.0);
            } // endwhile
            switch_mainY();
            simclock = StaticSync.get_clock();
        } // endwhile
        terminate();
    }  // end Main_body
    //
    public void  switch_sideY(   ) {
        double  wait_t;
        double  simclock;
        simclock = StaticSync.get_clock();
        tracedisp("Controller attempt comm Light");
        //Generate random time to wait
        wait_t = Comm_Light.fdraw();
        //Wait before Comm
        delay(wait_t);
        //add response to total response time
        Car.tot_response =  (Car.tot_response) + (wait_t);
        //If longest wait yet
        if ( wait_t > Car.worst_response) {
            Car.worst_response =  wait_t;
        } // endif
        // If waited longer than deadline
        if ( wait_t > c_timeout) {
            Car.deadline_l++;
            tracedisp("**** Controller timeout when comm with Light");
            System.out.println("**** Controller timeout when comm with Light");
            //
        } // endif
        //Check if light is active
        if ( (((Car.lightM_obj.idle()) && (Car.lightM_obj.light_stat() == Car.lightM_obj.GREEN)) || (Car.lightM_obj.light_stat() == Car.lightM_obj.YELLOW)) || (Car.lightM_obj.light_stat() == Car.lightM_obj.RED)) {
            reactivate(Car.lightM_obj);
        } // endif
        tracedisp("Controller sending change signal to Light");
        MainLChan.send(SendMsg);
        // Send signal to Main light to change
        status =  Y_R;
        // Change state
        tracedisp("Controller sent yellow signal to main street");
        switch_sideR();
    }  // end switch_sideY
    //
    public void  switch_sideR(   ) {
        double  wait_t;
        double  simclock;
        simclock = StaticSync.get_clock();
        delay(yellow_wait);
        tracedisp("Controller attempt comm Light");
        //Generate time till comm
        wait_t = Comm_Light.fdraw();
        //Hold before comm
        delay(wait_t);
        //add response to total response time
        Car.tot_response =  (Car.tot_response) + (wait_t);
        if ( wait_t > Car.worst_response) {
            Car.worst_response =  wait_t;
            //
        } // endif
        //if waited past deadline
        if ( wait_t > c_timeout) {
            Car.deadline_l++;
            tracedisp("**** Controller timeout when comm with Light");
            System.out.println("**** Controller timeout when comm with Light");
            //
        } // endif
        // Check if light active
        if ( (((Car.lightM_obj.idle()) && (Car.lightM_obj.light_stat() == Car.lightM_obj.GREEN)) || (Car.lightM_obj.light_stat() == Car.lightM_obj.YELLOW)) || (Car.lightM_obj.light_stat() == Car.lightM_obj.RED)) {
            reactivate(Car.lightM_obj);
        } // endif
        tracedisp("Controller sending change signal to Light");
        //
        MainLChan.send(SendMsg);
        // Send signal to change main light
        status =  R_R;
        //Change State
        tracedisp("Controller sent red signal to main street");
        switch_sideG();
    }  // end switch_sideR
    // Send open signal to gate
    public void  switch_sideG(   ) {
        double  wait_t;
        double  simclock;
        simclock = StaticSync.get_clock();
        delay(red_wait);
        tracedisp("Controller attempt comm Light");
        //Generate random hold time
        wait_t = Comm_Light.fdraw();
        delay(wait_t);
        //Wait to comm
        Car.tot_response =  (Car.tot_response) + (wait_t);
        if ( wait_t > Car.worst_response) {
            Car.worst_response =  wait_t;
            //
        } // endif
        //If waited past deadline
        if ( wait_t > c_timeout) {
            Car.deadline_l++;
            tracedisp("**** Controller timeout when comm with Light");
            System.out.println("**** Controller timeout when comm with Light");
            //
        } // endif
        //Check if Light active
        if ( (((Car.lightS_obj.idle()) && (Car.lightS_obj.light_stat() == Car.lightS_obj.GREEN)) || (Car.lightS_obj.light_stat() == Car.lightS_obj.YELLOW)) || (Car.lightS_obj.light_stat() == Car.lightS_obj.RED)) {
            reactivate(Car.lightS_obj);
        } // endif
        tracedisp("Controller sending change signal to Light");
        //
        SideLChan.send(SendMsg);
        // Send signal to change light
        status =  R_G;
        //Change State
        tracedisp("Controller sent green signal to side street");
        resetMainSensors();
    }  // end switch_sideG
    public void  switch_mainY(   ) {
        double  wait_t;
        double  simclock;
        simclock = StaticSync.get_clock();
        tracedisp("Controller attempt comm Light");
        //Generate random hold time
        wait_t = Comm_Light.fdraw();
        delay(wait_t);
        //Wait to comm
        Car.tot_response =  (Car.tot_response) + (wait_t);
        if ( wait_t > Car.worst_response) {
            Car.worst_response =  wait_t;
            //
        } // endif
        //If waited past deadline
        if ( wait_t > c_timeout) {
            Car.deadline_l++;
            tracedisp("**** Controller timeout when comm with Light");
            System.out.println("**** Controller timeout when comm with Light");
            //
        } // endif
        //Check if Light active
        if ( (((Car.lightS_obj.idle()) && (Car.lightS_obj.light_stat() == Car.lightS_obj.GREEN)) || (Car.lightS_obj.light_stat() == Car.lightS_obj.YELLOW)) || (Car.lightS_obj.light_stat() == Car.lightS_obj.RED)) {
            reactivate(Car.lightS_obj);
        } // endif
        tracedisp("Controller sending change signal to Light");
        //
        SideLChan.send(SendMsg);
        // Send signal to change light
        status =  R_Y;
        //Change State
        tracedisp("Controller sent yellow signal to side street");
        switch_mainR();
    }  // end switch_mainY
    //
    public void  switch_mainR(   ) {
        double  wait_t;
        double  simclock;
        simclock = StaticSync.get_clock();
        delay(yellow_wait);
        tracedisp("Controller attempt comm Light");
        //Generate random hold time
        wait_t = Comm_Light.fdraw();
        delay(wait_t);
        //Wait to comm
        Car.tot_response =  (Car.tot_response) + (wait_t);
        if ( wait_t > Car.worst_response) {
            Car.worst_response =  wait_t;
            //
        } // endif
        //If waited past deadline
        if ( wait_t > c_timeout) {
            Car.deadline_l++;
            tracedisp("**** Controller timeout when comm with Light");
            System.out.println("**** Controller timeout when comm with Light");
            //
        } // endif
        //Check if Light active
        if ( (((Car.lightS_obj.idle()) && (Car.lightS_obj.light_stat() == Car.lightS_obj.GREEN)) || (Car.lightS_obj.light_stat() == Car.lightS_obj.YELLOW)) || (Car.lightS_obj.light_stat() == Car.lightS_obj.RED)) {
            reactivate(Car.lightS_obj);
        } // endif
        tracedisp("Controller sending change signal to Light");
        //
        SideLChan.send(SendMsg);
        // Send signal to change light
        status =  R_R;
        //Change State
        tracedisp("Controller sent red signal to side street");
        switch_mainG();
    }  // end switch_mainR
    // Send open signal to gate
    public void  switch_mainG(   ) {
        double  wait_t;
        double  simclock;
        simclock = StaticSync.get_clock();
        delay(red_wait);
        tracedisp("Controller attempt comm Light");
        //Generate random hold time
        wait_t = Comm_Light.fdraw();
        delay(wait_t);
        //Wait to comm
        Car.tot_response =  (Car.tot_response) + (wait_t);
        if ( wait_t > Car.worst_response) {
            Car.worst_response =  wait_t;
            //
        } // endif
        //If waited past deadline
        if ( wait_t > c_timeout) {
            Car.deadline_l++;
            tracedisp("**** Controller timeout when comm with Light");
            System.out.println("**** Controller timeout when comm with Light");
            //
        } // endif
        //Check if Light active
        if ( (((Car.lightM_obj.idle()) && (Car.lightM_obj.light_stat() == Car.lightM_obj.GREEN)) || (Car.lightM_obj.light_stat() == Car.lightM_obj.YELLOW)) || (Car.lightM_obj.light_stat() == Car.lightM_obj.RED)) {
            reactivate(Car.lightM_obj);
        } // endif
        tracedisp("Controller sending change signal to Light");
        //
        MainLChan.send(SendMsg);
        // Send signal to change light
        status =  G_R;
        //Change State
        tracedisp("Controller sent green signal to main street");
        resetSideSensors();
    }  // end switch_mainG
    public void  resetSideSensors(   ) {
        delay (0.0);
        Car.dir_chg++;
        if ( (Car.Mon_Obj.idle()) && (( (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.NONE_WAIT) || (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.WAIT_TRAFFIC)|| (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.CAR_WAIT)|| (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.CAR_IN)) )) {
            reactivate(Car.Mon_Obj);
            // reactivate monitor
            delay (0.0);
        } // endif
        C_Mchan.send(SendMsg);
        // Send signal to Monitor to reset
        if ( (Car.ss_obj.idle()) && (( (Car.ss_obj.get_status() == Car.ss_obj.CAR_DETECT) || (Car.ss_obj.get_status() == Car.ss_obj.WAITING_CAR)|| (Car.ss_obj.get_status() == Car.ss_obj.NO_CAR)) )) {
            reactivate(Car.ss_obj);
            // reactivate monitor
        } // endif
        contSchan.send(SendMsg);
        delay (0.0);
        // Send signal to side sensor to reset
    }  // end resetSideSensors
    public void  resetMainSensors(   ) {
        delay (0.0);
        Car.dir_chg++;
        if ( (Car.Mon_Obj.idle()) && (( (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.NONE_WAIT) || (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.WAIT_TRAFFIC)|| (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.CAR_WAIT)|| (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.CAR_IN) ))) {
            reactivate(Car.Mon_Obj);
            // reactivate monitor
            delay (0.0);
        } // endif
        C_Mchan.send(SendMsg);
        // Send signal to Monitor to reset
        if ( (Car.sm_obj.idle()) && (( (Car.sm_obj.get_status() == Car.sm_obj.CAR_DETECT) || (Car.sm_obj.get_status() == Car.sm_obj.WAITING_CAR)|| (Car.sm_obj.get_status() == Car.sm_obj.NO_CAR)) )) {
            reactivate(Car.sm_obj);
            // reactivate monitor
        } // endif
        contMchan.send(SendMsg);
        delay (0.0);
        // Send signal to main sensor to reset
    }  // end resetMainSensors
    public int  cont_stat(   ) {
        return status;
    }  // end cont_stat
    //
} // end class/interf Controller 
