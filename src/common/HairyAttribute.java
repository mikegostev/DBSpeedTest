package common;

import java.io.Serializable;

public class HairyAttribute implements Serializable
{

 private static final long serialVersionUID = 1L;

 private HairyClass cls;
 private Object value;
 
 public HairyAttribute(HairyClass cls, Object value)
 {
  super();
  this.cls = cls;
  this.value = value;
 }

 public HairyClass getCls()
 {
  return cls;
 }

 public void setCls(HairyClass cls)
 {
  this.cls = cls;
 }

 public Object getValue()
 {
  return value;
 }

 public void setValue(Object value)
 {
  this.value = value;
 }
 
 
}
