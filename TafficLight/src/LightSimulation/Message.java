package LightSimulation;// OOSimL v 1.1 File: Message.java, Wed Dec 02 23:36:23 2015
 
import java.util.*; 
import psimjava.*; 
/** 
description
  Message transfer with synchronous communication 
  A sender process directly communicates with a receiver process
  to transfer a message through a channel.
    
  (C) J. Garrido, Updated Nov. 2014
  File: Message.osl
*/
 public  class Message     {
static Scanner scan = new Scanner (System.in);
 private  static int  seq_num = 0; 
  // message number 
 private int  mess_num; 
  private double  time_stamp; 
  // when message was created 
 private String  mes_data; 
  // data contents of message 
 public Message(  String  ldata) { 
mes_data =  ldata;
time_stamp =  0.0;
mess_num =  1;
 } // end initializer 
 // 
 //get message number 
 public int  get_messnum(   ) { 
return mess_num; 
 }  // end get_messnum 
 // 
 public String  get_data(   ) { 
return mes_data; 
 // 
 }  // end get_data 
 // set time stamp 
 public void  set_time(  double  ltime ) { 
time_stamp =  ltime;
 }  // end set_time 
 // 
 public double  get_time(   ) { 
return time_stamp; 
 }  // end get_time 
 //    
} // end class/interf Message 
