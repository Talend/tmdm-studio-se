// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation ��1.1.2_01������� R40��
// Generated source version: 1.1.2

package com.amalto.workbench.webservices;


public class WSProcessTaskInstance {
    protected java.lang.String uuid;
    protected java.lang.String status;
    protected java.lang.String candidates;
    
    public WSProcessTaskInstance() {
    }
    
    public WSProcessTaskInstance(java.lang.String uuid, java.lang.String status, java.lang.String candidates) {
        this.uuid = uuid;
        this.status = status;
        this.candidates = candidates;
    }
    
    public java.lang.String getUuid() {
        return uuid;
    }
    
    public void setUuid(java.lang.String uuid) {
        this.uuid = uuid;
    }
    
    public java.lang.String getStatus() {
        return status;
    }
    
    public void setStatus(java.lang.String status) {
        this.status = status;
    }
    
    public java.lang.String getCandidates() {
        return candidates;
    }
    
    public void setCandidates(java.lang.String candidates) {
        this.candidates = candidates;
    }
}