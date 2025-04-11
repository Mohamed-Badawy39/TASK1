// PNode.java
public class PNode {
    CriticalPatient patient;
    PNode next;

    public PNode(CriticalPatient patient) {
        this.patient = patient;
        this.next = null;
    }
}
