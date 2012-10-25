package common;

import java.io.Serializable;

public class LogRecord implements Serializable
{
 private static final long serialVersionUID = 1L;

 private long              time;
 private String            eventHash;

 public long getTime()
 {
  return time;
 }

 public void setTime(long time)
 {
  this.time = time;
 }

 public String getEventHash()
 {
  return eventHash;
 }

 public void setEventHash(String hash)
 {
  this.eventHash = hash;
 }

}
