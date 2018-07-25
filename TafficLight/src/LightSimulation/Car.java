package LightSimulation;// OOSimL v 1.1 File: Car.java, Wed Dec 02 23:36:36 2015
 
import java.util.*; 
import java.io.*; 
import java.text.*;
import psimjava.*;
import psimjava.Process;

import java.util.*; 
 public  class Car  extends psimjava.Process     {
static Scanner scan = new Scanner (System.in);
 // Globals 
 public  static double  worst_reaction = 0.0; 
  //Worst time for light to react 
 public  static double  worst_response = 0.0; 
  //worst time for light comm 
 public  static double  tot_reaction = 0.0; 
  //total reaction time 
 public  static double  tot_response = 0.0; 
  //total response time 
 public  static int  car_arr;
  //total number of arrivals 
 public  static int  light_chg; 
  // total number of light changes 
 public  static int  dir_chg; 
  //Total number of Traffic changes 
 public  static int  deadline_l = 0; 
  //Total number of deadlines missed 
 public  static double  Con_Lig_time; 
  //Max time for Cont comm with Light 
 public  static double  avg_arrival; 
  //Average car arrival time 
 public double  avg_comm = 5.85; 
  // comm interval 
 public double  Stn_Comm = 1.25; 
  // comm interval STD 
 public  static double  frstarvl_time; 
  //time of first arrival 
 public  static double  lstarvl_time; 
  //time of first arrival 
 public  static double  avg_sit; 
  //how long avg sits at sensor 
 public  static double  required_sit; 
  //reuired sit length to trip sensor 
 public  static double  simperiod; 
  // simulation interval 
 public  static double  stop_time; 
  // Time to stop sensors 
 // Channels to send/receive messages or signals 
 // 
  public  static Channel change_main; 
 //Channel to change the main street light 
  public  static Channel change_side; 
 // Channel to change the side steet light 
  public  static Channel notify_con; 
 // Channel to Notify controller of a waiting car 
  public  static Channel notify_mon; 
 // Channel to Notify monitor of a traffic change 
  public  static Channel Main_detect; 
 //Channel for main street sensor to notify monitor 
  public  static Channel Side_detect; 
 //Channel for side street sensor to notify monitor 
  public  static Channel MainChan; 
 // Channel for controller to reset main street sensor 
 // Channel for contorller to reset main street sensor 
  public  static Channel SideChan; 
 // 
 // 
 // Process objects 
  public  static Simulation sim; 
 // 
  public  static Light lightM_obj; 
 // Main light object 
  public  static Light lightS_obj; 
 // Side light object 
  public  static Controller Con_Obj; 
 // controller object 
  public  static Monitor Mon_Obj; 
 // monitor object 
  public  static Sensor sm_obj; 
 // sensor for main street 
 // sensor for side street 
  public  static Sensor ss_obj; 
 //
 // files for reporting    
  public  static PrintWriter statf; 
  public  static PrintWriter tracef;

 // 
 public Car(String  sys,String Tfile,String Sfile, double speriod, double avgAtime,double Light_dead,double sensor_stop, double req_sit,double mean_sit) {
   super(sys);
  try {
   //
   // set-up simulation
   //
   sim = new Simulation("Simple Traffic Light System");
   // Set up report files
   Simulation.set_tracef(Tfile);
   tracef = Simulation.get_tracefile();
   //
   Simulation.set_statf(Sfile);
   statf = Simulation.get_statfile();
   // set input parameters
   simperiod = speriod;
   //Simulation length in time units
   avg_arrival = avgAtime;
   //avg time takes car to arrive
   Con_Lig_time = Light_dead;
   // Deadline for controller comm with Light
   stop_time = sensor_stop;
   //Set Time for sensors to terminate
   avg_sit = mean_sit;
   //Set average time car sits at sensor
   //set sit time reuired to trip sensor
   //
   // main Process initialization
   required_sit = req_sit;
  }catch(Exception e) {
    System.out.println(e);
  }
 } // end initializer 
 // 

 public void  Main_body(   ) {
  try {
   System.out.println("running");
   String avgreact;
   String avgresp;
   String dworstreact;
   String dworstresp;
   // Round output to three decimal positions
   DecimalFormat dfmt;
   dfmt = new DecimalFormat("###.000");
   sm_obj = new Sensor("Main Street sensor", avg_arrival, avg_sit, 1);
   ss_obj = new Sensor("Side Street sensor", avg_arrival, avg_sit, 2);
   Con_Obj = new Controller("Controller", Con_Lig_time);
   lightM_obj = new Light("Main Street Light", 1);
   lightS_obj = new Light("Side Street Light", 2);
   Mon_Obj = new Monitor("Monitor");
   //Start Active objects
   Mon_Obj.start();
   lightM_obj.start();
   lightS_obj.start();
   Con_Obj.start();
   sm_obj.start();
   //
   // Channel initialization
   ss_obj.start();
   //
   change_main = new Channel("Cont-Main-Light Channel", Con_Obj, lightM_obj, avg_comm, Stn_Comm);
   change_side = new Channel("Cont-Side-Light-Channel", Con_Obj, lightS_obj, avg_comm, Stn_Comm);
   notify_con = new Channel("Monitor-Cont-Arrive-Channel", Mon_Obj, Con_Obj, avg_comm, Stn_Comm);
   notify_mon = new Channel("Cont-Monitor-Switch-Channel", Con_Obj, Mon_Obj, avg_comm, Stn_Comm);
   Main_detect = new Channel("MainSens-Monitor-Arrival-Channel", sm_obj, Mon_Obj, avg_comm, Stn_Comm);
   Side_detect = new Channel("SideSens-Monitor-Arrival-Channel", ss_obj, Mon_Obj, avg_comm, Stn_Comm);
   MainChan = new Channel("Cont-Sens-Go-Chan", Con_Obj, sm_obj, avg_comm, Stn_Comm);
   SideChan = new Channel("Cont-Sens-Stop-Chan", Con_Obj, ss_obj, avg_comm, Stn_Comm);
   statf.println("Controller timeout: " +
           Con_Lig_time);
   statf.flush();
   System.out.println("Controller timeout: " +
           Con_Lig_time);
   sim.start_sim(simperiod);
   // start simulation
   tracedisp("End of Simulation");
   //
   System.out.println("----------------------------------------------------------------------");
   System.out.println("First Car arrived at time: " +
           frstarvl_time);
   System.out.println("Last Car arrived at time: " +
           lstarvl_time);
   System.out.println("Total cars that arrived: " +
           car_arr);
   System.out.println("Number of Traffic Changes: " +
           dir_chg);
   System.out.println("Number of Light Changes: " +
           light_chg);
   avgreact = dfmt.format((tot_reaction) / ((light_chg)));
   System.out.println("Average system reaction time: " +
           avgreact);
   avgresp = dfmt.format((tot_response) / ((light_chg)));
   System.out.println("Average system response time: " +
           avgresp);
   dworstreact = dfmt.format(worst_reaction);
   System.out.println("Worst reaction time: " +
           dworstreact);
   dworstresp = dfmt.format(worst_response);
   System.out.println("Worst response time: " +
           dworstresp);
   System.out.println("Number of deadlines missed when Controller comm " +
           "with Light: " +
           deadline_l);
   System.out.println("----------------------------------------------------------------------");
   statf.println("First Car arrived at time: " +
           frstarvl_time);
   statf.flush();
   statf.println("Last Car arrived at time: " +
           lstarvl_time);
   statf.flush();
   statf.println("Total cars that arrived: " +
           car_arr);
   statf.flush();
   statf.println("Number of Traffic Changes: " +
           dir_chg);
   statf.flush();
   statf.println("Number of Light Changes: " +
           light_chg);
   statf.flush();
   statf.println("Average system reaction time: " +
           avgreact);
   statf.flush();
   statf.println("Average system response time: " +
           avgresp);
   statf.flush();
   statf.println("Worst reaction time: " +
           dworstreact);
   statf.flush();
   statf.println("Worst response time: " +
           dworstresp);
   statf.flush();
   statf.println("Number of deadlines missed when Controller comm " +
           "with Light: " +
           deadline_l);
   statf.flush();

   sim=null;

   Mon_Obj.join();
   lightM_obj.join();
   lightS_obj.join();
   Con_Obj.join();
   sm_obj.join();
   this.join();
  }catch (Exception e){
   System.out.println(e);
  }

 }  // end Main_body
} // end class/interf Car 
