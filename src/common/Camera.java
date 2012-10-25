package common;

import java.io.Serializable;
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

}
