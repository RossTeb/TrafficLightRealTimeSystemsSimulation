package LightSimulation;// OOSimL v 1.1 File: Sensor.java, Wed Dec 02 23:37:51 2015
 
import java.util.*; 
import psimjava.*; 
 public  class Sensor  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 private double  arr_mean; 
  //avg arrival time 
 private double  activity_mean; 
  // avg length of time a car waits at the sensor 
 private int  status; 
  //current state of sensor 
 private int  s_type; 
  //Street type eg:Main street or side street 
 private double  shortest_wait; 
  //shortest time car will sit at sensor 
 private double  longest_wait; 
  //longest time car will sit at sensor 
 private String  Sensor_Name; 
  //Name of sensor 
  private Erand arr_period; 
 // Random car interarrival period 
  private Urand activity_period; 
 //Period of time cars will sit at sensor 
  private Channel Rchannel; 
 //Reset channel from controller 
  private Channel arriveChannel; 
 //channel to notify monitor of arrival 
  private Message recievemsg; 
  private Message sendmsg; 
 //Defines states 
 final public int  NO_CAR = 1; 
  final public int  CAR_DETECT = 2; 
  final public int  WAITING_CAR = 3; 
  //Streets 
 final public int  MAIN_STREET = 1; 
  final public int  SIDE_STREET = 2; 
  //Method to get status of sensor 
 public int  get_status(   ) { 
return status; 
 }  // end get_status 
 public Sensor(  String  sname, double  arrive_mean, double  duration_mean, int  street) { 
super(sname); 
 //Set name of sensor 
Sensor_Name =  sname;
arr_mean =  arrive_mean;
activity_mean =  duration_mean;
 //Sets wait period from avg wait time 
shortest_wait =  (activity_mean) * (0.75);
longest_wait =  (activity_mean) * (2.0);
arr_period = new Erand(arr_mean);
activity_period = new Urand(shortest_wait, longest_wait);
status =  NO_CAR;
 //initialize which street the sensor should monitor 
s_type =  street;
System.out.println("Sensor initiated: "+ 
Sensor_Name);
 } // end initializer 
 // 
 public void  Main_body(   ) { 
 double  arrival_time; 
  //time until car arrival 
 double  wait_duration; 
  //period car triggers sensor 
 double  simclock; 
  simclock = StaticSync.get_clock();
sendmsg = new Message("1");
 //Sets channel based off street 
 if ( s_type == MAIN_STREET) { 
tracedisp((Sensor_Name) + (" suspends until red light"));
Rchannel =  Car.MainChan;
arriveChannel =  Car.Main_detect;
 //Suspends until reactivated by controller 
wait_traffic(); 
 }
 else {
Rchannel =  Car.SideChan;
arriveChannel =  Car.Side_detect;
 } // endif 
 while ( simclock < Car.stop_time ) { 
tracedisp((Sensor_Name) + (" waits for car to arrive"));
 //Generates arrival time of car 
 arrival_time = arr_period.fdraw(); 
 //Generates car stop time 
 wait_duration = activity_period.fdraw(); 
 // wait for car to arrive 
  delay(arrival_time);
 simclock = StaticSync.get_clock();
status =  WAITING_CAR;
 //If car triggered sensor for the required length of time signal monitor 
 if ( (simclock < Car.stop_time) && (wait_duration >= Car.required_sit)) { 
car_arrival(); 
 } // endif 
 simclock = StaticSync.get_clock();
 } // endwhile 
tracedisp("Terminate Sensor process");
  terminate();
 }  // end Main_body 
 // 
 public void  car_arrival(   ) { 
tracedisp((Sensor_Name) + (" detected waiting car"));
 //Check if monitor is active 
 delay (0.0);
 if ( (Car.Mon_Obj.idle()) && (( (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.NONE_WAIT) || (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.CAR_IN)|| (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.WAIT_TRAFFIC)|| (Car.Mon_Obj.mon_stat() == Car.Mon_Obj.CAR_WAIT)) )) {
 reactivate(Car.Mon_Obj);
 //reactivates  monitor 
 delay (0.0);
 } // endif 
arriveChannel.send(sendmsg); 
 //Signal arrival to monitor 
status =  CAR_DETECT;
tracedisp((Sensor_Name) + (" sent signal to Monitor"));
 //method to suspend until controller comm 
wait_traffic(); 
 }  // end car_arrival 
 public void  wait_traffic(   ) { 
status =  CAR_DETECT;
 while ( status == CAR_DETECT ) { 
 //if Reset channel is rdy for comm attempt comm otherwise suspend 
 if ( Rchannel.input_ready()) { 
recievemsg =  Rchannel.receive();
 //receive reset signal from controller 
tracedisp((Sensor_Name) + (" received reset signal from Controller "));
status =  NO_CAR;
 //Reset state and restart 
 }
 else {
tracedisp((Sensor_Name) + (" suspends"));
 deactivate(this);
 //Monitor suspends 
tracedisp((Sensor_Name) + (" reactivated by Controller"));
 } // endif 
 delay (0.0);
 //allow other threads to run 
 } // endwhile 
 }  // end wait_traffic 
} // end class/interf Sensor 
