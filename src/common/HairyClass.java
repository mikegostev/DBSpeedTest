package common;

import java.io.Serializable;

public class HairyClass implements Serializable
{

 private static final long serialVersionUID = 1L;


 public enum TYPE
 {
  INT,
  REAL,
  STRING,
  BOOLEAN
 }
 
 private String name;
 private TYPE type;
 
 
 public HairyClass(String name, TYPE type)
 {
  super();
  this.name = name;
  this.type = type;
 }


 public String getName()
 {
  return name;
 }


 public void setName(String name)
 {
  this.name = name;
 }


 public TYPE getType()
 {
  return type;
 }


 public void setType(TYPE type)
 {
  this.type = type;
 }
 
 
}
