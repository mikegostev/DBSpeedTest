package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HairyObject implements Serializable
{

 private static final long serialVersionUID = 1L;


 private String id;
 

 private List<HairyAttribute> attrs=new ArrayList<>(12);
 
 public void addAttribute( HairyAttribute at )
 {
  attrs.add(at);
 }
 
 public List<HairyAttribute> getAttributes()
 {
  return attrs;
 }

 public String getId()
 {
  return id;
 }

 public void setId(String id)
 {
  this.id = id;
 }
}
