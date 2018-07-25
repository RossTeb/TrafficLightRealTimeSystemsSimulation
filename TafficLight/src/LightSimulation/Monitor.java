package LightSimulation;// OOSimL v 1.1 File: Monitor.java, Wed Dec 02 23:36:20 2015
 
import java.util.*; 
import java.io.*; 
import psimjava.*; 
 public  class Monitor  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private int  num_cars; 
  // number of Cars currently in area 
 //holds the state of monitor 
 private int  status; 
  //current traffic direction: True = Main street is Green / False = Side street is Green 
 private boolean  Cur_Traf_Dir; 
  //Determines if is first car 
 private boolean  frst_arvl; 
   private Channel Mainarrival; 
 //Channel for main street sensor to notify monitor 
  private Channel Sidearrival; 
 //Channel for side sensor to nofity monitor 
  private Channel C_Mchan; 
 //Channel to notify controller to change direction 
  private Channel M_Cchan; 
 //Controller to verify direction change 
  private Message ReceiveMsg; 
  private Message SendMsg; 
 final public int  NONE_WAIT = 1; 
  final public int  CAR_WAIT = 2; 
  final public int  CAR_IN = 3; 
  final public int  WAIT_TRAFFIC = 4; 
  final public boolean  MAIN_GREEN =  true ; 
  final public boolean  SIDE_GREEN =  false ; 
  public int  mon_stat(   ) { 
return status; 
 // status of monitor process 
 }  // end mon_stat 
 // 
 public Monitor(  String  mname) { 
super(mname); 
 //set monitor name 
num_cars =  0;
status =  0;
SendMsg = new Message("1");
Cur_Traf_Dir =  MAIN_GREEN;
 //Sets main street green first 
frst_arvl =   true ;
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 double  simclock; 
 status =  NONE_WAIT;
 //Create channels 
Mainarrival =  Car.Main_detect;
Sidearrival =  Car.Side_detect;
C_Mchan =  Car.notify_con;
M_Cchan =  Car.notify_mon;
 //Set total number of cars to 0 
Car.car_arr =  0;
 simclock = StaticSync.get_clock();
 while ( simclock <= Car.simperiod ) { 
car_arrive(); 
 //Wait for sensor to comm 
 delay (0.0);
awaitChange(); 
 // Wait for reset signal from controller 
 simclock = StaticSync.get_clock();
 } // endwhile
  terminate();
 }  // end Main_body 
 // 
 public void  car_arrive(   ) { 
 //wait for sensor comm 
 while ( status == NONE_WAIT ) { 
 //if main stree is green attempt to receive signal from side street sensor, otherwise attempt comm with main sensor 
 if ( Cur_Traf_Dir == MAIN_GREEN) { 
 // checks if sensor is redy to comm else suspends 
 if ( Sidearrival.input_ready()) { 
 // comm with side street sensor 
first_side_arrival(); 
 }
 else {
tracedisp("Monitor suspends while waiting for side street sensor");
 deactivate(this);
 } // endif 
 delay (0.0);
 } // 
else if ( Cur_Traf_Dir == SIDE_GREEN) { 
 //checks is main street sensor is redy for comm otherwise suspends 
 if ( Mainarrival.input_ready()) { 
 //attempt comm with main street sensor 
first_main_arrival(); 
 }
 else {
tracedisp("Monitor suspends while waiting for main street sensor");
 deactivate(this);
 } // endif 
 delay (0.0);
 } // endif 
 // 
 } // endwhile 
 // Check if controller active
  tracedisp("Monitor attempting comm with Controller");
 if ( ((Car.Con_Obj.idle()) && (Car.Con_Obj.cont_stat() == Car.Con_Obj.G_R)) || (Car.Con_Obj.cont_stat() == Car.Con_Obj.R_G)|| (Car.Con_Obj.cont_stat() == Car.Con_Obj.R_Y)|| (Car.Con_Obj.cont_stat() == Car.Con_Obj.Y_R)|| (Car.Con_Obj.cont_stat() == Car.Con_Obj.R_R)|| (Car.Con_Obj.cont_stat() == Car.Con_Obj.SWITCH_SIDE)) {
 //reactivate controller if idle
  tracedisp("Monitor reactivated controller");
 reactivate(Car.Con_Obj);
C_Mchan.send(SendMsg); 
 // send signal  
tracedisp("Monitor sent change signal to Controller");
  //allows cont thread to run
  delay (0.0);
status =  WAIT_TRAFFIC;
 } // endif 
status =  WAIT_TRAFFIC;
 }  // end car_arrive 
 //method to comm with side street sensor 
 public void  first_side_arrival(   ) { 
 double  simclock; 
 ReceiveMsg =  Sidearrival.receive();
 //Get message from Sensor 
 num_cars++;
 // increment number of cars 
 simclock = StaticSync.get_clock();
 //if this is first car set first car arrival time 
 if ( frst_arvl ==  true ) { 
Car.frstarvl_time =  simclock;
frst_arvl =   false ;
 }
 else {
 //updates last car arrival time 
Car.lstarvl_time =  simclock;
 } // endif 
status =  CAR_WAIT;
 //change state to cars wating 
tracedisp("Monitor received signal from Sensor ");
tracedisp("Number of cars is: "+ 
num_cars);
 Car.car_arr++;
 //increments total number of cars  
 }  // end first_side_arrival 
 //  
 public void  first_main_arrival(   ) { 
 double  simclock; 
 ReceiveMsg =  Mainarrival.receive();
 //Get message from Sensor 
 num_cars++;
 // increment number of cars 
 simclock = StaticSync.get_clock();
 //if first car set first car arrival time 
 if ( frst_arvl ==  true ) { 
Car.frstarvl_time =  simclock;
frst_arvl =   false ;
 }
 else {
 //updates last car arrival time 
Car.lstarvl_time =  simclock;
 } // endif 
status =  CAR_WAIT;
 //set state to car waiting 
tracedisp("Monitor received signal from Sensor ");
tracedisp("Number of cars is: "+ 
num_cars);
 Car.car_arr++;
 //increments total number of cars 
 }  // end first_main_arrival 
 public void  awaitChange(   ) { 
 //Monitor Waits for controller to notify of traffic change 
 while ( status == WAIT_TRAFFIC ) { 
 //If current traffic direction if mainstreet attempt comm with side sensor or controller 
 if ( Cur_Traf_Dir == MAIN_GREEN) { 
 //checks if sensor or controller rdy for comm otherwise suspends 
 if ( Sidearrival.input_ready()) { 
ReceiveMsg =  Sidearrival.receive();
tracedisp("Monitor received signal from Sensor");
status =  CAR_IN;
 //method to increment number of cars 
car_in(); 
 } // 
else if ( M_Cchan.input_ready()) { 
 //receive reset msg from controller 
ReceiveMsg =  M_Cchan.receive();
tracedisp("Monitor received signal from Controller");
status =  NONE_WAIT;
 //Change current traffic direction- Begin waiting for adjacent street sensor 
tracedisp(("Main street is green: ") + (Cur_Traf_Dir));
Cur_Traf_Dir =   ! (Cur_Traf_Dir);
  num_cars=0;
tracedisp(("Main street is green: ") + (Cur_Traf_Dir));
 }
 else {
tracedisp("Monitor suspends ");
 deactivate(this);
 //suspends monitor if no comm was available 
 } // endif 
 //Allows Sensor thread to continue 
 delay (0.0);
 //If current traffic direction if side street attempt comm with main sensor or controller 
 } // 
else if ( Cur_Traf_Dir == SIDE_GREEN) { 
 //checks if sensor or controller rdy for comm otherwise suspends 
 if ( Mainarrival.input_ready()) { 
ReceiveMsg =  Mainarrival.receive();
tracedisp("Monitor received signal from Sensor");
status =  CAR_IN;
 //method to increment number of cars 
car_in(); 
 } // 
else if ( M_Cchan.input_ready()) { 
 //receive reset sensor from controller 
ReceiveMsg =  M_Cchan.receive();
tracedisp("Monitor received signal from Controller");
status =  NONE_WAIT;
 //Change current traffic direction- Begin waiting for adjacent street sensor 
tracedisp(("Side street is green: ") + (Cur_Traf_Dir));
Cur_Traf_Dir =   ! (Cur_Traf_Dir);
  num_cars=0;
tracedisp(("Side street is green: ") + (Cur_Traf_Dir));
 }
 else {
 deactivate(this);
 //suspends monitor if no comm was available 
 } // endif 
 delay (0.0);
 //Allows Sensor thread to continue 
 } // endif 
 } // endwhile 
 }  // end awaitChange 
 // 
 public void  car_in(   ) { 
 //if not the first car 
 if ( num_cars > 1) { 
 num_cars++;
status =  WAIT_TRAFFIC;
tracedisp("Another car has arrived, Cars waiting is now: "+ 
num_cars);
 // increment total number of cars 
 Car.car_arr++;
 //Attempts comm with controller if this is the first arrival since last reset 
 } // 
else if ( ((Car.Con_Obj.idle()) && (Car.Con_Obj.cont_stat() == Car.Con_Obj.G_R)) || (Car.Con_Obj.cont_stat() == Car.Con_Obj.R_G)|| (Car.Con_Obj.cont_stat() == Car.Con_Obj.R_Y)|| (Car.Con_Obj.cont_stat() == Car.Con_Obj.Y_R)|| (Car.Con_Obj.cont_stat() == Car.Con_Obj.R_R)|| (Car.Con_Obj.cont_stat() == Car.Con_Obj.SWITCH_SIDE)) {
 //reactivates controller thread 
 reactivate(Car.Con_Obj);
 //allows controller thread to run 
 delay (0.0);
C_Mchan.send(SendMsg); 
 // send signal  
tracedisp("Monitor sent change signal to Controller");
status =  WAIT_TRAFFIC;
 Car.car_arr++;
 // increment total number of cars 
 } // endif 
 }  // end car_in 
} // end class/interf Monitor 
