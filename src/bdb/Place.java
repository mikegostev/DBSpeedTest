package bdb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Place implements Serializable
{
 private static final long serialVersionUID = 1L;

 private String                city;
 private String                country;
 private Collection<LogRecord> log;

 public String getCity()
 {
  return city;
 }

 public void setCity(String city)
 {
  this.city = city;
 }

 public String getCountry()
 {
  return country;
 }

 public void setCountry(String country)
 {
  this.country = country;
 }

 public Collection<LogRecord> getLog()
 {
  return log;
 }

 public void setLog(Collection<LogRecord> log)
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
