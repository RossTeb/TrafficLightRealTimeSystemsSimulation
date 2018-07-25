package LightSimulation;// OOSimL v 1.1 File: Channel.java, Wed Dec 02 23:36:33 2015
 
import java.util.*; 
import psimjava.*; 
/** 
description
 Process synchronous communication in OOSimL
 Sender process directly communicates with a receiver process
 to transfer a message through a channel.
 
 A channel is implemented as a waitq object for the rendezvous.
 A channel is defined in the model for every each pair of processes.
 (C) J. Garrido, 2013. Updated Nov. 2014
  File: Channel.osl
  This class defines a communication channel
  It is used by pairs of processes that need to communicate synchronously
*/
 public  class Channel     {
static Scanner scan = new Scanner (System.in);
 private String  chname; 
  // channel name 
 private double  comm_int; 
  // comm interval  
 private double  wait_int; 
  // wait interval 
  private Waitq synchan; 
 // communication link 
  private psimjava.Process send_proc; 
 // process that sends message 
  private psimjava.Process rec_proc; 
 // process that receives message 
  private Message mess; 
 // message 
  private Normal gen_comm; 
 // generate random time interval 
 public Channel(  String  lname, psimjava.Process psend, psimjava.Process prec, double  mean_comm, double  std_comm) { 
chname =  lname;
send_proc =  psend;
rec_proc =  prec;
 // the actual communication link 
synchan = new Waitq(lname + chname, 1);
gen_comm = new Normal("Comm interval " + chname, mean_comm, std_comm);
 } // end initializer 
 //   
 public void  send(  Message msg ) { 
 double  simclock; 
  double  startw; 
  // generate random value comm_int from gen_comm 
mess =  msg;
 if ( mess ==  null ) { 
System.out.println("message to send is null");
 } // endif 
 simclock = StaticSync.get_clock();
startw =  simclock;
 synchan.qwait();
 // communicate 
 simclock = StaticSync.get_clock();
wait_int =  (simclock) - (startw);
 // display chname, " end send" 
 }  // end send 
 // 
 public Message receive(   ) { 
 double  startw; 
  double  comm_int; 
  double  simclock; 
  // display chname, " ready to receive message from ", send_proc.get_name() 
 simclock = StaticSync.get_clock();
startw =  simclock;
 // start time      
send_proc = (psimjava.Process) synchan.coopt();
 simclock = StaticSync.get_clock();
wait_int =  (simclock) - (startw);
 if ( mess ==  null ) { 
System.out.println("Chan "+ 
chname+ 
" mess is null");
 } // endif 
 comm_int = gen_comm.fdraw(); 
 if ( rec_proc != null ) 
     rec_proc.delay(comm_int);
 // display "end receive" 
 StaticSync.reactivate(send_proc);
return mess; 
 }  // end receive 
 // 
 public String  get_chname(   ) { 
return chname; 
 // 
 }  // end get_chname 
 // get the communication interval 
 public double  get_comm_int(   ) { 
return comm_int; 
 }  // end get_comm_int 
 // 
 public double  get_wait(   ) { 
return wait_int; 
 // 
 }  // end get_wait 
 // sender process ready? 
 public boolean  input_ready(   ) { 
 if ( synchan.length() > 0) { 
return  true ; 
 }
 else {
return  false ; 
 } // endif 
 // 
 }  // end input_ready 
 // receiver process ready? 
 public boolean  output_ready(   ) { 
 if ( synchan.lengthm() > 0) { 
return  true ; 
 }
 else {
return  false ; 
 } // endif 
 }  // end output_ready 
 // 
} // end class/interf Channel 
