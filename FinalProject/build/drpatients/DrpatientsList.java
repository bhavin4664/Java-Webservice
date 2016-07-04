package drpatients;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlElementWrapper; 
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DrpatientsList")
public class DrpatientsList {
    private List<Patient> drpats; 
    private AtomicInteger drpatsId;

    public DrpatientsList() { 
	drpats = new CopyOnWriteArrayList<Patient>(); 
	drpatsId = new AtomicInteger();
    }

    @XmlElement 
    @XmlElementWrapper(name = "drpatients") 
    public List<Patient> getDrpatients() { 
	return this.drpats;
    } 
    public void setDrpatients(List<Patient> drpats) { 
	this.drpats = drpats;
    }

    @Override
    public String toString() {
	String s = "";
	for (Patient p : drpats) s += p.toString();
	return s;
    }

    public List<Patient> find(int id) {
	List<Patient> dpat=new ArrayList<Patient>();
	for (Patient p : drpats) {
	    if (p.getIddr() == id) {
		dpat.add(p);
		//break;
	    }
	}	
	return dpat;
    }
    public int add(int drIdval,String numPat,String drwho,String whoPat, String whatId) {
		int id = drpatsId.incrementAndGet();
		Patient dp = new Patient();
		dp.setDrWho(drwho);
		dp.setWhoPat(whoPat);
		dp.setWhatId(whatId);
		dp.setId(id);
		dp.setIddr(drIdval);
		dp.setNumpat(numPat);
		drpats.add(dp);
	
		return id;
    }
}