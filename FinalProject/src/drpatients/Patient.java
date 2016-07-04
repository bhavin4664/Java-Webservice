package drpatients;

import javax.xml.bind.annotation.XmlRootElement; 
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "drpatients")
public class Patient implements Comparable<Patient> {
    private String whoPat;   // Who is Patient
    private String whatId;  // his/her Insurance card number
	private String drwho; // Who is doctor
    private int    id;    // identifier used as lookup-key
	private int idDr; //doctor Id
    private String numPat; //number of patients Doctor has
	
    public Patient() { }

    @Override
    public String toString() {
	return drwho+String.format("%2d: ", id-1) + whoPat + " ==> " + whatId +"\n";
    }
    
    //** properties
    public void setWhoPat(String whoPat) {
	this.whoPat = whoPat;
    }
    @XmlElement
    public String getWhoPat() {
	return this.whoPat;
    }

	public void setDrWho(String drwho) {
	this.drwho = drwho;
    }
    @XmlElement
    public String getDrWho() {
	return this.drwho;
    }
	
    public void setWhatId(String whatId) {
	this.whatId = whatId;
    }
    @XmlElement
    public String getWhatId() {
	return this.whatId;
    }

	
    public void setId(int id) {
	this.id = id;
    }
    @XmlElement
    public int getId() {
	return this.id;
    }

	public void setIddr(int idDr) {
	this.idDr = idDr;
    }
    @XmlElement
    public int getIddr() {
	return this.idDr;
    }
	//numPat
	public void setNumpat(String numPat) {
	this.numPat = numPat;
    }
    @XmlElement
    public String getNumpat() {
	return this.numPat;
    }
	
	
    // implementation of Comparable interface
    public int compareTo(Patient other) {
	return this.id - other.id;
    }	
}