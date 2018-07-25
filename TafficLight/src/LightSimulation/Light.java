package LightSimulation;// OOSimL v 1.1 File: Light.java, Wed Dec 02 23:36:26 2015
 
import java.util.*; 
import java.io.*; 
import psimjava.*; 
 public  class Light  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private int  status; 
  //Current State of light 
 private String  Light_Name; 
  //Light Name 
  private Message ReceiveMsg; 
  private Message SendMsg; 
  private Channel changechan; 
 //Channel to comm with controller 
 final public int  GREEN = 1; 
  final public int  RED = 2; 
  final public int  YELLOW = 3; 
  public Light(  String  lname, int  lstate) { 
super(lname); 
Light_Name =  lname;
 //Set inital state  
 if ( lstate == GREEN) { 
status =  GREEN;
 } // 
else if ( lstate == RED) { 
status =  RED;
 } // endif 
 } // end initializer 
 // 
 public int  light_stat(   ) { 
return status; 
 //return light status 
 }  // end light_stat 
 // 
 public void  Main_body(   ) { 
 double  simclock; 
  double  r_time; 
  //used to calculate reaction time - holds simulation time 
 //Sets channel based on inital state 
 if ( status == GREEN) { 
System.out.println((Light_Name) + (" is Green"));
tracedisp((Light_Name) + (" is Green"));
changechan =  Car.change_main;
 } // 
else if ( status == RED) { 
System.out.println((Light_Name) + (" is Red"));
tracedisp((Light_Name) + (" is Red"));
changechan =  Car.change_side;
 } // endif 
SendMsg = new Message("1");
 simclock = StaticSync.get_clock();
 while ( simclock <= Car.simperiod ) { 
tracedisp((Light_Name) + (" waiting for change signal"));
 //Attempt comm or suspend 
 if ( changechan.input_ready()) { 
 //if comm available, recieve signal and change light 
 simclock = StaticSync.get_clock();
ReceiveMsg =  changechan.receive();
tracedisp((Light_Name) + (" received change signal "));
 //call method to change light 
change_signal(); 
 //increments total number of light changes 
 Car.light_chg++;
 r_time = StaticSync.get_clock();
 //sum of reaction time 
Car.tot_reaction =  (Car.tot_reaction) + (( (r_time) - (simclock)) );
 //if new worst reaction time, set  
 if ( Car.worst_reaction < ( (r_time) - (simclock)) ) { 
Car.worst_reaction =  ( (r_time) - (simclock)) ;
 } // endif 
 delay (0.0);
 }
 else {
tracedisp((Light_Name) + (" suspends until controller signal"));
 deactivate(this);
tracedisp((Light_Name) + (" reactivated from controller signal"));
 } // endif 
 simclock = StaticSync.get_clock();
 } // endwhile
  terminate();
 }  // end Main_body 
 // 
 public void  change_signal(   ) { 
 boolean  alter; 
  String  color; 
  //used to determine if signal changed properly 
alter =   false ;
 //Changes to next light color based on current state 
 if ( status == GREEN) { 
status =  YELLOW;
alter =   true ;
 } // 
else if ( status == YELLOW) { 
alter =   true ;
status =  RED;
 } // 
else if ( status == RED) { 
alter =   true ;
status =  GREEN;
 } // endif 
 //Records light change 
 if ( alter ==  true ) { 
 //call method to a string of the current light state 
color =  get_status_string();
tracedisp((Light_Name) + (" has changed to ")+ 
color);
System.out.println((Light_Name) + (" has changed to ")+ 
color);
 } // endif 
 }  // end change_signal 
 public String  get_status_string(   ) { 
 if ( status == GREEN) { 
return "Green"; 
 } // 
else if ( status == RED) { 
return "Red"; 
 } // 
else if ( status == YELLOW) { 
return "Yellow"; 
 }
 else {
return "*Unable to get color*"; 
 } // endif 
 }  // end get_status_string 
 // 
} // end class/interf Light 
