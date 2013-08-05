package common;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Camera implements Serializable
{

 private static final long serialVersionUID = 1L;

 private String          id;
 private String          country;
 private String          city;
 private List<LogRecord> log;

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }

 public String getCountry()
 {
  return country;
 }

 public void setCountry(String country)
 {
  this.country = country;
 }

 public String getCity()
 {
  return city;
 }

 public void setCity(String city)
 {
  this.city = city;
 }

 public List<LogRecord> getLog()
 {
  return log;
 }

 public void setLog(List<LogRecord> log)
 {
  this.log = log;
 }
 
 public void addLogRecord( LogRecord lr )
 {
  if( log == null )
   log = new ArrayList<>();
   
  log.add(lr);
 }
 
 public static void save( ByteBuffer buf, Camera cam )
 {
  buf.putInt(cam.id.length());
  buf.put(cam.id.getBytes());

  buf.putInt(cam.country.length());
  buf.put(cam.country.getBytes());

  buf.putInt(cam.city.length());
  buf.put(cam.city.getBytes());

  buf.putInt(cam.log.size());
  
  for( int i=0; i<cam.log.size(); i++)
  {
   buf.putLong( cam.log.get(i).getTime() );
   buf.putInt(cam.log.get(i).getEventHash().length());
   buf.put(cam.log.get(i).getEventHash().getBytes());
  }
   
 }
 
 public static Camera load( ByteBuffer buf )
 {
  byte[] strbuf = new byte[ 1024 ];
  
  Camera cam = new Camera();
  
  int len = buf.getInt();
  
  buf.get(strbuf,0, len);
  
  cam.setId( new String(strbuf,0, len) );
  
  
  len = buf.getInt();
  
  buf.get(strbuf,0, len);
  
  cam.setCountry( new String(strbuf,0, len) );
  
  
  len = buf.getInt();
  
  buf.get(strbuf,0, len);
  
  cam.setCity( new String(strbuf,0, len) );
  
  
  int loglen = buf.getInt();

  for( int i=0; i < loglen; i++)
  {
   LogRecord lr = new LogRecord();
   
   lr.setTime(buf.getLong());
   
   len = buf.getInt();
   buf.get(strbuf,0, len);
   lr.setEventHash( new String(strbuf,0, len) );

   cam.addLogRecord(lr);
  }
  
  return cam;
 }

}
